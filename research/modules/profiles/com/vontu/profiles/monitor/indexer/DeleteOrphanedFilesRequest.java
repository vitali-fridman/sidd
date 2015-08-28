// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.util.HashMap;
import java.util.Collections;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import com.vontu.profiles.common.FilenameCollection;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.io.File;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import java.util.logging.Logger;

class DeleteOrphanedFilesRequest implements Runnable
{
    private static final Logger _logger;
    private final Iterable<IndexDescriptorMarshallable> _indexDescriptors;
    private final DeleteResultLogger _eventLogger;
    private final File _indexFolder;
    private final IndexFilenameFilter _filenameFilter;
    
    DeleteOrphanedFilesRequest(final Iterable<IndexDescriptorMarshallable> indexDescriptors, final DeleteResultLogger eventLogger, final File indexFolder, final Pattern fileMask) {
        this._indexDescriptors = indexDescriptors;
        this._indexFolder = indexFolder;
        this._filenameFilter = new IndexFilenameFilter(fileMask);
        this._eventLogger = eventLogger;
    }
    
    @Override
    public void run() {
        for (final File orphanedFile : this.getOrphanedFiles(this._indexDescriptors)) {
            if (!orphanedFile.delete()) {
                this._eventLogger.logDeleteOrphanFailed(orphanedFile);
            }
            else {
                DeleteOrphanedFilesRequest._logger.log(Level.INFO, "Deleted orphaned index file {0}.", orphanedFile.getAbsolutePath());
            }
        }
    }
    
    private File[] getOrphanedFiles(final Iterable<IndexDescriptorMarshallable> configSource) {
        final Map<String, File> foundFilesByName = this.findIndexFiles();
        for (final IndexDescriptorMarshallable indexDescriptor : configSource) {
            foundFilesByName.keySet().removeAll(FilenameCollection.forIndex(indexDescriptor));
        }
        return foundFilesByName.values().toArray(new File[foundFilesByName.size()]);
    }
    
    private Map<String, File> findIndexFiles() {
        final File[] indexFiles = this._indexFolder.listFiles(this._filenameFilter);
        if (indexFiles == null) {
            this._eventLogger.logListingFailed(this._indexFolder);
            return Collections.emptyMap();
        }
        final Map<String, File> foundFilesByName = new HashMap<String, File>(indexFiles.length);
        for (final File indexFile : indexFiles) {
            foundFilesByName.put(indexFile.getName(), indexFile);
        }
        return foundFilesByName;
    }
    
    static {
        _logger = Logger.getLogger(DeleteOrphanedFilesRequest.class.getName());
    }
}
