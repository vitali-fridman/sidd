// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager;

import java.util.logging.Level;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

public class IndexFileCleaner
{
    private static final Logger logger;
    private final IndexDirectoryProvider indexDirectoryProvider;
    
    public IndexFileCleaner() {
        this(new DefaultIndexDirectoryProvider());
    }
    
    IndexFileCleaner(final IndexDirectoryProvider indexDirectoryProvider) {
        this.indexDirectoryProvider = indexDirectoryProvider;
    }
    
    public void removeIndexFile(final int infoSourceID) {
        final File indexDirectory = this.indexDirectoryProvider.getDirectory();
        final File[] indexFiles = indexDirectory.listFiles(new RdxFileFilter());
        if (indexFiles != null && indexFiles.length > 0) {
            for (int i = 0; i < indexFiles.length; ++i) {
                final int id = extractInfoSourceIdFromFileName(indexFiles[i].getName());
                if (infoSourceID == id) {
                    indexFiles[i].delete();
                }
            }
        }
    }
    
    private static int extractInfoSourceIdFromFileName(final String fileName) {
        try {
            final String[] filename = fileName.split("\\.");
            if (filename.length == 4) {
                return Integer.parseInt(filename[1]);
            }
        }
        catch (NumberFormatException e) {
            final String message = String.format("Unable to identify index from file name %s", fileName);
            IndexFileCleaner.logger.log(Level.FINER, message, e);
        }
        return -1;
    }
    
    static {
        logger = Logger.getLogger(IndexFileCleaner.class.getName());
    }
    
    private static class RdxFileFilter implements FilenameFilter
    {
        @Override
        public boolean accept(final File dir, final String name) {
            return name.toLowerCase().endsWith(".rdx");
        }
    }
    
    private static class DefaultIndexDirectoryProvider implements IndexDirectoryProvider
    {
        @Override
        public File getDirectory() {
            return IndexerConfig.getLocalIndexDirectory();
        }
    }
    
    interface IndexDirectoryProvider
    {
        File getDirectory();
    }
}
