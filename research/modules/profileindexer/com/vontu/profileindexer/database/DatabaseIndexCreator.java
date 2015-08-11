// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.profileindexer.database.ramindex.RamIndexResult;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import com.vontu.profileindexer.database.ramindex.CryptoIndexDescriptor;
import com.vontu.util.Stopwatch;
import java.io.PrintWriter;
import java.io.Reader;
import com.vontu.profileindexer.database.ramindex.IndexCreator;
import com.vontu.profileindexer.database.ramindex.OutOfProcessIndexCreator;
import com.vontu.profileindexer.database.ramindex.RamIndexCreator;
import java.io.File;
import com.vontu.keystorehouse.KeyContainer;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;

public class DatabaseIndexCreator
{
    public static final String WORK_FOLDER_PROPERTY = "work_folder";
    public static final String USE_OOP_INDEXER_PROPERTY = "use_out_of_process_indexer";
    public static final boolean USE_OOP_INDEXER_DEFAULT = true;
    public static final String FILESTREAM_OUTPUT_BUFFER_SIZE_PROPERTY = "filestream_output_buffer_size";
    public static final int FILESTREAM_OUTPUT_BUFFER_SIZE_DEFAULT = 32768;
    public static final String RETAIN_CRYPTO_FILE_PROPERTY = "retain_crypto_file";
    public static final boolean RETAIN_CRYPTO_FILE_DEFAULT = false;
    private static final Logger _logger;
    private static final String IDX_EXTENSION = ".idx";
    private final SettingProvider _settingProvider;
    private final SettingReader _settingReader;
    private final KeyContainer _key;
    private static final int NUM_RETRIES = 2;
    
    public DatabaseIndexCreator(final SettingProvider settingProvider, final KeyContainer key) throws IllegalArgumentException {
        checkNullArgument(settingProvider, "settingProvider");
        checkNullArgument(key, "key");
        this._settingProvider = settingProvider;
        this._settingReader = new SettingReader(this._settingProvider, DatabaseIndexCreator._logger);
        this._key = key;
    }
    
    private static void checkNullArgument(final Object argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException("Argument " + name + " is null.");
        }
    }
    
    private String getWorkFolderPath() {
        return this._settingProvider.getSetting("work_folder");
    }
    
    private static File getCryptoIndexFile(final File indexFile, final String workFolderPath) {
        if (workFolderPath == null) {
            DatabaseIndexCreator._logger.info("No work_folder value is provided. Using " + indexFile.getParent() + " as the work folder.");
            return new File(indexFile.getParentFile(), getCryptoIndexFileName(indexFile));
        }
        return new File(workFolderPath, getCryptoIndexFileName(indexFile));
    }
    
    private static String getCryptoIndexFileName(final File indexFile) {
        final String indexFileName = indexFile.getName();
        final int extensionBegin = indexFileName.lastIndexOf(46);
        final String baseFileName = (extensionBegin == -1) ? indexFileName : indexFileName.substring(0, extensionBegin);
        return baseFileName + ".idx";
    }
    
    private boolean shouldIndexOutOfProcess() {
        return this._settingReader.getBooleanSetting("use_out_of_process_indexer", true);
    }
    
    private boolean shouldRetainCryptoIndexFile() {
        return this._settingReader.getBooleanSetting("retain_crypto_file", false);
    }
    
    private int filestreamOutputBufferSize() {
        return this._settingReader.getIntSetting("filestream_output_buffer_size", 32768);
    }
    
    private RamIndexCreator newRamIndexCreator() {
        if (this.shouldIndexOutOfProcess()) {
            return new OutOfProcessIndexCreator(this._settingProvider);
        }
        return new IndexCreator(this._settingProvider);
    }
    
    public DatabaseIndexCreatorResult createIndex(final DatabaseProfileDescriptor descriptor, final Reader dataReader, final File indexFile, final PrintWriter errorWriter) throws IndexerException, InterruptedException, IllegalArgumentException {
        checkNullArgument(descriptor, "descriptor");
        checkNullArgument(dataReader, "dataReader");
        checkNullArgument(indexFile, "indexFile");
        final Stopwatch stopwatch = new Stopwatch("DatabaseIndexCreator");
        stopwatch.start();
        final CryptoIndexCreator cryptoIndexCreator = new CryptoIndexCreator(this._settingProvider, descriptor, this._key);
        final File cryptoIndexFile = getCryptoIndexFile(indexFile, this.getWorkFolderPath());
        final RamIndexCreator ramIndexCreator = this.newRamIndexCreator();
        try {
            DatabaseIndexCreator._logger.fine("Size of stream output buffer for writing crypto hash file is: " + this.filestreamOutputBufferSize());
            CryptoIndexResult cryptoIndexResult = null;
            RamIndexResult ramIndexResult = null;
            int attempt = 0;
            while (true) {
                try {
                    cryptoIndexResult = cryptoIndexCreator.createIndex(dataReader, errorWriter, cryptoIndexFile, this.filestreamOutputBufferSize());
                    DatabaseIndexCreator._logger.info("Crypto hashing of profile \"" + descriptor.name() + "\" took " + cryptoIndexResult.elapsedTime() + " milliseconds.");
                    ramIndexResult = ramIndexCreator.createRamIndex(new CryptoIndexDescriptor(cryptoIndexResult), indexFile);
                    DatabaseIndexCreator._logger.info("Creating of RAM index image(s) of profile \"" + descriptor.name() + "\" took " + ramIndexResult.elapsedTime() + "  milliseconds.");
                }
                catch (IndexerException e) {
                    if (e.getError() == IndexerError.RAM_INDEX_CREATE_ERROR_WILL_RETRY && attempt++ < 2) {
                        DatabaseIndexCreator._logger.severe("Attemp #" + attempt + " to create index for " + descriptor.name() + "failed with known EOFException.");
                        cryptoIndexFile.delete();
                        continue;
                    }
                    throw e;
                }
                break;
            }
            return new DatabaseIndexCreatorResult(cryptoIndexResult, ramIndexResult, stopwatch.stop().getLastTime());
        }
        finally {
            if (!this.shouldRetainCryptoIndexFile()) {
                cryptoIndexFile.delete();
            }
        }
    }
    
    static {
        _logger = Logger.getLogger(DatabaseIndexCreator.class.getName());
    }
}
