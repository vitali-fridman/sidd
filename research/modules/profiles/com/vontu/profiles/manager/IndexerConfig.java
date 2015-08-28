// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager;

import com.vontu.util.unicode.UnicodeNormalizationConfigLoaderException;
import java.io.IOException;
import com.vontu.util.unicode.UnicodeNormalizerRegistry;
import com.vontu.util.unicode.AsianTokenUtil;
import com.vontu.util.unicode.UnicodeNormalizer;
import com.vontu.util.unicode.UnicodeNormalizationConfigLoader;
import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.PropertySettingProvider;
import java.io.FilenameFilter;
import com.vontu.cracker.SettingWrapper;
import com.vontu.util.config.SettingReader;
import java.io.File;
import java.util.logging.Logger;

public final class IndexerConfig
{
    private static final Logger _logger;
    public static final String INDEXER_PROPERTIES_FILE = "com.vontu.profiles.data.indexer.properties";
    private static final String INDEX_FOLDER_PROPERTY = "com.vontu.index.dir";
    private static final String INDEX_FOLDER_DEFAULT = "../index";
    public static final String CREATE_ERROR_FILE = "create_error_file";
    private static final boolean CREATE_ERROR_FILE_DEFAULT = false;
    private static final String RETAIN_DUMP_FILE = "retain_dump_file";
    private static final boolean RETAIN_DUMP_FILE_DEFAULT = false;
    private static final int DIRECTORY_BROWSER_TIMEOUT_MS_DEFAULT = 3000;
    private static final String DIRECTORY_BROWSER_TIMEOUT_MS = "com.vontu.profiles.directoryconnection.directoryBrowserTimeoutMS";
    private static final int MB = 1048576;
    private static final String MAX_LOADED_INDEX_MEMORY = "max_loaded_index_memory";
    private static final long MAX_LOADED_INDEX_MEMORY_DEFAULT = 1468006400L;
    private static final String INDEX_PROCESS_MEMORY_RESERVE = "index_process_memory_reserve";
    private static final long INDEX_PROCESS_MEMORY_RESERVE_DEFAULT = 209715200L;
    private static final String MAX_DIRECTORY_DEPTH = "com.vontu.profiles.documents.maxDirectoryDepth";
    private static final int MAX_DIRECTORY_DEPTH_DEFAULT = 50;
    private static final String MAX_DOCUMENT_SIZE = "ContentExtraction.MaxContentSize";
    private static final int MAX_DOCUMENT_SIZE_DEFAULT = 31457280;
    private static final String RETAIN_ALL_FILES = "com.vontu.profiles.documents.retainAllFiles";
    private static final boolean RETAIN_ALL_FILES_DEFAULT = false;
    private static final String RETAIN_ZIP_CONTENT = "com.vontu.profiles.documents.retainZipContent";
    private static final boolean RETAIN_ZIP_CONTENT_DEFAULT = true;
    private static final String DOC_COUNT_UPDATE_INTERVAL = "com.vontu.profiles.documents.docCountUpdateInterval";
    private static final int DOC_COUNT_UPDATE_INTERVAL_DEFAULT = 500;
    private static File _indexFolder;
    private static boolean _isLoaded;
    private static String _classPath;
    private static SettingReader _settings;
    private static SettingWrapper _settingWrapper;
    private static final String USE_JCIFS_FOR_IDM_INDEXING = "idmindexer.use.jcifs";
    private static final boolean USE_JCIFS_FOR_IDM_INDEXING_DEFAULT = false;
    
    private static void checkPreconditions() {
        if (!IndexerConfig._isLoaded) {
            throw new IllegalStateException("The configuration isn't loaded.");
        }
    }
    
    public static synchronized String getClassPath() {
        checkPreconditions();
        return IndexerConfig._classPath;
    }
    
    public static synchronized File getLocalIndexDirectory() {
        checkPreconditions();
        return IndexerConfig._indexFolder;
    }
    
    public static synchronized boolean shouldCreateErrorFile() {
        checkPreconditions();
        return IndexerConfig._settings.getBooleanSetting("create_error_file", false);
    }
    
    public static synchronized boolean shouldRemoveDumpFile() {
        checkPreconditions();
        return !IndexerConfig._settings.getBooleanSetting("retain_dump_file", false);
    }
    
    public static synchronized int getMaxDirectoryDepth() {
        checkPreconditions();
        return IndexerConfig._settings.getIntSetting("com.vontu.profiles.documents.maxDirectoryDepth", 50);
    }
    
    public static synchronized long getMaxLoadedIndexMemory() {
        checkPreconditions();
        return IndexerConfig._settings.getMemorySetting("max_loaded_index_memory", 1468006400L);
    }
    
    public static synchronized long getIndexProcessMemoryReserve() {
        checkPreconditions();
        return IndexerConfig._settings.getMemorySetting("index_process_memory_reserve", 209715200L);
    }
    
    public static synchronized int getMaxDocumentSize() {
        checkPreconditions();
        return (int)IndexerConfig._settings.getMemorySetting("ContentExtraction.MaxContentSize", 31457280L);
    }
    
    public static synchronized boolean retainAllFiles() {
        checkPreconditions();
        return IndexerConfig._settings.getBooleanSetting("com.vontu.profiles.documents.retainAllFiles", false);
    }
    
    public static synchronized boolean retainZipContent() {
        checkPreconditions();
        return IndexerConfig._settings.getBooleanSetting("com.vontu.profiles.documents.retainZipContent", true);
    }
    
    public static synchronized int getDocCountUpdateInterval() {
        checkPreconditions();
        return IndexerConfig._settings.getIntSetting("com.vontu.profiles.documents.docCountUpdateInterval", 500);
    }
    
    public static SettingReader getSettings() {
        checkPreconditions();
        return IndexerConfig._settings;
    }
    
    public static boolean getUseJcifsForIDMIndexing() {
        checkPreconditions();
        return IndexerConfig._settings.getBooleanSetting("idmindexer.use.jcifs", false);
    }
    
    public static int getDirectoryBrowserTimeoutMs() {
        return IndexerConfig._settings.getIntSetting("com.vontu.profiles.directoryconnection.directoryBrowserTimeoutMS", 3000);
    }
    
    public static synchronized void load(final String servletPath, final String homePath) {
        final char separator = File.separatorChar;
        final char pathSeparator = File.pathSeparatorChar;
        final String resourcePath = servletPath + separator + "WEB-INF";
        final StringBuffer classPathBuilder = new StringBuffer();
        classPathBuilder.append(resourcePath).append(separator).append("classes");
        final String libPath = resourcePath + separator + "lib";
        final File libFolder = new File(libPath);
        final String[] servletJars = libFolder.list(new JarFilter(libFolder));
        for (int i = 0; i < servletJars.length; ++i) {
            classPathBuilder.append(pathSeparator).append(libPath).append(separator).append(servletJars[i]);
        }
        final String commonPath = homePath + separator + "lib";
        final File commonFolder = new File(commonPath);
        final String[] commonJars = commonFolder.list(new JarFilter(commonFolder));
        for (int j = 0; j < commonJars.length; ++j) {
            classPathBuilder.append(pathSeparator).append(commonPath).append(separator).append(commonJars[j]);
        }
        IndexerConfig._classPath = classPathBuilder.toString();
        final SettingProvider settingProvider = (SettingProvider)new PropertySettingProvider("com.vontu.profiles.data.indexer.properties", IndexerConfig._logger);
        IndexerConfig._settingWrapper = new SettingWrapper(settingProvider, IndexerConfig._classPath);
        IndexerConfig._settings = new SettingReader((SettingProvider)IndexerConfig._settingWrapper, IndexerConfig._logger);
        final String indexPath = IndexerConfig._settings.checkSetting(System.getProperty("com.vontu.index.dir"), "com.vontu.index.dir", "../index");
        final File indexFolder = new File(indexPath);
        if ((indexFolder.exists() && !indexFolder.isDirectory()) || (!indexFolder.exists() && !indexFolder.mkdirs())) {
            throw new ConfigurationException("The value \"" + indexPath + "\" doesn't specify a valid folder.");
        }
        initUnicodeNormalization((SettingProvider)IndexerConfig._settingWrapper);
        IndexerConfig._indexFolder = indexFolder;
        IndexerConfig._isLoaded = true;
    }
    
    public static synchronized void loadForTesting(final SettingProvider settingProvider) {
        IndexerConfig._settingWrapper = new SettingWrapper(settingProvider, IndexerConfig._classPath);
        IndexerConfig._settings = new SettingReader((SettingProvider)IndexerConfig._settingWrapper, IndexerConfig._logger);
        final String indexPath = IndexerConfig._settings.checkSetting(System.getProperty("com.vontu.index.dir"), "com.vontu.index.dir", "../index");
        final File indexFolder = new File(indexPath);
        if ((indexFolder.exists() && !indexFolder.isDirectory()) || (!indexFolder.exists() && !indexFolder.mkdirs())) {
            throw new ConfigurationException("The value \"" + indexPath + "\" doesn't specify a valid folder.");
        }
        initUnicodeNormalization((SettingProvider)IndexerConfig._settingWrapper);
        IndexerConfig._indexFolder = indexFolder;
        IndexerConfig._isLoaded = true;
    }
    
    private static synchronized void initUnicodeNormalization(final SettingProvider settings) {
        try {
            final UnicodeNormalizationConfigLoader configLoader = new UnicodeNormalizationConfigLoader();
            configLoader.loadInternalFile();
            final UnicodeNormalizer normalizer = new UnicodeNormalizer(settings, configLoader.getNormalizations());
            final AsianTokenUtil asianTokenUtils = new AsianTokenUtil(settings);
            UnicodeNormalizerRegistry.setInstance(new UnicodeNormalizerRegistry(normalizer, asianTokenUtils));
        }
        catch (IOException ioException) {
            throw new ConfigurationException("Error Reading Unicode Normalization Config File", (Throwable)ioException);
        }
        catch (UnicodeNormalizationConfigLoaderException configException) {
            throw new ConfigurationException("Malformed Unicode Normalization Config File", (Throwable)configException);
        }
    }
    
    public static synchronized boolean isLoaded() {
        return IndexerConfig._isLoaded;
    }
    
    public static SettingProvider getSettingProvider() {
        return (SettingProvider)IndexerConfig._settingWrapper;
    }
    
    static {
        _logger = Logger.getLogger(IndexerConfig.class.getName());
        IndexerConfig._isLoaded = false;
    }
    
    private static final class JarFilter implements FilenameFilter
    {
        private final File _libFolder;
        
        JarFilter(final File libFolder) {
            this._libFolder = libFolder;
        }
        
        @Override
        public boolean accept(final File dir, final String name) {
            return dir.equals(this._libFolder) && name.toLowerCase().endsWith("jar");
        }
    }
}
