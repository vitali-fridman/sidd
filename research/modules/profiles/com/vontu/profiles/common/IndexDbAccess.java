// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.model.LockNotGrantedException;
import com.vontu.model.Transaction;
import java.util.LinkedList;
import com.vontu.model.data.constants.IndexedDataStatus;
import java.util.logging.Level;
import java.util.Date;
import java.text.MessageFormat;
import com.vontu.model.data.IndexedInfoSourceStatus;
import com.vontu.model.DataAccessException;
import com.vontu.model.ConnectionException;
import java.util.Iterator;
import java.util.Collection;
import com.vontu.model.data.Data;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.model.data.InfoSource;
import com.vontu.model.Model;
import java.util.logging.Logger;

public final class IndexDbAccess
{
    private static final Logger _logger;
    private final Model _model;
    
    public IndexDbAccess(final Model model) {
        assert model != null;
        this._model = model;
    }
    
    private IndexedInfoSource doGetLatestManagerIndexedInfoSource(final InfoSource infoSource) throws ConnectionException, DataAccessException {
        IndexedInfoSource result = null;
        final IndexedInfoSource qbeSource = (IndexedInfoSource)this._model.newDataObject((Class)IndexedInfoSource.class, false);
        qbeSource.setInfoSource(infoSource);
        this._model.beginTransaction();
        final Collection indexedSources = this._model.getDataCollectionByExample((Data)qbeSource, Model.ComparisonType.WILDCARD);
        this._model.commitTransaction();
        if (indexedSources != null) {
            int highestVersion = -1;
            for (final IndexedInfoSource indexedInfoSource : indexedSources) {
                if (indexedInfoSource.getVersion() >= highestVersion) {
                    result = indexedInfoSource;
                    highestVersion = indexedInfoSource.getVersion();
                }
            }
        }
        return result;
    }
    
    public IndexedInfoSourceStatus createIndexedInfoSourceStatus(final IndexedInfoSource index, final int status, final int serverId) throws ProfilesException {
        assert index != null;
        return runModelQuery((ModelQueryExecutor<IndexedInfoSourceStatus>)new ModelQueryExecutor<IndexedInfoSourceStatus>() {
            @Override
            public IndexedInfoSourceStatus execute() throws ConnectionException, DataAccessException {
                final String indexName = MessageFormat.format("\"{0}\" version {1}", index.getInfoSource().getName(), index.getVersion());
                final IndexedInfoSourceStatus indexStatus = (IndexedInfoSourceStatus)IndexDbAccess.this._model.newDataObject((Class)IndexedInfoSourceStatus.class, true);
                indexStatus.setIndexedInfoSource(index);
                indexStatus.setInformationMonitorID(serverId);
                indexStatus.setStatus(status);
                indexStatus.setStartDate(new Date(System.currentTimeMillis()));
                indexStatus.setUpdatedDate(new Date(System.currentTimeMillis()));
                indexStatus.setEndDate(new Date(System.currentTimeMillis()));
                IndexDbAccess.this._model.beginTransaction();
                IndexDbAccess.this._model.persistDataObject((Data)indexStatus, false);
                IndexDbAccess.this._model.commitTransaction();
                if (IndexDbAccess._logger.isLoggable(Level.FINE)) {
                    IndexDbAccess._logger.fine("Created status record for info profile " + indexName + " and set its initial value to \"" + IndexedDataStatus.getStatusMessage(status) + "\".");
                }
                return indexStatus;
            }
        });
    }
    
    public IndexedInfoSource getActiveIndexedInfoSource(final InfoSource infoSource) throws ProfilesException {
        return runModelQuery((ModelQueryExecutor<IndexedInfoSource>)new ModelQueryExecutor<IndexedInfoSource>() {
            @Override
            public IndexedInfoSource execute() throws ConnectionException, DataAccessException {
                final IndexedInfoSource qbeSource = (IndexedInfoSource)IndexDbAccess.this._model.newDataObject((Class)IndexedInfoSource.class, false);
                qbeSource.setInfoSource(infoSource);
                qbeSource.setIsActive(1);
                IndexDbAccess.this._model.beginTransaction();
                final IndexedInfoSource retSource = (IndexedInfoSource)IndexDbAccess.this._model.getDataObjectByExample((Data)qbeSource, Model.ComparisonType.WILDCARD);
                IndexDbAccess.this._model.commitTransaction();
                return retSource;
            }
        });
    }
    
    public IndexedInfoSourceStatus getIndexedInfoSourceStatus(final IndexedInfoSource index, final int hostId) throws ProfilesException {
        assert index != null;
        return runModelQuery((ModelQueryExecutor<IndexedInfoSourceStatus>)new ModelQueryExecutor<IndexedInfoSourceStatus>() {
            @Override
            public IndexedInfoSourceStatus execute() throws ConnectionException, DataAccessException {
                final IndexedInfoSourceStatus qbeStatus = (IndexedInfoSourceStatus)IndexDbAccess.this._model.newDataObject((Class)IndexedInfoSourceStatus.class, false);
                qbeStatus.setIndexedInfoSource(index);
                qbeStatus.setInformationMonitorID(hostId);
                IndexDbAccess.this._model.beginTransaction();
                final IndexedInfoSourceStatus status = (IndexedInfoSourceStatus)IndexDbAccess.this._model.getDataObjectByExample((Data)qbeStatus, Model.ComparisonType.WILDCARD);
                IndexDbAccess.this._model.commitTransaction();
                return status;
            }
        });
    }
    
    public IndexedInfoSourceStatus getIndexedInfoSourceStatus(final int infoSourceID, final int hostID) throws ProfilesException {
        return runModelQuery((ModelQueryExecutor<IndexedInfoSourceStatus>)new ModelQueryExecutor<IndexedInfoSourceStatus>() {
            @Override
            public IndexedInfoSourceStatus execute() throws ConnectionException, DataAccessException {
                IndexDbAccess.this._model.beginTransaction();
                final InfoSource infoSource = (InfoSource)IndexDbAccess.this._model.getDataObjectByKey((Class)InfoSource.class, infoSourceID);
                IndexDbAccess.this._model.commitTransaction();
                final IndexedInfoSource latestIndex = IndexDbAccess.this.doGetLatestManagerIndexedInfoSource(infoSource);
                final IndexedInfoSourceStatus qbeStatus = (IndexedInfoSourceStatus)IndexDbAccess.this._model.newDataObject((Class)IndexedInfoSourceStatus.class, false);
                qbeStatus.setIndexedInfoSource(latestIndex);
                qbeStatus.setInformationMonitorID(hostID);
                IndexDbAccess.this._model.beginTransaction();
                final IndexedInfoSourceStatus status = (IndexedInfoSourceStatus)IndexDbAccess.this._model.getDataObjectByExample((Data)qbeStatus, Model.ComparisonType.EXACT);
                IndexDbAccess.this._model.commitTransaction();
                return status;
            }
        });
    }
    
    public Collection<IndexedInfoSourceStatus> getIndexedInfoSourceStatusByInfoSource(final int infoSourceID) throws ProfilesException {
        return runModelQuery((ModelQueryExecutor<Collection<IndexedInfoSourceStatus>>)new ModelQueryExecutor<Collection<IndexedInfoSourceStatus>>() {
            @Override
            public Collection<IndexedInfoSourceStatus> execute() throws ConnectionException, DataAccessException {
                Collection<IndexedInfoSourceStatus> allStatus = null;
                IndexDbAccess.this._model.beginTransaction();
                final InfoSource infoSource = (InfoSource)IndexDbAccess.this._model.getDataObjectByKey((Class)InfoSource.class, infoSourceID);
                IndexDbAccess.this._model.commitTransaction();
                if (infoSource != null) {
                    final IndexedInfoSource latestIndex = IndexDbAccess.this.doGetLatestManagerIndexedInfoSource(infoSource);
                    if (latestIndex != null) {
                        final IndexedInfoSourceStatus qbeStatus = (IndexedInfoSourceStatus)IndexDbAccess.this._model.newDataObject((Class)IndexedInfoSourceStatus.class, false);
                        qbeStatus.setIndexedInfoSource(latestIndex);
                        IndexDbAccess.this._model.beginTransaction();
                        allStatus = (Collection<IndexedInfoSourceStatus>)IndexDbAccess.this._model.getDataCollectionByExample((Data)qbeStatus, Model.ComparisonType.WILDCARD);
                        IndexDbAccess.this._model.commitTransaction();
                    }
                }
                return (allStatus != null) ? allStatus : new LinkedList<IndexedInfoSourceStatus>();
            }
        });
    }
    
    public IndexedInfoSource getLatestManagerIndexedInfoSource(final InfoSource infoSource) throws ProfilesException {
        return runModelQuery((ModelQueryExecutor<IndexedInfoSource>)new ModelQueryExecutor<IndexedInfoSource>() {
            @Override
            public IndexedInfoSource execute() throws ConnectionException, DataAccessException {
                return IndexDbAccess.this.doGetLatestManagerIndexedInfoSource(infoSource);
            }
        });
    }
    
    public Model getModel() {
        return this._model;
    }
    
    public boolean isIndexingJobPendingCancel(final IndexedInfoSource index, final int hostId) throws ProfilesException {
        final IndexedInfoSourceStatus indexStatus = this.getIndexedInfoSourceStatus(index, hostId);
        return indexStatus != null && indexStatus.getStatus() == 7;
    }
    
    public static <T> T runModelQuery(final ModelQueryExecutor<T> executor) throws ProfilesException {
        try {
            return (T)runInterruptableModelQuery((ModelQueryExecutor<Object>)executor);
        }
        catch (InterruptedException e) {
            throw new ProfilesException(ProfilesError.PROCESS_SHUTDOWN, e);
        }
    }
    
    private static <T> T runInterruptableModelQuery(final ModelQueryExecutor<T> executor) throws ProfilesException, InterruptedException {
        try {
            return executor.execute();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e);
        }
        catch (ConnectionException e2) {
            throw new ProfilesConnectionException((Throwable)e2);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    public void updateIndexedInfoSourceStatus(final IndexedInfoSourceStatus status, final int value) throws ProfilesException {
        final IndexedInfoSource index = status.getIndexedInfoSource();
        final String indexName = '\"' + index.getInfoSource().getName() + "\" version " + index.getVersion();
        try {
            runModelQuery((ModelQueryExecutor<Object>)new ModelQueryExecutor() {
                @Override
                public Object execute() throws ConnectionException, DataAccessException {
                    final Transaction transaction = IndexDbAccess.this._model.beginTransaction();
                    transaction.lock((Data)status, 1, false);
                    status.setStatus(value);
                    status.setUpdatedDate(new Date(System.currentTimeMillis()));
                    status.setEndDate(new Date(System.currentTimeMillis()));
                    IndexDbAccess.this._model.commitTransaction();
                    if (IndexDbAccess._logger.isLoggable(Level.FINE)) {
                        IndexDbAccess._logger.fine("Set status of info profile " + indexName + " to \"" + IndexedDataStatus.getStatusMessage(value) + "\".");
                    }
                    return null;
                }
            });
        }
        catch (LockNotGrantedException e) {
            throw new ProfilesException(ProfilesError.PROFILES_STATUS_LOCKED, "Failed to set the status of " + indexName + " to \"" + IndexedDataStatus.getStatusMessage(value) + "\" because the status object was unexpectedly locked.");
        }
    }
    
    public IndexedInfoSource getPreviousIndexVersion(final IndexedInfoSource index) throws ProfilesException {
        return runModelQuery((ModelQueryExecutor<IndexedInfoSource>)new ModelQueryExecutor<IndexedInfoSource>() {
            @Override
            public IndexedInfoSource execute() throws ConnectionException, DataAccessException {
                final Model model = IndexDbAccess.this.getModel();
                final IndexedInfoSource qbeIndex = (IndexedInfoSource)model.newDataObject((Class)IndexedInfoSource.class, false);
                qbeIndex.setInfoSource(index.getInfoSource());
                qbeIndex.setVersion(index.getVersion() - 1);
                model.beginTransaction();
                final IndexedInfoSource previousIndex = (IndexedInfoSource)model.getDataObjectByExample((Data)qbeIndex, Model.ComparisonType.WILDCARD);
                model.commitTransaction();
                return previousIndex;
            }
        });
    }
    
    static {
        _logger = Logger.getLogger(IndexDbAccess.class.getName());
    }
    
    public interface ModelQueryExecutor<T>
    {
        T execute() throws ConnectionException, DataAccessException, InterruptedException;
    }
}
