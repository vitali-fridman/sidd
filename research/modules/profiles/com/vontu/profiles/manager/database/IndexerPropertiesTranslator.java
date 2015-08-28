// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import java.util.HashMap;
import com.vontu.profiles.manager.IndexerConfig;
import java.io.File;
import com.vontu.util.config.PropertySettingProvider;
import java.util.Map;
import java.util.logging.Logger;
import com.vontu.util.config.SettingProvider;

public final class IndexerPropertiesTranslator implements SettingProvider
{
    private static final Logger _logger;
    private static final Map<String, String> _translationMap;
    private static final String INDEXER_PROPERTIES_FILE = "com.vontu.profiles.data.indexer.properties";
    private final SettingProvider _indexerProperties;
    
    public IndexerPropertiesTranslator() {
        this._indexerProperties = (SettingProvider)new PropertySettingProvider("com.vontu.profiles.data.indexer.properties", IndexerPropertiesTranslator._logger);
    }
    
    public IndexerPropertiesTranslator(final File propertyFile) {
        this._indexerProperties = (SettingProvider)new PropertySettingProvider(propertyFile, IndexerPropertiesTranslator._logger);
    }
    
    private static String getClassPath() {
        return IndexerConfig.isLoaded() ? IndexerConfig.getClassPath() : System.getProperty("java.class.path");
    }
    
    private static String getWorkFolderPath() {
        return IndexerConfig.isLoaded() ? IndexerConfig.getLocalIndexDirectory().getAbsolutePath() : System.getProperty("java.io.tmpdir");
    }
    
    public String getSetting(final String name) {
        if ("indexer_classpath".equals(name)) {
            return getClassPath();
        }
        if ("indexer_logging_config_file".equals(name)) {
            return System.getProperty("com.vontu.manager.indexer.logging.properties");
        }
        if ("work_folder".equals(name)) {
            return getWorkFolderPath();
        }
        return this._indexerProperties.getSetting(translateSettingName(name));
    }
    
    private static String translateSettingName(final String settingName) {
        if (IndexerPropertiesTranslator._translationMap.containsKey(settingName)) {
            return IndexerPropertiesTranslator._translationMap.get(settingName);
        }
        return settingName;
    }
    
    static {
        _logger = Logger.getLogger(IndexerPropertiesTranslator.class.getName());
        (_translationMap = new HashMap<String, String>()).put("print_debug_info", "print_index_file");
        IndexerPropertiesTranslator._translationMap.put("enable_remote_debugging", "enable_remote_debugging");
        IndexerPropertiesTranslator._translationMap.put("filestream_output_buffer_size", "filestream_output_buffer_size");
        IndexerPropertiesTranslator._translationMap.put("indexer_memory_per_cell", "indexer_memory_to_cell_count");
        IndexerPropertiesTranslator._translationMap.put("max_indexer_memory", "max_indexer_memory");
        IndexerPropertiesTranslator._translationMap.put("index_process_memory_reserve", "index_process_memory_reserve");
        IndexerPropertiesTranslator._translationMap.put("max_loaded_index_memory", "max_loaded_index_memory");
        IndexerPropertiesTranslator._translationMap.put("ramindex_average_bucket_size", "ramindex_average_bucket_size");
        IndexerPropertiesTranslator._translationMap.put("term_commonality_threshold", "sdp_term_commonality_threshold");
        IndexerPropertiesTranslator._translationMap.put("term_size_to_retain", "term_size_to_retain");
        IndexerPropertiesTranslator._translationMap.put("drop_invalid_rows", "drop_bad_rows");
        IndexerPropertiesTranslator._translationMap.put("filestream_output_buffer_size", "filestream_output_buffer_size");
        IndexerPropertiesTranslator._translationMap.put("retain_crypto_file", "retain_idx_file");
        IndexerPropertiesTranslator._translationMap.put("use_out_of_process_indexer", "use_out_of_process_indexer");
    }
}
