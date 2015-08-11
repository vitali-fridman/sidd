// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

class FileCountConsumer implements FilesMonitoringConsumer
{
    private static final Logger logger;
    private FileCounterListener listener;
    private final int fileCountThreshold;
    private int previousBacklog;
    
    public FileCountConsumer(final FileCounterListener listener, final int fileCountThreshold) {
        this.previousBacklog = -1;
        this.listener = listener;
        this.fileCountThreshold = fileCountThreshold;
    }
    
    @Override
    public int getQueueSize(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        return 0;
    }
    
    @Override
    public int processAll(final String[] filesAvailable, final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        FileCountConsumer.logger.finest("Entering processAll() method.");
        final File targetFolder = fileMonitoringTaskInfo.getTargetFolder();
        final String taskName = fileMonitoringTaskInfo.getTaskName();
        try {
            final int currentBacklogCount = filesAvailable.length;
            if (this.hasBacklogSizeChanged(currentBacklogCount)) {
                this.previousBacklog = currentBacklogCount;
                this.listener.fileCountChanged(currentBacklogCount, fileMonitoringTaskInfo);
                if (currentBacklogCount >= this.fileCountThreshold) {
                    FileCountConsumer.logger.log(Level.FINE, "The file count reached the configured maximum of {0} for the folder {1} for {2}", new Object[] { this.fileCountThreshold, targetFolder.getName(), taskName });
                    this.listener.thresholdReached(currentBacklogCount, fileMonitoringTaskInfo);
                }
                else {
                    FileCountConsumer.logger.log(Level.FINE, "The file count reached below the configured maximum of {0} for the folder {1} for {2}", new Object[] { this.fileCountThreshold, targetFolder.getName(), taskName });
                    this.listener.belowTheThreshold(currentBacklogCount, fileMonitoringTaskInfo);
                }
            }
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error encountered by {0} while processing files from folder {1}. Reason: {2}", taskName, targetFolder.getName(), t.getMessage());
            FileCountConsumer.logger.log(Level.SEVERE, msg, t);
            return 0;
        }
        return filesAvailable.length;
    }
    
    @Override
    public boolean readyToConsumeFiles(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        return true;
    }
    
    @Override
    public void failedToListFiles(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        FileCountConsumer.logger.finest("Entering failedToListFiles() method for the task " + fileMonitoringTaskInfo.getTaskName() + " watching on " + fileMonitoringTaskInfo.getTargetFolder().getAbsolutePath());
        this.listener.failedToListFiles(fileMonitoringTaskInfo);
    }
    
    private boolean hasBacklogSizeChanged(final int backlog) {
        return this.previousBacklog != backlog;
    }
    
    static {
        logger = Logger.getLogger(FileCountConsumer.class.getName());
    }
}
