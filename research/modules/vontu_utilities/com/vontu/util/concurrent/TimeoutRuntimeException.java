// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.TimeUnit;

public class TimeoutRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -1858336779289834830L;
    
    public TimeoutRuntimeException(final long timeout, final TimeUnit timeUnit, final String conditionMessage) {
        super("The timeout [" + timeout + " " + timeUnit + "] elapsed before " + conditionMessage + ".");
    }
}
