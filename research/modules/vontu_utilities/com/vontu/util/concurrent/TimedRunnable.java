// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TimedRunnable implements Runnable
{
    private static final Logger _logger;
    private final long _timeout;
    private final QueueableTimerTask _timerTask;
    private Boolean _isDone;
    
    public void hasExpired() {
        if (!this._isDone) {
            this._isDone = true;
            this.timeout();
        }
    }
    
    protected TimedRunnable(final long timeout) {
        this._timerTask = new TimedThreadPoolWatcherTimerTask(this);
        this._isDone = false;
        this._timeout = timeout;
    }
    
    public QueueableTimerTask getTimerTask() {
        return this._timerTask;
    }
    
    protected void started() {
    }
    
    protected void finished() {
        this._isDone = true;
    }
    
    protected void reportException(final Throwable t) {
        TimedRunnable._logger.log(Level.SEVERE, "Uncaught RuntimeException or Error from Runnable: ", t);
    }
    
    public long getTimeout() {
        return this._timeout;
    }
    
    protected abstract void timeout();
    
    static {
        _logger = Logger.getLogger(TimedRunnable.class.getName());
    }
}
