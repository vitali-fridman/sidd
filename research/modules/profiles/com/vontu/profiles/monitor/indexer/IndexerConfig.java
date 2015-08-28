// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import com.vontu.logging.event.system.SystemEventSeverity;
import com.vontu.util.config.ConfigurationException;
import java.io.File;
import com.vontu.logging.LocalLogWriter;
import com.vontu.messaging.Registry;
import com.vontu.logging.SystemEvent;
import com.vontu.logging.SystemEventWriter;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SettingReader;
import java.util.logging.Logger;

public class IndexerConfig
{
    public static final String MONITOR_FOLDER_PROPERTY = "monitor_index_folder";
    public static final String INDEX_FOLDER_PROPERTY = "index_folder";
    public static final String INDEX_FOLDER_DEFALUT = "../index";
    public static final String IS_SINGLE_TIER_PROPERTY = "single_tier";
    public static final boolean IS_SINGLE_TIER_DEFAULT = false;
    private static final Logger _logger;
    private final SettingReader _settingReader;
    private final SettingProvider _settingProvider;
    private final SystemEventWriter _systemEventWriter;
    private static final SystemEvent FOLDER_CREATED_EVENT;
    private static final SystemEvent FOLDER_INVALID_EVENT;
    
    public IndexerConfig(final SettingProvider settingProvider) {
        this(settingProvider, LocalLogWriter.createAggregated(IndexerConfig._logger, Registry.getSystemEventWriter()));
    }
    
    IndexerConfig(final SettingProvider settingProvider, final SystemEventWriter eventWriter) {
        this(settingProvider, new SettingReader(settingProvider, IndexerConfig._logger), eventWriter);
    }
    
    private IndexerConfig(final SettingProvider settingProvider, final SettingReader settingReader, final SystemEventWriter eventWriter) {
        this._settingProvider = settingProvider;
        this._settingReader = settingReader;
        this._systemEventWriter = eventWriter;
    }
    
    private String getIndexFolderSetting() {
        return this._settingReader.getSetting(this.resolveFolderProperty(), "../index");
    }
    
    private String resolveFolderProperty() {
        return (this._settingProvider.getSetting("monitor_index_folder") == null) ? "index_folder" : "monitor_index_folder";
    }
    
    public boolean isTwoTierMode() {
        return !this._settingReader.getBooleanSetting("single_tier", false);
    }
    
    public File getIndexFolder() throws ConfigurationException {
        final File targetFolder = new File(this.getIndexFolderSetting());
        if (!targetFolder.exists()) {
            if (!this.isTwoTierMode()) {
                IndexerConfig.FOLDER_INVALID_EVENT.log(this._systemEventWriter, new String[] { targetFolder.getAbsolutePath() });
                throw new ConfigurationException("Index folder " + targetFolder.getAbsolutePath() + " doesn't exist.");
            }
            if (!targetFolder.mkdirs()) {
                throw new ConfigurationException("Failed to create index folder " + targetFolder.getAbsolutePath() + '.');
            }
            IndexerConfig.FOLDER_CREATED_EVENT.log(this._systemEventWriter, new String[] { targetFolder.getAbsolutePath() });
        }
        return targetFolder;
    }
    
    static {
        _logger = Logger.getLogger(IndexerConfig.class.getName());
        FOLDER_CREATED_EVENT = new SystemEvent("com.vontu.profiles.index_folder_created", SystemEventSeverity.WARNING);
        FOLDER_INVALID_EVENT = new SystemEvent("com.vontu.profiles.invalid_index_folder", SystemEventSeverity.SEVERE);
    }
}
