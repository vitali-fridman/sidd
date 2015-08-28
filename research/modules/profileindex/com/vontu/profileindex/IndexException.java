// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

import com.vontu.util.ProtectError;
import com.vontu.util.ProtectException;

public class IndexException extends ProtectException
{
    public IndexException(final IndexError error) {
        super((ProtectError)error, error.getDescription());
    }
    
    public IndexException(final IndexError error, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription());
    }
    
    public IndexException(final IndexError error, final Object oneParameter) {
        super((ProtectError)error, error.getDescription(new Object[] { oneParameter }));
    }
    
    public IndexException(final IndexError error, final Object oneParameter, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription(new Object[] { oneParameter }));
    }
    
    public IndexException(final IndexError error, final Object[] parameters) {
        super((ProtectError)error, error.getDescription(parameters));
    }
    
    public IndexException(final IndexError error, final Object[] parameters, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription(parameters));
    }
}
