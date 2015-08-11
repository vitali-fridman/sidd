// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

class TimedThreadPoolWatcherTimerTask extends QueueableTimerTask
{
    private TimedRunnable _timedRunnable;
    
    public TimedThreadPoolWatcherTimerTask(final TimedRunnable timedRunnable) {
        this._timedRunnable = timedRunnable;
    }
    
    @Override
    public void run() {
        this._timedRunnable.hasExpired();
    }
}
