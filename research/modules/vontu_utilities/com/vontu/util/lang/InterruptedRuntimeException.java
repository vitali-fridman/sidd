// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.lang;

public class InterruptedRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private final InterruptedException interruptedException;
    
    public InterruptedRuntimeException(final InterruptedException interruptedException) {
        super(interruptedException);
        this.interruptedException = interruptedException;
    }
    
    public InterruptedException getInterruptedException() {
        return this.interruptedException;
    }
}
