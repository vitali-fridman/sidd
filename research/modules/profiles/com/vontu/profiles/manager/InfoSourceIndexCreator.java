// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager;

import java.util.HashMap;
import com.vontu.logging.SystemEventUtil;
import com.vontu.logging.SystemEvent;
import com.vontu.logging.event.system.SystemEventSeverity;
import com.vontu.profiles.common.IndexStatus;
import com.vontu.profileindexer.IndexerException;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import com.vontu.keystorehouse.KeyStorehouse;
import com.vontu.model.data.InformationMonitor;
import com.vontu.model.LockNotGrantedException;
import com.vontu.model.Transaction;
import java.util.Iterator;
import java.util.Collection;
import com.vontu.profiles.common.ProfilesException;
import java.sql.Date;
import com.vontu.model.data.Data;
import com.vontu.model.data.constants.IndexedDataStatus;
import com.vontu.model.data.IndexedInfoSourceStatus;
import com.vontu.model.DataAccessException;
import com.vontu.profiles.manager.document.DocumentSourceIndexCreator;
import com.vontu.model.data.DocSource;
import java.util.logging.Level;
import com.vontu.profiles.manager.directoryconnection.DirectoryConnectionSourceIndexCreator;
import com.vontu.model.data.DirectoryConnectionSource;
import com.vontu.profiles.manager.database.DataSourceIndexCreator;
import com.vontu.model.data.DataSource;
import com.vontu.logging.LocalLogWriter;
import com.vontu.logging.ModelWriter;
import com.vontu.logging.SystemEventWriter;
import com.vontu.profiles.common.ProfilesError;
import com.vontu.profileindexer.IndexerError;
import java.util.Map;
import java.util.logging.Logger;
import com.vontu.model.Model;
import com.vontu.profiles.common.IndexDbAccess;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.model.data.InfoSource;
import com.vontu.profiles.common.Cancellable;

public abstract class InfoSourceIndexCreator implements Cancellable
{
    protected final InfoSource _infoSource;
    protected IndexedInfoSource _indexedInfoSource;
    protected final IndexDbAccess _indexDbAccess;
    protected final Model _model;
    protected final String _triggerName;
    protected static final Logger _logger;
    private Thread _indexerThread;
    private boolean _wasCancelled;
    private final String _eventIdPrefix;
    public static final String RDX_FILE_EXTENSION = ".rdx";
    private static final Map<IndexerError, ProfilesError> _errorMap;
    private final SystemEventWriter _eventWriter;
    
    protected InfoSourceIndexCreator(final Model model, final InfoSource infoSource, final String triggerName, final String eventIdPrefix) {
        this._infoSource = infoSource;
        this._triggerName = triggerName;
        this._model = model;
        this._indexDbAccess = new IndexDbAccess(this._model);
        this._eventIdPrefix = eventIdPrefix;
        this._eventWriter = LocalLogWriter.createAggregated(InfoSourceIndexCreator._logger, (SystemEventWriter)new ModelWriter(this._model, -1));
    }
    
    @Override
    public synchronized void cancel() {
        InfoSourceIndexCreator._logger.info("Indexing job for " + this._infoSource.getName() + " is beinh cancelled.");
        this._wasCancelled = true;
        this._indexerThread.interrupt();
    }
    
    protected synchronized boolean wasCancelled() {
        return this._wasCancelled;
    }
    
    public static void indexListOfDataSources(final String triggerName, final int[] sourceIDs) {
        Model model = null;
        try {
            model = Model.getInstance();
            for (int i = 0; i < sourceIDs.length; ++i) {
                final int infoSourceIDToIndex = sourceIDs[i];
                model.beginTransaction();
                final InfoSource foundInfoSource = (InfoSource)model.getDataObjectByKey((Class)InfoSource.class, infoSourceIDToIndex);
                model.commitTransaction();
                if (foundInfoSource != null) {
                    InfoSourceIndexCreator indexer;
                    if (foundInfoSource.getType() == 1) {
                        indexer = new DataSourceIndexCreator(model, (DataSource)foundInfoSource, triggerName);
                    }
                    else if (foundInfoSource.getType() == 3) {
                        indexer = new DirectoryConnectionSourceIndexCreator(model, (DirectoryConnectionSource)foundInfoSource, triggerName);
                    }
                    else {
                        if (foundInfoSource.getType() != 2) {
                            InfoSourceIndexCreator._logger.log(Level.WARNING, "Found InfoSource Type doesn't exit, InfoSource Id: " + foundInfoSource.getInfoSourceID());
                            throw new IllegalArgumentException();
                        }
                        indexer = new DocumentSourceIndexCreator(model, (DocSource)foundInfoSource, triggerName);
                    }
                    indexer.indexInfoSourceOnManager();
                    if (!IndexerConfig.retainAllFiles()) {
                        indexer.removeOldIndexFilesOnManager();
                    }
                }
                else {
                    logSystemEvent(Level.WARNING, "com.vontu.profiles.database.content_not_found", Integer.toString(infoSourceIDToIndex));
                    InfoSourceIndexCreator._logger.log(Level.WARNING, "Database content for this InfoSource not found, nothing to index, the requested InfoSource id: " + Integer.toString(infoSourceIDToIndex));
                }
            }
        }
        catch (DataAccessException e) {
            logSystemEvent(Level.SEVERE, "com.vontu.profiles.database.database_indexing_error", e.getMessage());
        }
        catch (InterruptedException e2) {
            InfoSourceIndexCreator._logger.log(Level.INFO, "InfoSourceIndexCreator.indexListOfDataSources caught InterruptedException: ", e2);
            logSystemEvent(Level.INFO, "com.vontu.profiles.database.indexing_shutdown", new String[0]);
            Thread.currentThread().interrupt();
        }
        catch (Exception e3) {
            InfoSourceIndexCreator._logger.log(Level.SEVERE, "Indexing failed. Trigger Name: " + triggerName + " IDs: " + sourceIDs.toString(), e3);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    public static int cancelIndexingJob(final int dataSourceID) throws ProfilesException, LockNotGrantedException {
        int hostsCancelled = 0;
        Model model = null;
        try {
            model = Model.getInstance();
            final IndexDbAccess indexDbAccess = new IndexDbAccess(model);
            final Collection<IndexedInfoSourceStatus> idss = indexDbAccess.getIndexedInfoSourceStatusByInfoSource(dataSourceID);
            final Iterator<IndexedInfoSourceStatus> idssIter = idss.iterator();
            final Transaction transaction = model.beginTransaction();
            while (idssIter.hasNext()) {
                final IndexedInfoSourceStatus tmpStatus = idssIter.next();
                if (IndexedDataStatus.isCurrentlyIndexingState(tmpStatus.getStatus())) {
                    transaction.lock((Data)tmpStatus, 1, false);
                    tmpStatus.setStatus(7);
                    tmpStatus.setUpdatedDate((java.util.Date)new Date(System.currentTimeMillis()));
                    ++hostsCancelled;
                }
            }
            model.commitTransaction();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e);
        }
        finally {
            Model.cleanupTransaction();
        }
        return hostsCancelled;
    }
    
    public static void cancelMonitorIndexingJob(final int dataSourceID, final int monitorID) throws ProfilesException {
        Model model = null;
        try {
            model = Model.getInstance();
            final IndexDbAccess indexDbAccess = new IndexDbAccess(model);
            final IndexedInfoSourceStatus idss = indexDbAccess.getIndexedInfoSourceStatus(dataSourceID, monitorID);
            if (IndexedDataStatus.isReplicatingState(idss.getStatus())) {
                final Transaction transaction = model.beginTransaction();
                transaction.lock((Data)idss, 1, false);
                idss.setStatus(7);
                idss.setUpdatedDate((java.util.Date)new Date(System.currentTimeMillis()));
                model.commitTransaction();
            }
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    public static void retryMonitorIndexingJob(final int infoSourceID, final int monitorID) throws ProfilesException, LockNotGrantedException {
        Model model = null;
        try {
            model = Model.getInstance();
            model.beginTransaction();
            final InfoSource infoSource = (InfoSource)model.getDataObjectByKey((Class)InfoSource.class, infoSourceID);
            model.commitTransaction();
            if (infoSource == null) {
                InfoSourceIndexCreator._logger.log(Level.WARNING, "Profile ID " + infoSourceID + " not found.");
                return;
            }
            final InformationMonitor qbeMonitor = (InformationMonitor)model.newDataObject((Class)InformationMonitor.class, false);
            qbeMonitor.setInformationMonitorID(monitorID);
            model.beginTransaction();
            final InformationMonitor monitor = (InformationMonitor)model.getDataObjectByExample((Data)qbeMonitor, Model.ComparisonType.WILDCARD);
            model.commitTransaction();
            if (monitor == null) {
                InfoSourceIndexCreator._logger.log(Level.WARNING, "Monitor " + monitorID + " not found.");
                return;
            }
            final IndexedInfoSource qbeDS = (IndexedInfoSource)model.newDataObject((Class)IndexedInfoSource.class, false);
            qbeDS.setInfoSource(infoSource);
            model.beginTransaction();
            final Collection<IndexedInfoSource> indexedDataSources = (Collection<IndexedInfoSource>)model.getDataCollectionByExample((Data)qbeDS, Model.ComparisonType.WILDCARD);
            model.commitTransaction();
            final Iterator<IndexedInfoSource> idsIter = indexedDataSources.iterator();
            int maxVersion = -1;
            IndexedInfoSource indexedDataSource = null;
            while (idsIter.hasNext()) {
                final IndexedInfoSource tmpSrc = idsIter.next();
                if (tmpSrc.getVersion() > maxVersion) {
                    maxVersion = tmpSrc.getVersion();
                    indexedDataSource = tmpSrc;
                }
            }
            final IndexedInfoSourceStatus qbeIDSS = (IndexedInfoSourceStatus)model.newDataObject((Class)IndexedInfoSourceStatus.class, false);
            qbeIDSS.setIndexedInfoSource(indexedDataSource);
            qbeIDSS.setInformationMonitorID(monitor.getInformationMonitorID());
            model.beginTransaction();
            final IndexedInfoSourceStatus indexedDataSourceStatus = (IndexedInfoSourceStatus)model.getDataObjectByExample((Data)qbeIDSS, Model.ComparisonType.WILDCARD);
            model.commitTransaction();
            if (indexedDataSourceStatus != null) {
                final Transaction transaction = model.beginTransaction();
                transaction.lock((Data)indexedDataSourceStatus, 1, false);
                indexedDataSourceStatus.setStatus(17);
                model.commitTransaction();
            }
            else {
                final IndexDbAccess indexDbAccess = new IndexDbAccess(model);
                indexDbAccess.createIndexedInfoSourceStatus(indexedDataSource, 17, monitorID);
            }
        }
        catch (DataAccessException dae) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)dae);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    protected static void checkKeyStorehouse() throws ProfilesException {
        if (!KeyStorehouse.isInstantiatedAndIgnited()) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_KEYS_NOT_IGNITED, "The cryptographic keys aren't ignited.");
        }
    }
    
    protected void switchOverToNewIndexedInfoSource() throws ProfilesException, InterruptedException {
        final IndexDbAccess indexDbAccess = new IndexDbAccess(this._model);
        final IndexedInfoSource lastSource = indexDbAccess.getActiveIndexedInfoSource(this._infoSource);
        try {
            final Transaction transaction = this._model.beginTransaction();
            if (lastSource != null) {
                transaction.waitLock((Data)lastSource, 1, false);
                lastSource.setIsActive(0);
            }
            transaction.waitLock((Data)this._indexedInfoSource, 1, false);
            this._indexedInfoSource.setIsActive(1);
            this._model.commitTransaction();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    private void removeOldIndexFilesOnManager() {
        if (this._indexedInfoSource == null) {
            return;
        }
        if (this._indexedInfoSource.getIsActive() == 0) {
            return;
        }
        final File indexDirectory = IndexerConfig.getLocalIndexDirectory();
        final File[] indexFiles = indexDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().endsWith(".rdx");
            }
        });
        if (indexFiles != null && indexFiles.length > 0) {
            for (int i = 0; i < indexFiles.length; ++i) {
                try {
                    final int currentInfoSourceID = this._indexedInfoSource.getInfoSource().getInfoSourceID();
                    final int newVersion = this._indexedInfoSource.getVersion();
                    final String[] filename = indexFiles[i].getName().split("\\.");
                    if (filename.length == 4) {
                        final int infoSourceID = Integer.parseInt(filename[1]);
                        final int version = Integer.parseInt(filename[2]);
                        if (infoSourceID == currentInfoSourceID && version < newVersion && !this.isIndexFileInUse(this._indexedInfoSource.getInfoSource(), version)) {
                            indexFiles[i].delete();
                        }
                    }
                }
                catch (Throwable t) {
                    try {
                        InfoSourceIndexCreator._logger.log(Level.WARNING, "Cannot delete index file " + indexFiles[i].getCanonicalPath(), t);
                    }
                    catch (IOException e) {
                        InfoSourceIndexCreator._logger.log(Level.WARNING, "Cannot get file name");
                    }
                }
                finally {
                    Model.cleanupTransaction();
                }
            }
        }
    }
    
    private boolean isIndexFileInUse(final InfoSource infoSource, final int version) throws DataAccessException {
        this._model.beginTransaction();
        final IndexedInfoSource qeIndexedInfoSource = (IndexedInfoSource)this._model.newDataObject((Class)IndexedInfoSource.class, false);
        qeIndexedInfoSource.setInfoSource(infoSource);
        qeIndexedInfoSource.setVersion(version);
        for (final IndexedInfoSource indexedInfoSource : this._model.getDataCollectionByExample((Data)qeIndexedInfoSource, Model.ComparisonType.WILDCARD)) {
            final IndexedInfoSourceStatus qeStatus = (IndexedInfoSourceStatus)this._model.newDataObject((Class)IndexedInfoSourceStatus.class, false);
            qeStatus.setIndexedInfoSource(indexedInfoSource);
            qeStatus.setStatus(2);
            if (this._model.getDataCollectionByExample((Data)qeStatus, Model.ComparisonType.WILDCARD).size() > 0) {
                return true;
            }
        }
        this._model.commitTransaction();
        return false;
    }
    
    protected abstract IndexedInfoSource createIndexedInfoSource() throws ProfilesException;
    
    protected abstract void indexInfoSourceOnManager() throws InterruptedException;
    
    protected synchronized void setIndexerThread(final Thread thread) {
        this._indexerThread = thread;
    }
    
    protected static ProfilesException translateIndexerException(final IndexerException e) {
        return new ProfilesException(translateIndexerError((IndexerError)e.getError()), e.getCause(), e.getMessage());
    }
    
    private static ProfilesError translateIndexerError(final IndexerError error) {
        return InfoSourceIndexCreator._errorMap.get(error);
    }
    
    protected void handleInterrupt(final InterruptedException e, final IndexStatus indexStatus) throws ProfilesException, InterruptedException {
        if (this.wasCancelled()) {
            Thread.interrupted();
            indexStatus.update(8);
            this.getSystemEvent("indexing_cancelled", SystemEventSeverity.WARNING).log(this._eventWriter, new String[] { indexStatus.getIndexName() });
            return;
        }
        Thread.currentThread().interrupt();
        throw e;
    }
    
    private SystemEvent getSystemEvent(final String shortName, final SystemEventSeverity severity) {
        return new SystemEvent(this._eventIdPrefix + shortName, severity);
    }
    
    protected static String getEventIdPrefix(final String profileType) {
        return "com.vontu.profiles." + profileType + '.';
    }
    
    protected static void logSystemEvent(final Level severity, final String eventType, final String... params) {
        SystemEventUtil.log(InfoSourceIndexCreator._logger, -1, severity, eventType, params);
    }
    
    static {
        _logger = Logger.getLogger(InfoSourceIndexCreator.class.getName());
        (_errorMap = new HashMap<IndexerError, ProfilesError>()).put(IndexerError.CRYPTO_FILE_WRITE_ERROR, ProfileManagerError.PROFILE_MANAGER_CRYPTO_FILE_WRITE_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.PROFILE_DATA_CORRUPTED, ProfileManagerError.PROFILE_MANAGER_CORRUPTED_DATA_FILE);
        InfoSourceIndexCreator._errorMap.put(IndexerError.MAXIMUM_SIZE_EXCEEDED, ProfileManagerError.PROFILE_MANAGER_PROFILE_TOO_LARGE);
        InfoSourceIndexCreator._errorMap.put(IndexerError.KEYSTORE_ERROR, ProfileManagerError.PROFILE_MANAGER_KEY_RETRIEVAL_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.CRYPTOGRAPHIC_ERROR, ProfileManagerError.PROFILE_MANAGER_CRYPTOGRAPHIC_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.RAM_INDEX_CREATE_ERROR, ProfileManagerError.PROFILE_MANAGER_RAM_INDEX_CREATE_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.PROCESS_SHUTDOWN_ERROR, ProfileManagerError.PROFILES_MANAGER_PROCESS_SHUTDOWN_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.UNEXPECTED_PROCESS_EXIT, ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.UNEXPECTED_OUT_OF_MEMORY_ERROR, ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.PROCESS_IO_ERROR, ProfileManagerError.PROFILE_MANAGER_INDEXER_PROCESS_IO_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.RAM_INDEX_CREATE_ERROR_WILL_RETRY, ProfileManagerError.PROFILE_MANAGER_RAM_INDEX_CREATE_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.GENERIC_COULD_NOT_CREATE_JVM, ProfileManagerError.PROFILE_MANAGER_COULD_NOT_CREATE_JVM);
        InfoSourceIndexCreator._errorMap.put(IndexerError.NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP, ProfileManagerError.PROFILE_MANAGER_NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP);
        InfoSourceIndexCreator._errorMap.put(IndexerError.CHARACTER_ENCODING_ERROR, ProfileManagerError.PROFILE_MANAGER_CHARACTER_ENCODING_ERROR);
        InfoSourceIndexCreator._errorMap.put(IndexerError.NOT_ENOUGH_DISK_SPACE, ProfileManagerError.PROFILE_MANAGER_NOT_ENOUGH_DISK_SPACE);
    }
}
