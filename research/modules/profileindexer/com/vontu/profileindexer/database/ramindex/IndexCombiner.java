// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.logging.Level;
import java.util.LinkedList;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.logging.Logger;

class IndexCombiner
{
    private static final Logger logger;
    private static final String ACT_PREFIX = "act";
    private static final String CTI_PREFIX = "cti";
    private static final String UTI_PREFIX = "uti";
    private final Configuration _configuration;
    private File _rdxFile;
    private final int _numRanges;
    private static final int READ_BUFFER_SIZE = 1048576;
    
    IndexCombiner(final Configuration configuration, final File rdxFile, final int numRanges) {
        this._configuration = configuration;
        this._rdxFile = rdxFile;
        this._numRanges = numRanges;
    }
    
    int combine(final IdxHeaderHandler headerHandler) throws IOException, InterruptedException {
        int requiredMemory = 0;
        DataOutputStream combinedIndex = null;
        final DataInputStream[] range = new DataInputStream[this._numRanges];
        final File[] rangeFile = new File[this._numRanges];
        try {
            final String pathPrefix = this._rdxFile.getPath();
            final int filestreamOutputBufferSize = this._configuration.getFilestreamOutputBufferSize();
            IndexCombiner.logger.fine("Size of stream output buffer for writing final index files: " + filestreamOutputBufferSize);
            combinedIndex = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this._rdxFile), filestreamOutputBufferSize));
            if (headerHandler != null) {
                headerHandler.copyIdxFooterToRdxHeader(combinedIndex);
            }
            openRanges(range, rangeFile, pathPrefix, "act");
            requiredMemory += combineRanges("act", combinedIndex, range, new AllCommonTermsIndexEntryFactory());
            closeRanges(range, rangeFile);
            openRanges(range, rangeFile, pathPrefix, "cti");
            requiredMemory += combineRanges("cti", combinedIndex, range, new CommonTermCellFactory());
            closeRanges(range, rangeFile);
            openRanges(range, rangeFile, pathPrefix, "uti");
            requiredMemory += combineRanges("uti", combinedIndex, range, new CellFactory());
            closeRanges(range, rangeFile);
        }
        finally {
            if (combinedIndex != null) {
                combinedIndex.close();
            }
            closeRanges(range, rangeFile);
        }
        return requiredMemory;
    }
    
    private static int combineRanges(final String indexName, final DataOutputStream combinedIndex, final DataInputStream[] range, final IndexEntryFactory factory) throws IOException, InterruptedException {
        IndexCombiner.logger.finest("Combining ranges for " + indexName);
        int termLength = 0;
        int spineLength = 0;
        int emptyBucketCount = 0;
        int countOfEntries = 0;
        for (int i = 0; i < range.length; ++i) {
            termLength = range[i].readInt();
            spineLength = range[i].readInt();
            emptyBucketCount = range[i].readInt();
            countOfEntries += range[i].readInt();
        }
        IndexCombiner.logger.finest("spineLength: " + spineLength + " emptyBucketCount: " + emptyBucketCount + " countOfEntries: " + countOfEntries);
        combinedIndex.writeInt(termLength);
        combinedIndex.writeInt(spineLength);
        combinedIndex.writeInt(0);
        combinedIndex.writeInt(countOfEntries);
        for (int bucketIndex = 0; bucketIndex < spineLength; ++bucketIndex) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            int prevBucketSize = 0;
            int bucketSize = 0;
            final List combinedBucket = new LinkedList();
            for (int j = 0; j < range.length; ++j) {
                try {
                    bucketSize = range[j].readInt();
                    for (int k = 0; k < bucketSize; ++k) {
                        combinedBucket.add(factory.read(range[j], termLength));
                    }
                }
                catch (IOException e) {
                    IndexCombiner.logger.log(Level.FINEST, "IOException:", e);
                    IndexCombiner.logger.finest("for range #" + j + " bucket index:" + bucketIndex + " bucketSize:" + bucketSize + " prevBucketSize:" + prevBucketSize);
                    throw e;
                }
                prevBucketSize = bucketSize;
            }
            combinedIndex.writeInt(combinedBucket.size());
            Collections.sort((List<Comparable>)combinedBucket);
            final Iterator iterator = combinedBucket.iterator();
            while (iterator.hasNext()) {
                ((IndexEntry) iterator.next()).write(combinedIndex);
            }
        }
        if (IndexCombiner.logger.isLoggable(Level.INFO)) {
            IndexCombiner.logger.log(Level.INFO, "For " + indexName + " Index - spine length: " + spineLength + " count of entries: " + countOfEntries + " average bucket size: " + countOfEntries / spineLength);
        }
        return factory.estimateMemory(termLength, spineLength, countOfEntries);
    }
    
    private static void openRanges(final DataInputStream[] range, final File[] rangeFile, final String pathPrefix, final String suffix) throws FileNotFoundException {
        for (int i = 0; i < range.length; ++i) {
            rangeFile[i] = new File(pathPrefix + "." + i + "." + suffix);
            final FileInputStream fis = new FileInputStream(rangeFile[i]);
            range[i] = new DataInputStream(new BufferedInputStream(fis, 1048576));
        }
    }
    
    private static void closeRanges(final DataInputStream[] range, final File[] rangeFile) throws IOException {
        for (int i = 0; i < range.length; ++i) {
            if (range[i] != null) {
                range[i].close();
                range[i] = null;
                if (rangeFile[i] != null) {
                    rangeFile[i] = null;
                }
            }
        }
    }
    
    void deleteRangeFiles() {
        for (int i = 0; i < this._numRanges; ++i) {
            new File(this._rdxFile.getPath() + "." + i + "." + "act").delete();
            new File(this._rdxFile.getPath() + "." + i + "." + "cti").delete();
            new File(this._rdxFile.getPath() + "." + i + "." + "uti").delete();
        }
    }
    
    File getIndexFilePath() {
        return this._rdxFile;
    }
    
    void renameWithSuffix(final int n) throws IOException {
        final File pathWithSuffix = new File(this._rdxFile.getPath() + "." + n);
        if (this._rdxFile.renameTo(pathWithSuffix)) {
            this._rdxFile = pathWithSuffix;
            return;
        }
        throw new IOException("Unable to rename " + this._rdxFile + " to " + pathWithSuffix);
    }
    
    void stripSuffux() throws IOException {
        final String pathWithSuffix = this._rdxFile.getPath();
        final int lastDot = pathWithSuffix.lastIndexOf(46);
        if (lastDot == -1) {
            throw new IllegalStateException("No suffix in " + pathWithSuffix);
        }
        final File pathWithoutSuffix = new File(pathWithSuffix.substring(0, lastDot));
        if (this._rdxFile.renameTo(pathWithoutSuffix)) {
            this._rdxFile = pathWithoutSuffix;
            return;
        }
        throw new IOException("Unable to rename " + this._rdxFile + " to " + pathWithoutSuffix);
    }
    
    void deleteIndexFile() {
        if (!this._rdxFile.delete()) {
            IndexCombiner.logger.warning("Unable to delete file " + this._rdxFile);
        }
    }
    
    static {
        logger = Logger.getLogger(IndexCombiner.class.getName());
    }
}
