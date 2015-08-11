// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import java.util.logging.Level;
import com.vontu.util.system.MemoryInfoWithGc;
import com.vontu.util.system.MemoryInfo;
import java.io.File;
import java.util.logging.Logger;

public final class SubIndexCreator
{
    private final Logger _logger;
    private final File _idxFile;
    private final File _rdxFile;
    private IndexCombiner _combiner;
    private final SubIndexSpec _subIndexSpec;
    private final Configuration _configuration;
    private long _currentIdxFilePosition;
    private int _uncommonTermCellCount;
    private int _commonTermCount;
    private int _commonTermCellCount;
    private static final int MAX_ATTEMPTS_TO_COMBINE = 1;
    private final MemoryInfo _memoryInfo;
    
    public SubIndexCreator(final File idxFile, final SubIndexSpec spec, final File rdxFile, final Configuration configuration) {
        this._logger = Logger.getLogger(SubIndexCreator.class.getName());
        this._currentIdxFilePosition = -1L;
        this._uncommonTermCellCount = 0;
        this._commonTermCount = 0;
        this._commonTermCellCount = 0;
        this._idxFile = idxFile;
        this._rdxFile = rdxFile;
        this._subIndexSpec = spec;
        this._configuration = configuration;
        this._memoryInfo = (MemoryInfo)new MemoryInfoWithGc();
    }
    
    public int createRamIndex(final boolean addHeader) throws IOException, InterruptedException, IndexerException {
        int requiredMemory = 0;
        final int maxCellsInPass = this.getMaxCellsInPass();
        final IdxFileReader idxReader = new IdxFileReader(this._idxFile, this._subIndexSpec, this._configuration);
        idxReader.estimateCommonTerms(maxCellsInPass);
        final int estimatedNumCommonTerms = idxReader.getEstimatedNumCommonTerms();
        final int estimatedNumCommonTermCells = idxReader.getEstimatedNumCommonTermCells();
        this._currentIdxFilePosition = idxReader.getCurrentFilePosition();
        idxReader.close();
        final int targetUtiEntryCount = this._subIndexSpec.sdpCells - estimatedNumCommonTermCells;
        final int targetCollisionListLength = this._configuration.getRamindexAverageBucketSize();
        int[] ranges = Index.calculateSpineRanges(targetUtiEntryCount, targetCollisionListLength, this._subIndexSpec.sdpCells, maxCellsInPass);
        this._logger.info("Creating sub-index " + this._subIndexSpec.subIndexNumber + " Start row: " + this._subIndexSpec.startRow + " End row: " + this._subIndexSpec.endRow + " Cells (approx): " + this._subIndexSpec.sdpCells + " Starting IDX file position: " + this._subIndexSpec.startIdxFilePosition + " MaxCellsInPass: " + maxCellsInPass + " Number of passes: " + ranges.length);
        boolean doneSplitForRangeOnce = false;
        for (int rangeNum = 0; rangeNum < ranges.length; ++rangeNum) {
            IndexInRangeCreator rangeIndexCreator = null;
            int rangeBottom = 0;
            int rangeTop = 0;
            try {
                rangeBottom = ((rangeNum == 0) ? 0 : ranges[rangeNum - 1]);
                rangeTop = ranges[rangeNum];
                rangeIndexCreator = new IndexInRangeCreator(this._idxFile, this._rdxFile, this._subIndexSpec, rangeNum, rangeBottom, rangeTop, estimatedNumCommonTerms, estimatedNumCommonTermCells, this._configuration);
                rangeIndexCreator.indexRange();
                this._uncommonTermCellCount += rangeIndexCreator.getUncommonTermCellsStat();
                this._commonTermCount += rangeIndexCreator.getCommonTermsStat();
                this._commonTermCellCount += rangeIndexCreator.getCommonTermCellsStat();
                doneSplitForRangeOnce = false;
            }
            catch (OutOfMemoryError e) {
                if (doneSplitForRangeOnce) {
                    this._logger.log(Level.SEVERE, "Out of memory after splitting indexing range in half - this is unexpected error.", e);
                    throw new IndexerException(IndexerError.UNEXPECTED_OUT_OF_MEMORY_ERROR);
                }
                doneSplitForRangeOnce = true;
                final int rangeHalfPoint = rangeBottom + (rangeTop - rangeBottom) / 2;
                final int[] newRanges = new int[ranges.length + 1];
                System.arraycopy(ranges, 0, newRanges, 0, rangeNum);
                System.arraycopy(ranges, rangeNum, newRanges, rangeNum + 1, ranges.length - rangeNum);
                newRanges[rangeNum] = rangeHalfPoint;
                ranges = newRanges;
                --rangeNum;
                this._logger.warning("Out of memory on indexing pass " + rangeNum + " - will try split this pass in half. Number of passes now is" + ranges.length);
            }
        }
        this._combiner = new IndexCombiner(this._configuration, this._rdxFile, ranges.length);
        int numAttempts = 0;
        while (true) {
            try {
                if (addHeader) {
                    final IdxHeaderHandler headerHandler = new IdxHeaderHandler(this._idxFile);
                    requiredMemory = this._combiner.combine(headerHandler);
                }
                else {
                    requiredMemory = this._combiner.combine(null);
                }
                this._combiner.deleteRangeFiles();
            }
            catch (IOException e2) {
                if (++numAttempts < 1) {
                    this._logger.severe("Attemp #" + numAttempts + " to combine index ranges failed. Will retry.");
                    continue;
                }
                this._logger.severe("Attemp #" + numAttempts + " to combine index ranges failed. Bailing out.");
                throw e2;
            }
            break;
        }
        this._combiner.renameWithSuffix(this._subIndexSpec.subIndexNumber);
        IndexFilePrinter printer = null;
        try {
            if (this._configuration.shouldPrintDebugInfo()) {
                final File indexFile = this._combiner.getIndexFilePath();
                printer = new IndexFilePrinter(indexFile.getPath(), indexFile.getPath() + ".print");
                printer.printIndex();
            }
        }
        finally {
            if (printer != null) {
                printer.close();
            }
        }
        this._logger.info("Created sub-index " + this._subIndexSpec.subIndexNumber + ". It requires " + requiredMemory + "KB to load and contains " + this._commonTermCount + " common terms,  " + this._commonTermCellCount + " common term cells, and " + this._uncommonTermCellCount + " uncommon term cells");
        this._logger.info("Expected approximate total number of cells for this sub-index was: " + this._subIndexSpec.sdpCells + " and actual is: " + (this._uncommonTermCellCount + this._commonTermCellCount));
        return requiredMemory;
    }
    
    private int getMaxCellsInPass() {
        final long maxCellsLong = this.getMemoryLimit() / this._configuration.getIndexerMemoryPerCell();
        return (maxCellsLong > 2147483647L) ? Integer.MAX_VALUE : ((int)maxCellsLong);
    }
    
    public long getCurrentIdxFilePosition() {
        if (this._currentIdxFilePosition >= 0L) {
            return this._currentIdxFilePosition;
        }
        throw new IllegalStateException("Run indexing first.");
    }
    
    public int getCommonTermCellsStat() {
        return this._commonTermCellCount;
    }
    
    public int getCommonTermsStat() {
        return this._commonTermCount;
    }
    
    public int getUncommonTermCellsStat() {
        return this._uncommonTermCellCount;
    }
    
    public void deleteRamIndexFile() {
        if (this._combiner != null) {
            this._combiner.deleteIndexFile();
            this._combiner.deleteRangeFiles();
        }
    }
    
    void stripSuffixFromIndexFile() throws IOException {
        if (this._combiner != null) {
            this._combiner.stripSuffux();
            return;
        }
        throw new IllegalStateException("Can not be called prior to indexing.");
    }
    
    private long getMemoryLimit() {
        return Math.min(this._memoryInfo.getAvailableJvmMemory(), this._configuration.getMaxIndexerMemory());
    }
}
