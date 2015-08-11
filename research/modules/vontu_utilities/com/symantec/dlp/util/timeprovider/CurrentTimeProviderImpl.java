// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.timeprovider;

import java.util.concurrent.TimeUnit;

public class CurrentTimeProviderImpl implements CurrentTimeProvider
{
    @Override
    public long getCurrentTimeInSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }
    
    @Override
    public long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
    
    @Override
    public long getCurrentNanoTime() {
        return System.nanoTime();
    }
}
