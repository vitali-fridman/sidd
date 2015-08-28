// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import java.util.Collection;
import com.vontu.model.data.Data;
import com.vontu.model.data.constants.IndexedDataStatus;
import java.util.Iterator;
import com.vontu.model.SafeTransaction;
import com.vontu.model.LockNotGrantedException;
import com.vontu.model.ConnectionException;
import com.vontu.util.collection.Filter;
import com.vontu.model.DataAccessException;
import com.vontu.model.data.IndexedInfoSourceStatus;
import com.vontu.model.Model;
import java.util.logging.Logger;

public class IndexStatusCleanup
{
    private static final Logger _logger;
    private final Model _model;
    
    public IndexStatusCleanup(final Model model) {
        this._model = model;
    }
    
    public IndexedInfoSourceStatus createServerExample(final int serverId) throws DataAccessException {
        final IndexedInfoSourceStatus qbeIndexedStatus = (IndexedInfoSourceStatus)this._model.newDataObjectForExample((Class)IndexedInfoSourceStatus.class);
        qbeIndexedStatus.setInformationMonitorID(serverId);
        return qbeIndexedStatus;
    }
    
    public void cleanupIndexStatuses(final IndexedInfoSourceStatus example) throws DataAccessException, ConnectionException, LockNotGrantedException {
        this.cleanupIndexStatuses(new StatusByExampleSource(example), (Filter<IndexedInfoSourceStatus>)new FilterStub());
    }
    
    public void cleanupIndexStatuses(final Filter<IndexedInfoSourceStatus> filter) throws DataAccessException, ConnectionException, LockNotGrantedException {
        this.cleanupIndexStatuses(new AllStatusesSource(), filter);
    }
    
    private void cleanupIndexStatuses(final StatusCollectionSource statusSource, final Filter<IndexedInfoSourceStatus> filter) throws DataAccessException {
        final SafeTransaction safeTx = new SafeTransaction(this._model);
        safeTx.begin();
        try {
            for (final IndexedInfoSourceStatus status : statusSource.getStatusCollection(this._model)) {
                if (filter.shouldAccept((Object)status)) {
                    this.cleanupIndexStatus(status);
                }
            }
            safeTx.commit();
        }
        finally {
            safeTx.cleanup();
        }
    }
    
    private void cleanupIndexStatus(final IndexedInfoSourceStatus status) throws DataAccessException {
        if (IndexedDataStatus.isCurrentlyIndexingState(status.getStatus()) && this._model.currentTransaction().tryLock((Data)status, 1, false)) {
            status.setStatus(14);
            IndexStatusCleanup._logger.info("Found an indexing job for protected profile \"" + status.getIndexedInfoSource().getInfoSource().getName() + "\" version " + status.getIndexedInfoSource().getVersion() + " in a running state on startup, converting to error state.");
        }
    }
    
    static {
        _logger = Logger.getLogger(IndexStatusCleanup.class.getName());
    }
    
    private static class AllStatusesSource implements StatusCollectionSource
    {
        @Override
        public Collection<IndexedInfoSourceStatus> getStatusCollection(final Model model) throws DataAccessException {
            return (Collection<IndexedInfoSourceStatus>)model.getDataCollection((Class)IndexedInfoSourceStatus.class);
        }
    }
    
    private static class StatusByExampleSource implements StatusCollectionSource
    {
        private final IndexedInfoSourceStatus _example;
        
        StatusByExampleSource(final IndexedInfoSourceStatus example) {
            this._example = example;
        }
        
        @Override
        public Collection<IndexedInfoSourceStatus> getStatusCollection(final Model model) throws DataAccessException {
            return (Collection<IndexedInfoSourceStatus>)model.getDataCollectionByExample((Data)this._example, Model.ComparisonType.WILDCARD);
        }
    }
    
    private static class FilterStub implements Filter<IndexedInfoSourceStatus>
    {
        public boolean shouldAccept(final IndexedInfoSourceStatus item) {
            return true;
        }
    }
    
    private interface StatusCollectionSource
    {
        Collection<IndexedInfoSourceStatus> getStatusCollection(Model p0) throws DataAccessException;
    }
}
