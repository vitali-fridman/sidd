// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

public class UnicodeNormalizationConfigLoaderException extends Exception
{
    private final ErrorType _errorType;
    private int _lineNumber;
    
    public UnicodeNormalizationConfigLoaderException(final String str, final ErrorType errorType) {
        super(str);
        this._errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return this._errorType;
    }
    
    public int getLineNumber() {
        return this._lineNumber;
    }
    
    public void setLineNumber(final int lineNumber) {
        this._lineNumber = lineNumber;
    }
    
    public enum ErrorType
    {
        INVALID_RANGE, 
        INVALID_CODE_POINT, 
        MALFORMED_LINE;
    }
}
