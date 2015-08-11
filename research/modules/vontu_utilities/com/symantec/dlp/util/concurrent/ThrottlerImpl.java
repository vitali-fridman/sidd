// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

public class ThrottlerImpl implements Throttler
{
    private long workPerTimePeriodLimit;
    private long nanosecondsPerTimePeriod;
    private long currentlyAccumulatingForTimePeriod;
    private long accumulatedWorkInCurrentTimePeriod;
    
    public ThrottlerImpl(final long initialWorkPerTimePeriodLimit, final long nanosecondsPerTimePeriod) {
        this.workPerTimePeriodLimit = initialWorkPerTimePeriodLimit;
        this.nanosecondsPerTimePeriod = nanosecondsPerTimePeriod;
        this.currentlyAccumulatingForTimePeriod = System.nanoTime() / nanosecondsPerTimePeriod;
        this.accumulatedWorkInCurrentTimePeriod = 0L;
    }
    
    @Override
    public void setWorkPerTimePeriodLimit(final long workPerTimePeriodLimit) {
        this.workPerTimePeriodLimit = workPerTimePeriodLimit;
    }
    
    @Override
    public long calculateDelay(final long estimatedWorkToBeDone) {
        final long now = System.nanoTime();
        this.accountForThePassageOfTime(now);
        final long allowedWorkRemainingInTheCurrentTimePeriod = this.workPerTimePeriodLimit - this.accumulatedWorkInCurrentTimePeriod;
        if (this.workPerTimePeriodLimit == 0L || this.accumulatedWorkInCurrentTimePeriod == 0L || estimatedWorkToBeDone <= allowedWorkRemainingInTheCurrentTimePeriod) {
            return 0L;
        }
        final long startOfNextTimePeriodInNanoseconds = (this.currentlyAccumulatingForTimePeriod + 1L) * this.nanosecondsPerTimePeriod;
        final long nanosecondsUntilNextTimePeriod = startOfNextTimePeriodInNanoseconds - now;
        return nanosecondsUntilNextTimePeriod;
    }
    
    private void accountForThePassageOfTime(final long now) {
        final long nowTimePeriod = now / this.nanosecondsPerTimePeriod;
        if (nowTimePeriod != this.currentlyAccumulatingForTimePeriod) {
            this.currentlyAccumulatingForTimePeriod = nowTimePeriod;
            this.accumulatedWorkInCurrentTimePeriod = 0L;
        }
    }
    
    @Override
    public void addWorkDone(final long workDone) {
        this.accumulatedWorkInCurrentTimePeriod += workDone;
    }
}
