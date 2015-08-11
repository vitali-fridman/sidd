// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;

import com.vontu.ramindex.util.IdxRdxMetadata;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.DataInputStream;
import java.util.logging.Logger;

final class IdxFileReader
{
    private static final Logger logger;
    private static final int READ_BUFFER_SIZE = 1048576;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_BYTE = 1;
    private static final int TERM_SIZE = 20;
    private static final int SIZE_OF_IDX_FILE_CELL = 25;
    private boolean _canRead;
    private long _currentPosition;
    private final DataInputStream _dataStream;
    private final Configuration _configuration;
    private int _estimatedNumCommonTerms;
    private int _estimatedNumCommonTermCells;
    private final File _filePath;
    private final SubIndexSpec _spec;
    private final FileChannel _fileChannel;
    private Cell _prevCell;
    
    IdxFileReader(final File idxFile, final SubIndexSpec spec, final Configuration configuration) throws IOException {
        this._estimatedNumCommonTerms = -1;
        this._estimatedNumCommonTermCells = -1;
        this._filePath = idxFile;
        this._spec = spec;
        this._configuration = configuration;
        final FileInputStream fileInputStream = new FileInputStream(this._filePath);
        fileInputStream.getChannel().position(this._spec.startIdxFilePosition);
        this._fileChannel = fileInputStream.getChannel();
        this._prevCell = null;
        this._dataStream = new DataInputStream(new BufferedInputStream(fileInputStream, 1048576));
        this._currentPosition = this._spec.startIdxFilePosition;
        this._canRead = true;
    }
    
    long getCurrentFilePosition() {
        return this._currentPosition;
    }
    
    Cell readCell() throws IOException {
        if (!this._canRead) {
            throw new IllegalStateException("Must rewind IDX file reader before attemping to read again.");
        }
        byte[] term = new byte[0];
        int row = 0;
        int col = 0;
        while (true) {
            term = new byte[20];
            row = 0;
            col = 0;
            try {
                this._dataStream.readFully(term);
                row = this._dataStream.readInt();
                col = this._dataStream.readByte();
            }
            catch (EOFException e) {
                this._canRead = false;
                return this._prevCell = null;
            }
            if (IdxRdxMetadata.isIdxFooterMarker(term, row, col)) {
                this._canRead = false;
                return this._prevCell = null;
            }
            if (row < this._spec.startRow || col < 0 || col > 30) {
                final String message = "Invalid data in IDX file (skipping):\n\tcell row:" + row + " startRow:" + this._spec.startRow + " col:" + col + " term:" + IndexFilePrinter.bytes2hex(term) + "\n\t previous cell row:" + this._prevCell.row + " col:" + this._prevCell.col + " term:" + IndexFilePrinter.bytes2hex(this._prevCell.term) + "\n\tcurrent idx file position:" + this._fileChannel.position();
                IdxFileReader.logger.severe(message);
            }
            else {
                if (row >= this._spec.endRow) {
                    this._canRead = false;
                    return this._prevCell = null;
                }
                this._currentPosition += 25L;
                return this._prevCell = new Cell(row, col, term);
            }
        }
    }
    
    int getEstimatedNumCommonTerms() {
        if (this._estimatedNumCommonTerms < 0) {
            throw new IllegalStateException("Common terms has not been estimated yet");
        }
        return this._estimatedNumCommonTerms;
    }
    
    int getEstimatedNumCommonTermCells() {
        if (this._estimatedNumCommonTermCells < 0) {
            throw new IllegalStateException("Common terms has not been estimated yet");
        }
        return this._estimatedNumCommonTermCells;
    }
    
    void close() throws IOException {
        this._dataStream.close();
    }
    
    void estimateCommonTerms(final int maxCellsInPath) throws IOException, InterruptedException {
        final int samplingRate = this._spec.sdpCells / maxCellsInPath + 1;
        if (IdxFileReader.logger.isLoggable(Level.INFO)) {
            IdxFileReader.logger.log(Level.INFO, "For index " + this._filePath + " number of SDP cells is: " + this._spec.sdpCells + " and sampling rate for estimation of number of common terms: " + samplingRate);
        }
        final Map histogram = new HashMap(maxCellsInPath, 0.55f);
        this._estimatedNumCommonTerms = 0;
        this._estimatedNumCommonTermCells = 0;
        long cellCounter = 0L;
        Cell cell;
        while ((cell = this.readCell()) != null) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final RawTerm term = new RawTerm(cell.term);
            final Integer cardinality = (Integer) histogram.get(term);
            if (cardinality == null) {
                if (cell.row % samplingRate == 0) {
                    histogram.put(term, new Integer(1));
                }
            }
            else {
                histogram.put(term, new Integer(cardinality + 1));
            }
            if (!IdxFileReader.logger.isLoggable(Level.FINE) || ++cellCounter % 100000L != 0L) {
                continue;
            }
            IdxFileReader.logger.log(Level.FINE, "estimateCommonTerms processed " + cellCounter + " cells (" + cellCounter * 100L / this._spec.sdpCells + "%)");
        }
        if (IdxFileReader.logger.isLoggable(Level.FINE)) {
            IdxFileReader.logger.log(Level.FINE, "estimateCommonTerms finished reading IDX file and found " + histogram.size() + " unique terms.");
        }
        final int threshold = this._configuration.getTermCommonalityThreshold();
        final Iterator it = histogram.keySet().iterator();
        while (it.hasNext()) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final RawTerm term2 = (RawTerm) it.next();
            final int cardinality2 = (int) histogram.get(term2);
            if (cardinality2 <= threshold) {
                continue;
            }
            ++this._estimatedNumCommonTerms;
            this._estimatedNumCommonTermCells += cardinality2;
        }
        if (IdxFileReader.logger.isLoggable(Level.FINE)) {
            IdxFileReader.logger.log(Level.FINE, "estimateCommonTerms estimated " + this._estimatedNumCommonTerms + " common terms, and " + this._estimatedNumCommonTermCells + " common term cells (out of " + this._spec.sdpCells + " total SDP cells.)");
        }
    }
    
    static {
        logger = Logger.getLogger(IdxFileReader.class.getName());
    }
}
