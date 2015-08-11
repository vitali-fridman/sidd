// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.io.FilenameFilter;
import com.vontu.util.filesystem.DirectoryPermissionsDeterminer;
import java.io.File;
import java.util.logging.Logger;
import java.util.TimerTask;

public class FilesMonitoringTask extends TimerTask implements FileMonitoringTaskInfo
{
    private static final Logger logger;
    protected final String fileMonitorTaskName;
    protected final File targetFolder;
    private final FilesMonitoringConsumer consumer;
    private final FilesMonitoringStatistics statistics;
    private final DirectoryPermissionsDeterminer directoryPermissionsDeterminer;
    private FilenameFilter filenameFilter;
    
    public FilesMonitoringTask(final String taskName, final File targetFolder, final FilenameFilter filter, final FilesMonitoringConsumer consumer, final FilesMonitoringStatistics statistics) {
        this(taskName, targetFolder, filter, consumer, new DirectoryPermissionsDeterminer(), statistics);
    }
    
    public FilesMonitoringTask(final String subSystemName, final File catalogItemsFolder, final FilenameFilter filter, final FilesMonitoringConsumer consumer) {
        this(subSystemName, catalogItemsFolder, filter, consumer, new DirectoryPermissionsDeterminer(), null);
    }
    
    FilesMonitoringTask(final String taskName, final File targetFolder, final FilenameFilter filter, final FilesMonitoringConsumer consumer, final DirectoryPermissionsDeterminer directoryPermissionsDeterminer, final FilesMonitoringStatistics statistics) {
        this.fileMonitorTaskName = taskName;
        this.targetFolder = targetFolder;
        this.filenameFilter = filter;
        this.consumer = consumer;
        this.statistics = statistics;
        this.directoryPermissionsDeterminer = directoryPermissionsDeterminer;
        FilesMonitoringTask.logger.info("Successfully initialized file monitoring task '" + taskName + "' for the folder " + targetFolder.getAbsolutePath() + ".");
    }
    
    @Override
    public String getTaskName() {
        return this.fileMonitorTaskName;
    }
    
    @Override
    public File getTargetFolder() {
        return this.targetFolder;
    }
    
    @Override
    public void run() {
        try {
            if (this.statistics != null) {
                this.statistics.startTimer();
            }
            if (this.consumer.readyToConsumeFiles(this)) {
                this.consumeFiles(this.getFilesList());
            }
            else if (FilesMonitoringTask.logger.isLoggable(Level.FINER)) {
                FilesMonitoringTask.logger.log(Level.FINER, "File monitoring task " + this.fileMonitorTaskName + "'s file Consumer still has " + this.consumer.getQueueSize(this) + " items in it, skipping execution");
            }
        }
        catch (Throwable t) {
            FilesMonitoringTask.logger.log(Level.SEVERE, "Error enountered by " + this.fileMonitorTaskName + " while adding files to Queue ", t);
        }
        finally {
            if (this.statistics != null) {
                this.statistics.stopTimer();
            }
        }
    }
    
    void consumeFiles(final String[] filesArray) {
        if (filesArray == null) {
            FilesMonitoringTask.logger.log(Level.SEVERE, "The file monitoring task {0} encountered error in getting the files for the folder {1}.", new Object[] { this.fileMonitorTaskName, this.targetFolder.getName() });
            this.consumer.failedToListFiles(this);
            return;
        }
        if (this.statistics != null) {
            this.statistics.incrementFilesLoaded(filesArray.length);
        }
        if (FilesMonitoringTask.logger.isLoggable(Level.FINEST)) {
            FilesMonitoringTask.logger.log(Level.FINEST, "Found " + filesArray.length + " files(s) to be added to file monitoring" + this.fileMonitorTaskName + "'s  consumer");
        }
        final int processedFilesCount = this.consumer.processAll(filesArray, this);
        if (FilesMonitoringTask.logger.isLoggable(Level.FINEST)) {
            FilesMonitoringTask.logger.log(Level.FINEST, "Added " + processedFilesCount + " file(s) to file monitoring" + this.fileMonitorTaskName + "'s consumer");
        }
        if (this.statistics != null) {
            this.statistics.incrementFilesAddedToConsumerQueue(processedFilesCount);
        }
    }
    
    @Override
    public boolean cancel() {
        FilesMonitoringTask.logger.log(Level.INFO, "File monitoring task '" + this.fileMonitorTaskName + "' Recieved shutdown/cancel notification.");
        return super.cancel();
    }
    
    public String[] getFilesList() throws FilesMonitoringException {
        if (FilesMonitoringTask.logger.isLoggable(Level.FINEST)) {
            FilesMonitoringTask.logger.log(Level.FINEST, "File monitoring task '" + this.fileMonitorTaskName + "' getFileList().");
        }
        if (!this.directoryPermissionsDeterminer.verifyDirectoryPersmissions(this.targetFolder)) {
            final String message = MessageFormat.format("File Poller is wrongly configured. Please verify that the directory '{0}' is a directory with read and write permissions on it.", this.targetFolder.getAbsolutePath());
            FilesMonitoringTask.logger.log(Level.SEVERE, message);
            throw new FilesMonitoringException(message);
        }
        final String[] filesList = this.targetFolder.list(this.filenameFilter);
        return filesList;
    }
    
    static {
        logger = Logger.getLogger(FilesMonitoringTask.class.getName());
    }
}
