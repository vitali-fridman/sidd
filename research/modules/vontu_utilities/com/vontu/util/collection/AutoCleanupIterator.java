// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.Iterator;

public class AutoCleanupIterator<E> implements Iterator<E>
{
    private static final long DEFAULT_ENQUEUE_TIMEOUT = 30000L;
    private static final long DEFAULT_DEQUEUE_TIMEOUT = 120000L;
    private static final int DEFAULT_QUEUE_SIZE = 10;
    private final IteratorRequiringCleanup<E> _messyIterator;
    private final BlockingQueue<Payload<E>> _queue;
    private final long _enqueueTimeout;
    private final long _dequeueTimeout;
    private Payload<E> _nextItem;
    private volatile AutoCleanupIteratorException _exception;
    private boolean _started;
    private final Logger _logger;
    
    public AutoCleanupIterator(final IteratorRequiringCleanup<E> messyIterator) {
        this(messyIterator, 10, 30000L, 120000L);
    }
    
    public AutoCleanupIterator(final IteratorRequiringCleanup<E> messyIterator, final int queueSize, final long enqueueTimeoutMS, final long dequeueTimeoutMS) {
        this._started = false;
        this._logger = Logger.getLogger(AutoCleanupIterator.class.getName());
        if (queueSize < 1) {
            throw new IllegalArgumentException("Not a valid queue size: " + queueSize);
        }
        this._messyIterator = messyIterator;
        this._queue = new LinkedBlockingQueue<Payload<E>>(queueSize);
        this._enqueueTimeout = enqueueTimeoutMS;
        this._dequeueTimeout = dequeueTimeoutMS;
    }
    
    @Override
    public boolean hasNext() {
        this.grabItem();
        return !this._nextItem.isFinalElement();
    }
    
    @Override
    public E next() {
        this.grabItem();
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final E result = this._nextItem.getItem();
        this._nextItem = null;
        return result;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private void grabItem() {
        if (!this._started) {
            this.startProducerThread();
        }
        if (this._nextItem != null) {
            return;
        }
        try {
            this._nextItem = this._queue.poll(this._dequeueTimeout, TimeUnit.MILLISECONDS);
            this.checkException();
            if (this._nextItem == null) {
                throw new AutoCleanupIteratorException("AutoCleanupIterator timeout elapsed without finding anything on the queue.");
            }
        }
        catch (InterruptedException e) {
            throw new AutoCleanupIteratorException(e);
        }
    }
    
    private void startProducerThread() {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (AutoCleanupIterator.this._messyIterator.hasNext()) {
                        final E item = AutoCleanupIterator.this._messyIterator.next();
                        AutoCleanupIterator.this.insertPayload(item);
                    }
                }
                catch (AutoCleanupIteratorException e) {
                    AutoCleanupIterator.this.setException(e);
                }
                catch (Throwable e2) {
                    AutoCleanupIterator.this.setException(new AutoCleanupIteratorException(e2));
                }
                finally {
                    try {
                        AutoCleanupIterator.this._messyIterator.cleanUp();
                        AutoCleanupIterator.this.insertPayload(null);
                    }
                    catch (AutoCleanupIteratorException e3) {
                        AutoCleanupIterator.this.setException(e3);
                    }
                    catch (Throwable e4) {
                        AutoCleanupIterator.this.setException(new AutoCleanupIteratorException(e4));
                    }
                }
            }
        }, "AutoCleanupIterator producer thread for " + this._messyIterator.getClass().getName());
        t.start();
        this._started = true;
    }
    
    private void setException(final AutoCleanupIteratorException e) {
        this._exception = e;
        this._logger.log(Level.WARNING, "Exception in AutoCleanupIterator's producer thread", e);
    }
    
    private void checkException() {
        if (this._exception != null) {
            throw this._exception;
        }
    }
    
    private void insertPayload(final E item) throws InterruptedException {
        if (!this._queue.offer(new Payload<E>(item), this._enqueueTimeout, TimeUnit.MILLISECONDS)) {
            throw new AutoCleanupIteratorException("AutoCleanupIterator queue is full [" + this._queue.size() + "]. " + "Producer couldn't insert an element within " + "the allowed time [" + this._enqueueTimeout + " ms]");
        }
    }
    
    private static class Payload<E>
    {
        private E _item;
        
        Payload(final E item) {
            this._item = item;
        }
        
        public E getItem() {
            return this._item;
        }
        
        public boolean isFinalElement() {
            return this._item == null;
        }
    }
}
