// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class ProtectRuntimeException extends RuntimeException
{
    private final ProtectError _error;
    
    public ProtectRuntimeException(final ProtectError errorCode) {
        this._error = errorCode;
    }
    
    public ProtectRuntimeException(final ProtectError errorCode, final Throwable cause) {
        super(cause);
        this._error = errorCode;
    }
    
    public ProtectRuntimeException(final ProtectError errorCode, final String message) {
        super(message);
        this._error = errorCode;
    }
    
    public ProtectRuntimeException(final ProtectError errorCode, final Throwable cause, final String message) {
        super(message, cause);
        this._error = errorCode;
    }
    
    public ProtectRuntimeException(final ParameterizedError error, final Object... parameters) {
        super(error.getDescription(parameters));
        this._error = error;
    }
    
    public ProtectRuntimeException(final ParameterizedError error, final Throwable cause, final Object... parameters) {
        super(error.getDescription(parameters), cause);
        this._error = error;
    }
    
    public ProtectError getError() {
        return this._error;
    }
}
