// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.text.MessageFormat;
import java.io.FilenameFilter;
import java.io.File;
import java.util.logging.Logger;

public class FileCountMonitoringTask extends FilesMonitoringTask
{
    private static final Logger logger;
    
    public FileCountMonitoringTask(final String taskName, final File targetFolder, final int fileCountThreshold, final FilenameFilter targetFileNameFilter, final FileCounterListener listener, final FilesMonitoringStatistics statistics) {
        super(taskName, targetFolder, targetFileNameFilter, new FileCountConsumer(listener, fileCountThreshold), statistics);
        FileCountMonitoringTask.logger.info(MessageFormat.format("The task '{0}' was initialized successfully for the folder {1} with file count threshold of {2}.", taskName, targetFolder.getAbsolutePath(), fileCountThreshold));
    }
    
    static {
        logger = Logger.getLogger(FileCountConsumer.class.getName());
    }
}
