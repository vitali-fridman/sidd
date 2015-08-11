// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.LinkedList;

public final class QueueingTask implements Task
{
    private final LinkedList<Runnable> _requestQueue;
    
    public QueueingTask() {
        this._requestQueue = new LinkedList<Runnable>();
    }
    
    public void queueRequest(final Runnable request) {
        synchronized (this._requestQueue) {
            this._requestQueue.add(request);
        }
    }
    
    @Override
    public boolean hasWork() {
        synchronized (this._requestQueue) {
            return this._requestQueue.size() > 0;
        }
    }
    
    @Override
    public void performWork() {
        final Runnable request;
        synchronized (this._requestQueue) {
            request = this._requestQueue.removeFirst();
        }
        request.run();
    }
    
    public int getQueueSize() {
        synchronized (this._requestQueue) {
            return this._requestQueue.size();
        }
    }
}
