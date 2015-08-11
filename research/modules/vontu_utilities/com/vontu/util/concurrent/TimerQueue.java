// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class TimerQueue
{
    private final String _threadName;
    private final ScheduledThreadPoolExecutor _executor;
    
    public TimerQueue(final String threadName) {
        this._threadName = threadName;
        final ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread timerThread = new Thread(r, TimerQueue.this._threadName);
                timerThread.setDaemon(true);
                return timerThread;
            }
        };
        this._executor = new ScheduledThreadPoolExecutor(1, factory);
    }
    
    synchronized void shutdown() {
        this._executor.shutdown();
    }
    
    public void addTimerExpiringAt(final QueueableTimerTask task, final Date date) {
        this.addTimerExpiringIn(task, date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    
    public void addTimerExpiringIn(final QueueableTimerTask task, final long fromNow, final TimeUnit unit) {
        final ScheduledFuture future = this._executor.schedule(task, fromNow, unit);
        task.setFuture(future);
    }
    
    public synchronized void removeTimerTask(final QueueableTimerTask task) {
        this._executor.remove(task);
        final ScheduledFuture future = task.getFuture();
        if (future != null) {
            future.cancel(false);
        }
    }
}
