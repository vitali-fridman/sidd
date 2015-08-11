// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class DirectoryPermissionsDeterminer
{
    private static final Logger logger;
    
    public boolean verifyDirectoryPersmissions(final String directoryName) {
        return this.verifyDirectoryPersmissions(new File(directoryName));
    }
    
    public boolean verifyDirectoryPersmissions(final File directory) {
        if (!this.verifyDirectoryReadPersmissions(directory)) {
            DirectoryPermissionsDeterminer.logger.log(Level.SEVERE, "Directory '" + directory.getAbsolutePath() + "' is not readable.");
            return false;
        }
        if (!directory.canWrite()) {
            DirectoryPermissionsDeterminer.logger.log(Level.SEVERE, "Directory '" + directory.getAbsolutePath() + "' is not writeable.");
            return false;
        }
        return true;
    }
    
    public boolean verifyDirectoryReadPersmissions(final File directory) {
        if (!directory.exists()) {
            DirectoryPermissionsDeterminer.logger.log(Level.SEVERE, "Directory '" + directory.getAbsolutePath() + "' does not exist.");
            return false;
        }
        if (!directory.isDirectory()) {
            DirectoryPermissionsDeterminer.logger.log(Level.SEVERE, "The name '" + directory.getAbsolutePath() + "' is a file and is not a directorry.");
            return false;
        }
        if (!directory.canRead()) {
            DirectoryPermissionsDeterminer.logger.log(Level.SEVERE, "Directory '" + directory.getAbsolutePath() + "' is not readable.");
            return false;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(DirectoryPermissionsDeterminer.class.getName());
    }
}
