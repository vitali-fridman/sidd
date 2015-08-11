// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

public class SynchronizedThrottler implements Throttler
{
    private final Throttler throttler;
    private final Object lock;
    
    public SynchronizedThrottler(final Throttler throttler) {
        this.lock = new Object();
        this.throttler = throttler;
    }
    
    @Override
    public void setWorkPerTimePeriodLimit(final long workPerTimePeriodLimit) {
        synchronized (this.lock) {
            this.throttler.setWorkPerTimePeriodLimit(workPerTimePeriodLimit);
        }
    }
    
    @Override
    public long calculateDelay(final long estimatedWorkToBeDone) {
        synchronized (this.lock) {
            return this.throttler.calculateDelay(estimatedWorkToBeDone);
        }
    }
    
    @Override
    public void addWorkDone(final long workDone) {
        synchronized (this.lock) {
            this.throttler.addWorkDone(workDone);
        }
    }
}
