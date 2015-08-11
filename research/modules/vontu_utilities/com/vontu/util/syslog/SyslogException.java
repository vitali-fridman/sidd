// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.syslog;

public class SyslogException extends Exception
{
    public SyslogException(final String message) {
        super(message);
    }
    
    SyslogException(final String message, final Throwable Cause) {
        super(message, Cause);
    }
}
