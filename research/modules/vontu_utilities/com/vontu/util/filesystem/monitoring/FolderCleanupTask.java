// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.util.logging.Level;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

public class FolderCleanupTask extends FilesMonitoringTask
{
    static final Logger logger;
    private FilenameFilter targetFileNameFilter;
    private FileCleaner fileCleaner;
    
    public FolderCleanupTask(final String taskName, final File targetFolder, final long fileExpirationPeriodInMilliSeconds, final FilenameFilter targetFileNameFilter) {
        this(taskName, targetFolder, new OldFileFilter(fileExpirationPeriodInMilliSeconds, targetFileNameFilter), targetFileNameFilter, new FileCleaner(), null);
    }
    
    FolderCleanupTask(final String taskName, final File targetFolder, final OldFileFilter oldFileFilter, final FilenameFilter targetFileNameFilter, final FileCleaner fileCleaner, final FilesMonitoringStatistics filesMonitoringStatistics) {
        super(taskName, targetFolder, oldFileFilter, fileCleaner, filesMonitoringStatistics);
        this.targetFileNameFilter = targetFileNameFilter;
        this.fileCleaner = fileCleaner;
    }
    
    public void deleteAllFiles() {
        if (FolderCleanupTask.logger.isLoggable(Level.FINE)) {
            FolderCleanupTask.logger.fine("deleteAllFiled() was called for the task " + this.getTaskName() + " for the folder " + this.getTargetFolder().getAbsolutePath() + ".");
        }
        this.fileCleaner.processAll(this.getTargetFolder().list(this.targetFileNameFilter), this);
    }
    
    static {
        logger = Logger.getLogger(FolderCleanupTask.class.getName());
    }
}
