// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.timeprovider;

public interface CurrentTimeProvider
{
    long getCurrentTimeInSeconds();
    
    long getCurrentTimeInMillis();
    
    long getCurrentNanoTime();
}
