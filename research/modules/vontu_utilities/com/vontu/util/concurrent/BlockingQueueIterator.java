// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.BlockingQueue;
import com.vontu.util.collection.IteratorRequiringCleanup;

public class BlockingQueueIterator<T> implements IteratorRequiringCleanup<T>
{
    private T _nextRecord;
    private final BlockingQueue<BlockingQueuePayload<T>> _queue;
    private final long _dequeueTimeoutMS;
    private final Future<?> _backgroundTaskFuture;
    
    public BlockingQueueIterator(final BlockingQueue<BlockingQueuePayload<T>> queue, final Future<?> backgroundTaskFuture, final long dequeueTimeoutMS) {
        this._nextRecord = null;
        this._queue = queue;
        this._dequeueTimeoutMS = dequeueTimeoutMS;
        this._backgroundTaskFuture = backgroundTaskFuture;
    }
    
    @Override
    public boolean hasNext() {
        if (this._nextRecord != null) {
            return true;
        }
        try {
            final BlockingQueuePayload<T> payload = this._queue.poll(this._dequeueTimeoutMS, TimeUnit.MILLISECONDS);
            if (payload == null) {
                this._backgroundTaskFuture.cancel(true);
                throw new BlockingQueueRuntimeException("Timeout [" + this._dequeueTimeoutMS + " ms] elapsed while" + " waiting to retrieve a violating message component " + "in " + this.getClass().getName());
            }
            if (!payload.isEndOfQueueMarker()) {
                this._nextRecord = payload.getItem();
                return true;
            }
            if (payload.getThrowable() != null) {
                throw new BlockingQueueRuntimeException(payload.getThrowable());
            }
            return false;
        }
        catch (InterruptedException ie) {
            this._backgroundTaskFuture.cancel(true);
            throw new BlockingQueueRuntimeException(ie);
        }
    }
    
    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final T result = this._nextRecord;
        this._nextRecord = null;
        return result;
    }
    
    @Override
    public void cleanUp() {
        this._backgroundTaskFuture.cancel(true);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
