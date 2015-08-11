// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.db;

import com.vontu.util.ProtectError;
import com.vontu.util.ProtectException;

public class DBUtilException extends ProtectException
{
    public DBUtilException(final ProtectError error, final Throwable reason, final String message) {
        super(error, reason, message);
    }
    
    public DBUtilException(final ProtectError error) {
        super(error);
    }
    
    public DBUtilException(final ProtectError error, final String message) {
        super(error, message);
    }
    
    public DBUtilException(final ProtectError error, final Throwable reason) {
        super(error, reason);
    }
}
