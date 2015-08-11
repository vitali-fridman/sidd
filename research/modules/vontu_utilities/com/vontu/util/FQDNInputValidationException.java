// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class FQDNInputValidationException extends Exception
{
    private static final long serialVersionUID = -2368700630282044554L;
    private FQDNInputValidationErrorEnum _errorCode;
    private String _inputValue;
    
    public FQDNInputValidationException(final FQDNInputValidationErrorEnum errorCode, final String inputValue) {
        this._errorCode = errorCode;
        this._inputValue = inputValue;
    }
    
    public FQDNInputValidationErrorEnum getErrorCode() {
        return this._errorCode;
    }
    
    public String getInputValue() {
        return this._inputValue;
    }
}
