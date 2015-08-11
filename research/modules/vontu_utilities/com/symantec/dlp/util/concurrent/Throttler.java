// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

public interface Throttler
{
    void setWorkPerTimePeriodLimit(long p0);
    
    long calculateDelay(long p0);
    
    void addWorkDone(long p0);
}
