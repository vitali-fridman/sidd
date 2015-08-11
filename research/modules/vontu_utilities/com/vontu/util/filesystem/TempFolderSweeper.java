// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.logging.Level;
import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;
import java.util.TimerTask;

public final class TempFolderSweeper extends TimerTask
{
    private static final Logger _logger;
    private final FileFilter _fileFilter;
    private final File _tempFolder;
    
    public TempFolderSweeper(final File tempFolder, final FileFilter fileFilter) {
        this._tempFolder = tempFolder;
        this._fileFilter = fileFilter;
    }
    
    @Override
    public void run() {
        if (TempFolderSweeper._logger.isLoggable(Level.FINE)) {
            TempFolderSweeper._logger.fine("Running temporary folder sweeper for \"" + this._tempFolder.getAbsolutePath() + "\".");
        }
        final File[] filesToDelete = this._tempFolder.listFiles(this._fileFilter);
        for (int i = 0; filesToDelete != null && i < filesToDelete.length; ++i) {
            final File each = filesToDelete[i];
            if (each.isDirectory()) {
                if (!DirectoryUtils.deleteDirectory(each)) {
                    TempFolderSweeper._logger.warning("Failed to delete temp folder \"" + each.getAbsolutePath() + "\".");
                }
            }
            else if (!each.delete()) {
                TempFolderSweeper._logger.warning("Failed to delete temp file \"" + each.getAbsolutePath() + "\".");
            }
        }
    }
    
    static {
        _logger = Logger.getLogger(TempFolderSweeper.class.getName());
    }
}
