// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

import java.util.ResourceBundle;
import com.vontu.util.ParameterizedError;

public final class IndexError extends ParameterizedError
{
    public static final IndexError INDEX_CRYPTO_ERROR;
    public static final IndexError INDEX_LOAD_INTERRUPTED;
    public static final IndexError INDEX_UNLOAD_ERROR;
    public static final IndexError INDEX_LOOKUP_INTERRUPTED;
    public static final IndexError INDEX_NOT_ENOUGH_RAM;
    public static final IndexError INDEX_PROCESS_LAUNCH_ERROR;
    public static final IndexError INDEX_PROCESS_CONNECTION_ERROR;
    public static final IndexError INDEX_PROCESS_LOAD_ERROR;
    public static final IndexError INDEX_PROCESS_LOOKUP_ERROR;
    public static final IndexError INDEX_LOAD_ERROR;
    public static final IndexError INDEX_NOT_ENOUGH_RAM_TO_LOAD;
    public static final IndexError INDEX_NOT_ENOUGH_RAM_TO_DETECT;
    public static final IndexError INDEX_PROCESS_LOOKUP_RECOVERABLE_ERROR;
    private static final ResourceBundle _messages;
    
    private IndexError(final int value) throws IllegalArgumentException {
        super(value);
    }
    
    protected String getMessageTemplate() {
        return IndexError._messages.getString(String.valueOf(this.getValue()));
    }
    
    static {
        INDEX_CRYPTO_ERROR = new IndexError(1400);
        INDEX_LOAD_INTERRUPTED = new IndexError(1401);
        INDEX_UNLOAD_ERROR = new IndexError(1402);
        INDEX_LOOKUP_INTERRUPTED = new IndexError(1403);
        INDEX_NOT_ENOUGH_RAM = new IndexError(1404);
        INDEX_PROCESS_LAUNCH_ERROR = new IndexError(1405);
        INDEX_PROCESS_CONNECTION_ERROR = new IndexError(1406);
        INDEX_PROCESS_LOAD_ERROR = new IndexError(1407);
        INDEX_PROCESS_LOOKUP_ERROR = new IndexError(1408);
        INDEX_LOAD_ERROR = new IndexError(1409);
        INDEX_NOT_ENOUGH_RAM_TO_LOAD = new IndexError(1410);
        INDEX_NOT_ENOUGH_RAM_TO_DETECT = new IndexError(1411);
        INDEX_PROCESS_LOOKUP_RECOVERABLE_ERROR = new IndexError(1412);
        _messages = ResourceBundle.getBundle(IndexError.class.getName());
    }
}
