// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;
import java.io.Serializable;

public final class Configuration implements Serializable
{
    private static final long serialVersionUID = 1720675395193197731L;
    private static final Logger _logger;
    private static final int MB = 1048576;
    public static final String INDEXER_MEMORY_PER_CELL_NAME = "indexer_memory_per_cell";
    public static final int INDEXER_MEMORY_PER_CELL_DEFAULT = 160;
    public static final String MAX_INDEXER_MEMORY_NAME = "max_indexer_memory";
    public static final int MAX_INDEXER_MEMORY_DEFAULT = 1468006400;
    public static final String INDEX_PROCESS_MEMORY_RESERVE = "index_process_memory_reserve";
    public static final long INDEX_PROCESS_MEMORY_RESERVE_DEFAULT = 209715200L;
    public static final String MAX_LOADED_INDEX_MEMORY_NAME = "max_loaded_index_memory";
    public static final int MAX_LOADED_INDEX_MEMORY_DEFAULT = 1468006400;
    public static final String TERM_COMMONALITY_THRESHOLD_NAME = "term_commonality_threshold";
    public static final int TERM_COMMONALITY_THRESHOLD_DEFAULT = 100;
    public static final String RAMINDEX_AVERAGE_BUCKET_SIZE_NAME = "ramindex_average_bucket_size";
    public static final int RAMINDEX_AVERAGE_BUCKET_SIZE_DEFAULT = 10;
    public static final String TERM_SIZE_TO_RETAIN_NAME = "term_size_to_retain";
    public static final String TERM_SIZE_TO_RETAIN_PROPERTY_DEFAULT = "1000,6,10000000,5,50000000,4,150000000,3,500000000,2,1000000000,1";
    private static final int[] TERM_SIZE_TO_RETAIN_ROW_LIMIT_DEFAULT;
    private static final int[] TERM_SIZE_TO_RETAIN_DEFAULT;
    public static final String SHOULD_PRINT_DEBUG_INFO_NAME = "print_debug_info";
    public static final boolean SHOULD_PRINT_DEBUG_INFO_DEFAULT = false;
    public static final String ENABLE_REMOTE_DEBUGGING = "enable_remote_debugging";
    public static final boolean ENABLE_REMOTE_DEBUGGING_DEFAULT = false;
    public static final String FILESTREAM_OUTPUT_BUFFER_SIZE = "filestream_output_buffer_size";
    public static final int FILESTREAM_OUTPUT_BUFFER_SIZE_DEFAULT = 32768;
    private final long _indexProcessMemoryReserve;
    private final long _maxLoadedIndexMemory;
    private final long _maxIndexerMemory;
    private final int _termCommonalityThreshold;
    private final int _ramIndexAverageBucketSize;
    private final int _indexerMemoryPerCell;
    private final int[] _termSizeToRetainRowLimits;
    private final int[] _termSizeToRetain;
    private final boolean _shouldPrintDebugInfo;
    private final boolean _enableRemoteDebugging;
    private final int _filestreamOutputBufferSize;
    
    public Configuration(final SettingProvider settingProvider) {
        this(new SettingReader(settingProvider, Configuration._logger));
    }
    
    private Configuration(final SettingReader reader) {
        this._indexerMemoryPerCell = reader.getIntSetting("indexer_memory_per_cell", 160);
        this._indexProcessMemoryReserve = reader.getMemorySetting("index_process_memory_reserve", 209715200L);
        this._maxIndexerMemory = reader.getMemorySetting("max_indexer_memory", 1468006400L);
        this._maxLoadedIndexMemory = reader.getMemorySetting("max_loaded_index_memory", 1468006400L);
        this._ramIndexAverageBucketSize = reader.getIntSetting("ramindex_average_bucket_size", 10);
        this._shouldPrintDebugInfo = reader.getBooleanSetting("print_debug_info", false);
        this._enableRemoteDebugging = reader.getBooleanSetting("enable_remote_debugging", false);
        this._termCommonalityThreshold = reader.getIntSetting("term_commonality_threshold", 100);
        this._filestreamOutputBufferSize = reader.getIntSetting("filestream_output_buffer_size", 32768);
        final int[][] termSizeValues = parseTermSizeToRetain(reader.getSetting("term_size_to_retain", "1000,6,10000000,5,50000000,4,150000000,3,500000000,2,1000000000,1"));
        this._termSizeToRetainRowLimits = termSizeValues[0];
        this._termSizeToRetain = termSizeValues[1];
    }
    
    long getMaxIndexerMemory() {
        return this._maxIndexerMemory;
    }
    
    long getMaxLoadedIndexMemory() {
        return this._maxLoadedIndexMemory;
    }
    
    long getIndexProcessMemoryReserve() {
        return this._indexProcessMemoryReserve;
    }
    
    int getTermCommonalityThreshold() {
        return this._termCommonalityThreshold;
    }
    
    int getRamindexAverageBucketSize() {
        return this._ramIndexAverageBucketSize;
    }
    
    int getIndexerMemoryPerCell() {
        return this._indexerMemoryPerCell;
    }
    
    int[] getTermSizeToRetainRowLimits() {
        return this._termSizeToRetainRowLimits.clone();
    }
    
    int[] getTermSizeToRetain() {
        return this._termSizeToRetain.clone();
    }
    
    boolean shouldPrintDebugInfo() {
        return this._shouldPrintDebugInfo;
    }
    
    boolean enableRemoteDebugging() {
        return this._enableRemoteDebugging;
    }
    
    int getFilestreamOutputBufferSize() {
        return this._filestreamOutputBufferSize;
    }
    
    private static int[][] parseTermSizeToRetain(final String termSizeToRetain) {
        final String[] val = termSizeToRetain.split(",");
        final int[][] result = new int[2][0];
        if (val.length % 2 == 0 && val.length >= 2) {
            try {
                final int numRanges = val.length / 2;
                result[0] = new int[numRanges];
                result[1] = new int[numRanges];
                for (int range = 0; range < numRanges; ++range) {
                    result[0][range] = Integer.parseInt(val[range * 2]);
                    result[1][range] = Integer.parseInt(val[range * 2 + 1]);
                }
            }
            catch (NumberFormatException e) {
                result[0] = Configuration.TERM_SIZE_TO_RETAIN_ROW_LIMIT_DEFAULT;
                result[1] = Configuration.TERM_SIZE_TO_RETAIN_DEFAULT;
            }
        }
        else {
            result[0] = Configuration.TERM_SIZE_TO_RETAIN_ROW_LIMIT_DEFAULT;
            result[1] = Configuration.TERM_SIZE_TO_RETAIN_DEFAULT;
        }
        return result;
    }
    
    static {
        _logger = Logger.getLogger(Configuration.class.getName());
        TERM_SIZE_TO_RETAIN_ROW_LIMIT_DEFAULT = new int[] { 1000, 10000000, 50000000, 150000000, 500000000, 1000000000 };
        TERM_SIZE_TO_RETAIN_DEFAULT = new int[] { 6, 5, 4, 3, 2, 1 };
    }
}
