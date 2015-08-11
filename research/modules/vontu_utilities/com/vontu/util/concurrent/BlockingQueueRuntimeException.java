// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

public class BlockingQueueRuntimeException extends RuntimeException
{
    public BlockingQueueRuntimeException() {
    }
    
    public BlockingQueueRuntimeException(final String message) {
        super(message);
    }
    
    public BlockingQueueRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public BlockingQueueRuntimeException(final Throwable cause) {
        super(cause);
    }
}
