// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.model.data.constants.IndexedDataStatus;
import java.util.logging.Level;
import com.vontu.util.ProtectError;
import java.text.MessageFormat;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.model.Model;
import java.util.concurrent.atomic.AtomicReference;
import com.vontu.model.data.IndexedInfoSourceStatus;
import java.util.logging.Logger;

public class IndexStatus
{
    private static final Logger _logger;
    private final IndexDbAccess _indexDbAccess;
    private final String _indexName;
    private final IndexedInfoSourceStatus _indexStatus;
    private final AtomicReference<IndexingCancelListener> _listenerRef;
    
    public IndexStatus(final Model model, final IndexedInfoSource index, final int serverId, final int initialStatus) throws ProfilesException {
        this(new IndexDbAccess(model), index, serverId, initialStatus);
    }
    
    public IndexStatus(final IndexDbAccess indexDbAccess, final IndexedInfoSource index, final int serverId, final int initialStatus) throws ProfilesException {
        this(indexDbAccess, indexDbAccess.createIndexedInfoSourceStatus(index, initialStatus, serverId));
    }
    
    public IndexStatus(final Model model, final IndexedInfoSourceStatus indexStatus) {
        this(new IndexDbAccess(model), indexStatus);
    }
    
    public IndexStatus(final IndexDbAccess indexDbAccess, final IndexedInfoSourceStatus indexStatus) {
        this._listenerRef = new AtomicReference<IndexingCancelListener>();
        this._indexName = getIndexName(indexStatus.getIndexedInfoSource());
        this._indexDbAccess = indexDbAccess;
        this._indexStatus = indexStatus;
    }
    
    private static String getIndexName(final IndexedInfoSource indexedInfoSource) {
        return MessageFormat.format("\"{0}\" version {1}", indexedInfoSource.getInfoSource().getName(), indexedInfoSource.getVersion());
    }
    
    public String getIndexName() {
        return this._indexName;
    }
    
    public void setFailed(final ProtectError error) {
        try {
            this.update(ProfilesError.toStatus(error));
        }
        catch (ProfilesException e) {
            IndexStatus._logger.log(Level.SEVERE, MessageFormat.format("Failed to set status to \"{0}\" for {1}.", getStatusMessage(error), this.getIndexName()), (Throwable)e);
        }
    }
    
    private static String getStatusMessage(final ProtectError error) {
        return IndexedDataStatus.getStatusMessage(ProfilesError.toStatus(error));
    }
    
    public void startCancelListener(final Cancellable cancellable) {
        final IndexingCancelListener listener = new IndexingCancelListener(this._indexDbAccess.getModel(), this._indexStatus.getIndexedInfoSourceStatusID(), cancellable);
        listener.start();
        this._listenerRef.set(listener);
    }
    
    public void stopCancelListener() {
        this._listenerRef.get().stop();
    }
    
    public void update(final int newStatus) throws ProfilesException {
        this._indexDbAccess.updateIndexedInfoSourceStatus(this._indexStatus, newStatus);
    }
    
    static {
        _logger = Logger.getLogger(IndexStatus.class.getName());
    }
}
