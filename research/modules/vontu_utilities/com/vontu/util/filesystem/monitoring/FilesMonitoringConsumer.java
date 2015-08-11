// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

public interface FilesMonitoringConsumer
{
    int getQueueSize(FileMonitoringTaskInfo p0);
    
    int processAll(String[] p0, FileMonitoringTaskInfo p1);
    
    boolean readyToConsumeFiles(FileMonitoringTaskInfo p0);
    
    void failedToListFiles(FileMonitoringTaskInfo p0);
}
