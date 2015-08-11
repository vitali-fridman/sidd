// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.EOFException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import com.vontu.util.Stopwatch;
import java.io.File;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;

public final class IndexCreator implements RamIndexCreator
{
    private static final Logger _logger;
    private final Configuration _configuration;
    
    public IndexCreator(final SettingProvider settingProvider) {
        this._configuration = new Configuration(settingProvider);
    }
    
    public IndexCreator(final Configuration configuration) {
        this._configuration = configuration;
    }
    
    @Override
    public RamIndexResult createRamIndex(final CryptoIndexDescriptor indexDescriptor, final File rdxFile) throws IndexerException, InterruptedException {
        IndexCreator._logger.info("Creating RAM index in " + rdxFile.getAbsolutePath() + " for " + indexDescriptor);
        final Stopwatch stopwatch = new Stopwatch("IndexCreator");
        stopwatch.start();
        final File idxFile = indexDescriptor.indexFile();
        final int rowCount = indexDescriptor.rowCount();
        final int cellCount = indexDescriptor.cellCount();
        int maxRequiredMemory = 0;
        final int termLengthToRetain = this.calculateTermLengthToRetain(rowCount);
        final int numOfSubIndexes = this.calculateNumSubIndexes(rowCount, cellCount);
        final int subIndexRows = rowCount / numOfSubIndexes;
        final int subIndexCells = cellCount / numOfSubIndexes;
        final RamIndexStatistics statisticsBuilder = new RamIndexStatistics();
        final SubIndexCreator[] subIndexCreators = new SubIndexCreator[numOfSubIndexes];
        IndexCreator._logger.info("Will create " + numOfSubIndexes + " sub-index(es). " + termLengthToRetain + " bytes of term will be retained." + " Rows per sub-index: " + subIndexRows + " Cells per sub-index: " + subIndexCells);
        long startIdxFilePosition = 0L;
        int startRow = 1;
        int cellsProcessed = 0;
        try {
            for (int i = 0; i < numOfSubIndexes; ++i) {
                final SubIndexSpec spec = new SubIndexSpec(i, startRow, (i == numOfSubIndexes - 1) ? (rowCount + 1) : (startRow + subIndexRows), startIdxFilePosition, (i == numOfSubIndexes - 1) ? (cellCount - cellsProcessed) : subIndexCells, termLengthToRetain);
                subIndexCreators[i] = new SubIndexCreator(indexDescriptor.indexFile(), spec, rdxFile, this._configuration);
                final int requiredMemory = subIndexCreators[i].createRamIndex(i == 0);
                if (requiredMemory > maxRequiredMemory) {
                    maxRequiredMemory = requiredMemory;
                }
                statisticsBuilder.addUncommonTermCount(subIndexCreators[i].getUncommonTermCellsStat());
                statisticsBuilder.addCommonTermCellCount(subIndexCreators[i].getCommonTermCellsStat());
                statisticsBuilder.addCommonTermCount(subIndexCreators[i].getCommonTermsStat());
                startRow += subIndexRows;
                cellsProcessed += subIndexCells;
                startIdxFilePosition = subIndexCreators[i].getCurrentIdxFilePosition();
                if (numOfSubIndexes == 1) {
                    subIndexCreators[i].stripSuffixFromIndexFile();
                }
            }
            return new RamIndexResult(maxRequiredMemory, numOfSubIndexes, termLengthToRetain, this._configuration.getRamindexAverageBucketSize(), statisticsBuilder, stopwatch.stop().getLastTime());
        }
        catch (EOFException e) {
            cleanup(subIndexCreators);
            throw new IndexerException(IndexerError.RAM_INDEX_CREATE_ERROR_WILL_RETRY, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e);
        }
        catch (IOException e2) {
            cleanup(subIndexCreators);
            if (e2.getMessage() != null && e2.getMessage().toLowerCase().indexOf("not enough space") != -1) {
                throw new IndexerException(IndexerError.NOT_ENOUGH_DISK_SPACE, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e2);
            }
            throw new IndexerException(IndexerError.RAM_INDEX_CREATE_ERROR, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e2);
        }
        catch (InterruptedException e3) {
            cleanup(subIndexCreators);
            throw e3;
        }
        catch (RuntimeException e4) {
            cleanup(subIndexCreators);
            throw e4;
        }
        catch (Error e5) {
            cleanup(subIndexCreators);
            throw e5;
        }
    }
    
    private int calculateNumSubIndexes(final int totalSdpRows, final int totalSdpCells) {
        final int memoryInKB = this.calculateIndexOnlyMemoryRequirements(totalSdpRows, totalSdpCells);
        final int maxMemoryInKB = (int)(this._configuration.getMaxLoadedIndexMemory() - this._configuration.getIndexProcessMemoryReserve()) >> 10;
        int numSubIndexes;
        if (maxMemoryInKB <= 0 || memoryInKB == 0) {
            numSubIndexes = 1;
        }
        else {
            numSubIndexes = memoryInKB / maxMemoryInKB + ((memoryInKB % maxMemoryInKB > 0) ? 1 : 0);
        }
        return numSubIndexes;
    }
    
    private int calculateIndexOnlyMemoryRequirements(final int totalSdpRows, final int totalSdpCells) {
        final int termLength = this.calculateTermLengthToRetain(totalSdpRows);
        final int spineLength = Index.calculateSpineLength(totalSdpCells, this._configuration.getRamindexAverageBucketSize());
        return new CellFactory().estimateMemory(termLength, spineLength, totalSdpCells);
    }
    
    private static void cleanup(final SubIndexCreator[] subIndexCreators) {
        for (int i = 0; i < subIndexCreators.length; ++i) {
            if (subIndexCreators[i] != null) {
                subIndexCreators[i].deleteRamIndexFile();
            }
        }
    }
    
    private int calculateTermLengthToRetain(final int numSdpRows) {
        return calculateTermLengthToRetain(this._configuration, numSdpRows);
    }
    
    private static int calculateTermLengthToRetain(final Configuration indexerConfig, final int numSdpRows) {
        final int[] rowRange = indexerConfig.getTermSizeToRetainRowLimits();
        final int[] toRetain = indexerConfig.getTermSizeToRetain();
        for (int i = 0; i < rowRange.length; ++i) {
            if (numSdpRows < rowRange[i]) {
                return toRetain[i];
            }
        }
        return 0;
    }
    
    static {
        _logger = Logger.getLogger(IndexCreator.class.getName());
    }
}
