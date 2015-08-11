// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer;

import com.vontu.util.ProtectError;
import com.vontu.util.ProtectException;

public class IndexerException extends ProtectException
{
    public IndexerException(final IndexerError error) {
        super((ProtectError)error, error.getDescription());
    }
    
    public IndexerException(final IndexerError error, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription());
    }
    
    public IndexerException(final IndexerError error, final Object oneParameter) {
        super((ProtectError)error, error.getDescription(new Object[] { oneParameter }));
    }
    
    public IndexerException(final IndexerError error, final Object oneParameter, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription(new Object[] { oneParameter }));
    }
    
    public IndexerException(final IndexerError error, final Object[] parameters) {
        super((ProtectError)error, error.getDescription(parameters));
    }
    
    public IndexerException(final IndexerError error, final Object[] parameters, final Throwable cause) {
        super((ProtectError)error, cause, error.getDescription(parameters));
    }
}
