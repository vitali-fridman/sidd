// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.util.Collection;
import com.vontu.profiles.common.FilenameCollection;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import com.vontu.communication.data.IndexDescriptorMarshallable;

class DeleteFilesRequest implements Runnable
{
    private final IndexDescriptorMarshallable _index;
    private final DeleteResultLogger _eventLogger;
    private final File[] _files;
    private final List<String> _failedFilenames;
    private final List<String> _deletedFilenames;
    
    DeleteFilesRequest(final IndexDescriptorMarshallable index, final DeleteResultLogger eventLogger, final File indexFolder) {
        this._failedFilenames = new LinkedList<String>();
        this._deletedFilenames = new LinkedList<String>();
        this._index = index;
        this._eventLogger = eventLogger;
        this._files = FilenameCollection.forIndex(this._index).toFiles(indexFolder);
    }
    
    private void deleteFile(final File file) {
        if (file.exists()) {
            if (file.delete()) {
                this._deletedFilenames.add(file.getName());
            }
            else {
                this._failedFilenames.add(file.getName());
            }
        }
    }
    
    private void deleteFiles() {
        for (int i = 0; i < this._files.length; ++i) {
            this.deleteFile(this._files[i]);
        }
    }
    
    private void logDeleted() {
        if (this._deletedFilenames.size() > 0) {
            this._eventLogger.logFilesDeleted(this._deletedFilenames, this._index);
        }
    }
    
    private void logFailed() {
        if (this._failedFilenames.size() > 0) {
            this._eventLogger.logDeleteFailed(this._failedFilenames, this._index);
        }
    }
    
    @Override
    public void run() {
        this.deleteFiles();
        this.logDeleted();
        this.logFailed();
    }
}
