// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

public class DatabaseRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 8207319074166702363L;
    
    public DatabaseRuntimeException(final String message) {
        super(message);
    }
    
    public DatabaseRuntimeException(final Throwable cause) {
        super(cause);
    }
    
    public DatabaseRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
