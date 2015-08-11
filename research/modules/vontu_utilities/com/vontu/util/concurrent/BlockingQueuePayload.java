// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

public class BlockingQueuePayload<T>
{
    private final T _item;
    private final Throwable _throwable;
    
    public static <T> BlockingQueuePayload<T> getInstance(final T item) {
        return new BlockingQueuePayload<T>(item, null);
    }
    
    public static <T> BlockingQueuePayload<T> getThrowableInstance(final Throwable t) {
        return new BlockingQueuePayload<T>(null, t);
    }
    
    public static <T> BlockingQueuePayload<T> getEndOfQueueInstance() {
        return new BlockingQueuePayload<T>(null, null);
    }
    
    private BlockingQueuePayload(final T item, final Throwable throwable) {
        this._throwable = throwable;
        this._item = item;
    }
    
    public T getItem() {
        return this._item;
    }
    
    public Throwable getThrowable() {
        return this._throwable;
    }
    
    public boolean isEndOfQueueMarker() {
        return this.getItem() == null;
    }
}
