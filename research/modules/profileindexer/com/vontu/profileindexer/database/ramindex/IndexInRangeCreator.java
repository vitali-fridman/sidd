// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.io.IOException;
import java.io.File;
import java.util.logging.Logger;

class IndexInRangeCreator
{
    private static final Logger logger;
    public static final String RAM_INDEX_FILE_EXTENSION = ".rdx";
    private final IdxFileReader _idxReader;
    private final File _idxFile;
    private final File _workFolder;
    private final Configuration _configuration;
    private final File _ramIndexFile;
    private IndexHashedByTermInRange _uncomonTermIndex;
    private IndexHashedByTermAndRow _commonTermIndex;
    private AllCommonTermIndex _allCommonTerms;
    private final SubIndexSpec _subIndexSpec;
    private final int _estimatedNumCommonTermsInSdp;
    private final int _estimatedNumCommonTermsCellsInSdp;
    private final int _rangeNum;
    private final int _rangeBottom;
    private final int _rangeTop;
    private int _uncommonTermCellCountInRange;
    private int _commonTermCountInRange;
    private int _commonTermCellCountInRange;
    
    IndexInRangeCreator(final File idxFile, final File ramIndexFile, final SubIndexSpec subIndexSpec, final int rangeNum, final int rangeBottom, final int rangeTop, final int estimatedNumCommonTermsInSdp, final int estimatedNumCommonTermsCellsInSdp, final Configuration configuration) throws IOException {
        this._uncommonTermCellCountInRange = 0;
        this._commonTermCountInRange = 0;
        this._commonTermCellCountInRange = 0;
        this._subIndexSpec = subIndexSpec;
        this._estimatedNumCommonTermsInSdp = estimatedNumCommonTermsInSdp;
        this._estimatedNumCommonTermsCellsInSdp = estimatedNumCommonTermsCellsInSdp;
        this._idxFile = idxFile;
        this._idxReader = new IdxFileReader(this._idxFile, this._subIndexSpec, configuration);
        this._workFolder = ramIndexFile.getParentFile();
        this._ramIndexFile = ramIndexFile;
        this._rangeNum = rangeNum;
        this._rangeBottom = rangeBottom;
        this._rangeTop = rangeTop;
        this._configuration = configuration;
    }
    
    void indexRange() throws IOException, InterruptedException {
        try {
            final int targetUtiEntryCount = this._subIndexSpec.sdpCells - this._estimatedNumCommonTermsCellsInSdp;
            final int targetCollisionListLength = this._configuration.getRamindexAverageBucketSize();
            this._uncomonTermIndex = new IndexHashedByTermInRange(targetUtiEntryCount, targetCollisionListLength, this._subIndexSpec.termLengthToRetain, this._rangeBottom, this._rangeTop);
            this._commonTermIndex = new IndexHashedByTermAndRow(this._estimatedNumCommonTermsCellsInSdp, targetCollisionListLength, this._subIndexSpec.termLengthToRetain);
            this._allCommonTerms = new AllCommonTermIndex(this._estimatedNumCommonTermsInSdp, targetCollisionListLength);
            long cellCounter = 0L;
            final long cellProgressSampling = (this._subIndexSpec.sdpCells > 1000) ? (this._subIndexSpec.sdpCells / 20) : 100L;
            Cell cell;
            while ((cell = this._idxReader.readCell()) != null) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (this._uncomonTermIndex.tryToAddEntry(cell)) {
                    ++this._uncommonTermCellCountInRange;
                }
                if (!IndexInRangeCreator.logger.isLoggable(Level.FINE) || ++cellCounter % cellProgressSampling != 0L) {
                    continue;
                }
                IndexInRangeCreator.logger.log(Level.FINE, "IndexInRangeCreator for range " + this._rangeNum + " processed " + cellCounter + " cells (" + cellCounter * 100L / this._subIndexSpec.sdpCells + "%)");
            }
            boolean logProgress = true;
            final int logProgressSamling = (this._rangeTop - this._rangeBottom) / 20;
            if (logProgressSamling < 1000) {
                logProgress = false;
            }
            int uncommonTermIndexEmptyBucketCount = 0;
            for (int i = this._rangeBottom; i < this._rangeTop; ++i) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                final List bucket = this._uncomonTermIndex.getBucket(i);
                if (bucket != null) {
                    this.moveCommonTerms(bucket);
                    if (bucket.size() == 0) {
                        ++uncommonTermIndexEmptyBucketCount;
                    }
                }
                else {
                    ++uncommonTermIndexEmptyBucketCount;
                }
                if (IndexInRangeCreator.logger.isLoggable(Level.FINE) && logProgress && (i - this._rangeBottom) % logProgressSamling == 0) {
                    IndexInRangeCreator.logger.log(Level.FINE, "IndexInRangeCreator for range " + this._rangeNum + " processed " + (i - this._rangeBottom) * 100 / (this._rangeTop - this._rangeBottom) + " % of the range spine.");
                }
            }
            this._uncomonTermIndex.setEmptyBucketCount(uncommonTermIndexEmptyBucketCount);
            this._allCommonTerms.setEntryCount(this._commonTermCountInRange);
            this._commonTermIndex.setEntryCount(this._commonTermCellCountInRange);
            this._uncomonTermIndex.setEntryCount(this._uncommonTermCellCountInRange);
            IndexInRangeCreator.logger.fine("Size of stream output buffer for writing index ranges is : " + this._configuration.getFilestreamOutputBufferSize());
            final IndexRangeWriter indexRangeWriter = new IndexRangeWriter(this._configuration, this._workFolder, this._ramIndexFile, this._allCommonTerms, this._commonTermIndex, this._uncomonTermIndex, this._rangeNum);
            indexRangeWriter.writeIndex();
        }
        finally {
            this.close();
        }
    }
    
    int getCommonTermCellsStat() {
        return this._commonTermCellCountInRange;
    }
    
    int getCommonTermsStat() {
        return this._commonTermCountInRange;
    }
    
    int getUncommonTermCellsStat() {
        return this._uncommonTermCellCountInRange;
    }
    
    private void close() throws IOException {
        if (this._idxReader != null) {
            this._idxReader.close();
        }
    }
    
    private void moveCommonTerms(final List bucket) {
        final Map<RawTerm, HistogramEntry> histogram = calculateHistogram(bucket);
        for (final HistogramEntry he : histogram.values()) {
            if (he.getCardinality() > this._configuration.getTermCommonalityThreshold()) {
                int mask = 0;
                Cell cell = null;
                for (final Integer indexInt : he) {
                    final int index = indexInt;
                    cell = (Cell) bucket.set(index, null);
                    --this._uncommonTermCellCountInRange;
                    mask = AllCommonTermIndex.addToMask(mask, cell.col);
                    this._commonTermIndex.addEntry(cell);
                    ++this._commonTermCellCountInRange;
                }
                this._allCommonTerms.addEntry(new AllCommonTermsIndexEntry(mask, cell.term));
                ++this._commonTermCountInRange;
            }
        }
    }
    
    private static Map calculateHistogram(final List<Cell> bucket) {
        final HashMap<RawTerm, HistogramEntry> histogram = new HashMap<RawTerm, HistogramEntry>(bucket.size() * 2, 0.55f);
        int index = 0;
        for (final Cell cell : bucket) {
            final RawTerm term = new RawTerm(cell.term);
            final HistogramEntry entry = (HistogramEntry) histogram.get(term);
            if (entry == null) {
                histogram.put(term, new HistogramEntry(index));
            }
            else {
                entry.addLocation(index);
            }
            ++index;
        }
        return histogram;
    }
    
    static {
        logger = Logger.getLogger(IndexInRangeCreator.class.getName());
    }
}
