// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class ProtectException extends Exception
{
    private ProtectError _error;
    
    public ProtectException(final ProtectError error, final Throwable reason, final String message) {
        super(message, reason);
        this._error = error;
    }
    
    public ProtectException(final ProtectError error) {
        super(String.valueOf(error));
        this._error = error;
    }
    
    public ProtectException(final ProtectError error, final String message) {
        super(message);
        this._error = error;
    }
    
    public ProtectException(final ProtectError error, final Throwable reason) {
        super(String.valueOf(error), reason);
        this._error = error;
    }
    
    public final ProtectError getError() {
        return this._error;
    }
}
