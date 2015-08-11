// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer;

import java.util.ResourceBundle;
import com.vontu.util.ParameterizedError;

public final class IndexerError extends ParameterizedError
{
    public static final IndexerError CRYPTO_FILE_WRITE_ERROR;
    public static final IndexerError PROFILE_DATA_CORRUPTED;
    public static final IndexerError MAXIMUM_SIZE_EXCEEDED;
    public static final IndexerError KEYSTORE_ERROR;
    public static final IndexerError CRYPTOGRAPHIC_ERROR;
    public static final IndexerError RAM_INDEX_CREATE_ERROR;
    public static final IndexerError PROCESS_SHUTDOWN_ERROR;
    public static final IndexerError UNEXPECTED_PROCESS_EXIT;
    public static final IndexerError PROCESS_IO_ERROR;
    public static final IndexerError RAM_INDEX_CREATE_ERROR_WILL_RETRY;
    public static final IndexerError GENERIC_COULD_NOT_CREATE_JVM;
    public static final IndexerError NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP;
    public static final IndexerError CHARACTER_ENCODING_ERROR;
    public static final IndexerError NOT_ENOUGH_DISK_SPACE;
    public static final IndexerError UNEXPECTED_OUT_OF_MEMORY_ERROR;
    private static final ResourceBundle _messages;
    
    private IndexerError(final int value) throws IllegalArgumentException {
        super(value);
    }
    
    protected String getMessageTemplate() {
        return IndexerError._messages.getString(String.valueOf(this.getValue()));
    }
    
    static {
        CRYPTO_FILE_WRITE_ERROR = new IndexerError(1300);
        PROFILE_DATA_CORRUPTED = new IndexerError(1301);
        MAXIMUM_SIZE_EXCEEDED = new IndexerError(1302);
        KEYSTORE_ERROR = new IndexerError(1303);
        CRYPTOGRAPHIC_ERROR = new IndexerError(1304);
        RAM_INDEX_CREATE_ERROR = new IndexerError(1306);
        PROCESS_SHUTDOWN_ERROR = new IndexerError(1307);
        UNEXPECTED_PROCESS_EXIT = new IndexerError(1308);
        PROCESS_IO_ERROR = new IndexerError(1309);
        RAM_INDEX_CREATE_ERROR_WILL_RETRY = new IndexerError(1312);
        GENERIC_COULD_NOT_CREATE_JVM = new IndexerError(1313);
        NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP = new IndexerError(1314);
        CHARACTER_ENCODING_ERROR = new IndexerError(1315);
        NOT_ENOUGH_DISK_SPACE = new IndexerError(1316);
        UNEXPECTED_OUT_OF_MEMORY_ERROR = new IndexerError(1317);
        _messages = ResourceBundle.getBundle(IndexerError.class.getName());
    }
}
