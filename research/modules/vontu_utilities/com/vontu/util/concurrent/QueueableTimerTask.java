// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.ScheduledFuture;

public abstract class QueueableTimerTask implements Runnable
{
    private boolean _isRunning;
    private long _expirationTime;
    private QueueableTimerTask _nextTimer;
    private ScheduledFuture _future;
    
    public QueueableTimerTask() {
        this._future = null;
    }
    
    public ScheduledFuture getFuture() {
        return this._future;
    }
    
    public void setFuture(final ScheduledFuture future) {
        this._future = future;
    }
    
    public long getExpirationTime() {
        return this._expirationTime;
    }
    
    public void setExpirationTime(final long expirationTime) {
        this._expirationTime = expirationTime;
    }
    
    public QueueableTimerTask getNextTask() {
        return this._nextTimer;
    }
    
    public void setNextTask(final QueueableTimerTask nextTimer) {
        this._nextTimer = nextTimer;
    }
    
    public boolean isRunning() {
        return this._isRunning;
    }
    
    public void setRunning(final boolean running) {
        this._isRunning = running;
    }
}
