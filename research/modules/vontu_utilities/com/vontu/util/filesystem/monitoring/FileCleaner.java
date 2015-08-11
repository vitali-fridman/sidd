// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.io.File;

class FileCleaner implements FilesMonitoringConsumer
{
    @Override
    public int getQueueSize(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        return 0;
    }
    
    @Override
    public int processAll(final String[] filesAvailable, final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        int numProcessedSuccessfully = 0;
        for (final String tempFile : filesAvailable) {
            final File file = new File(tempFile);
            final boolean wasDeleted = this.deleteFile(fileMonitoringTaskInfo, file);
            if (wasDeleted) {
                ++numProcessedSuccessfully;
            }
        }
        return numProcessedSuccessfully;
    }
    
    protected boolean deleteFile(final FileMonitoringTaskInfo fileMonitoringTaskInfo, final File fileToBeDeleted) {
        final boolean wasDeleted = fileToBeDeleted.delete();
        if (wasDeleted) {
            if (FolderCleanupTask.logger.isLoggable(Level.FINE)) {
                FolderCleanupTask.logger.log(Level.FINE, MessageFormat.format("Removed file {0} from the folder {1} for the task {2}.", fileToBeDeleted.getName(), fileMonitoringTaskInfo.getTargetFolder().getAbsolutePath(), fileMonitoringTaskInfo.getTaskName()));
            }
        }
        else {
            FolderCleanupTask.logger.log(Level.SEVERE, MessageFormat.format("Error encountered in removing the file {0} from the folder {1} for the sub-system {2}.", fileToBeDeleted.getName(), fileMonitoringTaskInfo.getTargetFolder().getAbsolutePath(), fileMonitoringTaskInfo.getTaskName()));
        }
        return wasDeleted;
    }
    
    @Override
    public boolean readyToConsumeFiles(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        return true;
    }
    
    @Override
    public void failedToListFiles(final FileMonitoringTaskInfo fileMonitoringTaskInfo) {
        if (FolderCleanupTask.logger.isLoggable(Level.FINE)) {
            FolderCleanupTask.logger.log(Level.FINE, MessageFormat.format("Error encountered in fetching the files from the folder '{0}' from for the task '{1}'.", fileMonitoringTaskInfo.getTargetFolder().getAbsolutePath(), fileMonitoringTaskInfo.getTaskName()));
        }
    }
}
