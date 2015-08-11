// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

public interface FilesMonitoringStatistics
{
    void startTimer();
    
    void incrementFilesLoaded(int p0);
    
    void incrementFilesAddedToConsumerQueue(Object p0);
    
    void stopTimer();
}
