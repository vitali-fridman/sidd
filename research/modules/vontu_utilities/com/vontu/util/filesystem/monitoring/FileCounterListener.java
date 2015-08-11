// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

public interface FileCounterListener
{
    void failedToListFiles(FileMonitoringTaskInfo p0);
    
    void fileCountChanged(int p0, FileMonitoringTaskInfo p1);
    
    void thresholdReached(int p0, FileMonitoringTaskInfo p1);
    
    void belowTheThreshold(int p0, FileMonitoringTaskInfo p1);
}
