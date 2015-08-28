// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import com.vontu.profiles.ProfilesBundle;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.vontu.security.KeyStorehouseException;
import java.io.IOException;
import com.vontu.profileindexer.database.DatabaseIndexCreatorResult;
import com.vontu.keystorehouse.KeyContainer;
import com.vontu.keystorehouse.KeyStorehouse;
import java.io.PrintWriter;
import java.io.Reader;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindexer.database.DatabaseIndexCreator;
import com.vontu.profiles.manager.database.IndexerPropertiesTranslator;
import com.vontu.vontukeystorehouse.common.KeyStorehouseManager;
import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.model.Transaction;
import com.vontu.profiles.manager.database.SimpleDataSourceIndexCreatorResult;
import com.vontu.profiles.manager.ProfileManagerError;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profiles.common.Cancellable;
import com.vontu.model.DataAccessException;
import com.vontu.profiles.common.ProfilesError;
import com.vontu.model.data.Data;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.profiles.common.ProfilesException;
import com.vontu.logging.SystemEventUtil;
import java.util.logging.Level;
import com.vontu.profiles.common.IndexStatus;
import com.vontu.model.data.InfoSource;
import com.vontu.model.data.DirectoryConnectionSource;
import com.vontu.model.Model;
import com.vontu.profiles.manager.InfoSourceIndexCreator;

public class DirectoryConnectionSourceIndexCreator extends InfoSourceIndexCreator
{
    private static final String FILE_NAME_PREFIX = "DataSource";
    private UserGroupEntryReaderCreator userGroupEntryReaderCreator;
    
    public DirectoryConnectionSourceIndexCreator(final Model model, final DirectoryConnectionSource directoryConnectionSource, final String triggerName) {
        this(model, directoryConnectionSource, triggerName, new UserGroupEntryReaderCreator(directoryConnectionSource.getDirectoryConnection()));
    }
    
    DirectoryConnectionSourceIndexCreator(final Model model, final DirectoryConnectionSource directoryConnectionSource, final String triggerName, final UserGroupEntryReaderCreator userGroupEntryReaderCreator) {
        super(model, (InfoSource)directoryConnectionSource, triggerName, InfoSourceIndexCreator.getEventIdPrefix("directoryconnection"));
        this.userGroupEntryReaderCreator = userGroupEntryReaderCreator;
    }
    
    @Override
    protected void indexInfoSourceOnManager() throws InterruptedException {
        IndexStatus indexStatus = null;
        try {
            this._indexedInfoSource = this.createIndexedInfoSource();
            indexStatus = new IndexStatus(this._model, this._indexedInfoSource, -1, 1);
            this.indexDirectoryConnection(indexStatus);
        }
        catch (ProfilesException e) {
            if (indexStatus != null) {
                indexStatus.setFailed(e.getError());
            }
            DirectoryConnectionSourceIndexCreator._logger.log(Level.SEVERE, e.getMessage(), e.getCause());
            SystemEventUtil.log(-1, Level.SEVERE, "com.vontu.profiles.database.creation_failed", new String[] { this._infoSource.getName() });
        }
    }
    
    @Override
    protected IndexedInfoSource createIndexedInfoSource() throws ProfilesException {
        int indexVersion = 1;
        final IndexedInfoSource lastSource = this._indexDbAccess.getLatestManagerIndexedInfoSource(this._infoSource);
        if (lastSource != null) {
            indexVersion = lastSource.getVersion() + 1;
        }
        IndexedInfoSource indexedDataSource;
        try {
            indexedDataSource = (IndexedInfoSource)this._model.newDataObject((Class)IndexedInfoSource.class, false);
            indexedDataSource.setInfoSource(this._infoSource);
            indexedDataSource.setInfoSourceVersion(this._infoSource.getVersion());
            indexedDataSource.setVersion(indexVersion);
            indexedDataSource.setIsActive(0);
            indexedDataSource.setFileSize(0);
            indexedDataSource.setTriggerName(this._triggerName);
            indexedDataSource.setNumberOfSubIndexes(1);
            this._model.beginTransaction();
            this._model.persistDataObject((Data)indexedDataSource, false);
            this._model.commitTransaction();
            assert indexedDataSource.getIndexedInfoSourceID() >= 0;
        }
        catch (DataAccessException dae) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)dae, "Failed to create directory connection profile \"" + this._infoSource.getName() + "\" version " + indexVersion + '.');
        }
        finally {
            Model.cleanupTransaction();
        }
        return indexedDataSource;
    }
    
    private void indexDirectoryConnection(final IndexStatus indexStatus) throws ProfilesException, InterruptedException {
        this.setIndexerThread(Thread.currentThread());
        indexStatus.startCancelListener(this);
        try {
            final SimpleDataSourceIndexCreatorResult directoryConnectionIndexCreatorResult = this.createIndex();
            final Transaction transaction = this._model.beginTransaction();
            transaction.waitLock((Data)this._indexedInfoSource, 1, false);
            this._indexedInfoSource.setKeyAlias(directoryConnectionIndexCreatorResult.cryptoKeyAlias());
            this._indexedInfoSource.setFileSize((int)(directoryConnectionIndexCreatorResult.size() / directoryConnectionIndexCreatorResult.fileCount() / 1024L));
            this._indexedInfoSource.setNumberOfSubIndexes(directoryConnectionIndexCreatorResult.fileCount());
            this._model.commitTransaction();
            this.switchOverToNewIndexedInfoSource();
            indexStatus.update(6);
            InfoSourceIndexCreator.logSystemEvent(Level.INFO, "com.vontu.profiles.database.created", this.getIndexName(), this._infoSource.getFileName(), String.valueOf(directoryConnectionIndexCreatorResult.getProcessedRowCount()), String.valueOf(directoryConnectionIndexCreatorResult.getInvalidRowCount()));
            DirectoryConnectionSourceIndexCreator._logger.info(directoryConnectionIndexCreatorResult.toString());
        }
        catch (ProfilesException pe) {
            throw pe;
        }
        catch (DataAccessException dae) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)dae, "Failed to update " + this.getIndexName() + " after index completion.");
        }
        catch (IndexerException ie) {
            throw InfoSourceIndexCreator.translateIndexerException(ie);
        }
        catch (InterruptedException ie2) {
            this.handleInterrupt(ie2, indexStatus);
        }
        catch (Throwable t) {
            DirectoryConnectionSourceIndexCreator._logger.log(Level.SEVERE, "Unexpected exception while creating " + this.getIndexName(), t);
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, "Unexpected error occurred while creating " + this.getIndexName() + ". " + t.getMessage());
        }
        finally {
            Model.cleanupTransaction();
            indexStatus.stopCancelListener();
        }
    }
    
    private SimpleDataSourceIndexCreatorResult createIndex() throws ProfilesException, InterruptedException, IndexerException, IOException, KeyStorehouseException {
        if (!(this._infoSource instanceof DirectoryConnectionSource)) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
        }
        final Reader userGroupEntryReader = this.userGroupEntryReaderCreator.createUserGroupEntryReader();
        final PrintWriter errorWriter = IndexerConfig.shouldCreateErrorFile() ? this.createErrorWriter() : null;
        try {
            final KeyStorehouse keyStore = KeyStorehouseManager.getEDMKeyStore();
            final KeyContainer key = keyStore.getKeyContainerWithLatestKeyAlias();
            final DatabaseIndexCreator indexer = new DatabaseIndexCreator((SettingProvider)new IndexerPropertiesTranslator(), key);
            final DatabaseIndexCreatorResult databaseIndexCreatorResult = indexer.createIndex((DatabaseProfileDescriptor)new DirectoryConnectionSourceAdapter((DirectoryConnectionSource)this._infoSource), userGroupEntryReader, this.getRdxFile(), errorWriter);
            userGroupEntryReader.close();
            return new SimpleDataSourceIndexCreatorResult(databaseIndexCreatorResult);
        }
        finally {
            this.closeReader(userGroupEntryReader);
            if (errorWriter != null) {
                errorWriter.close();
            }
        }
    }
    
    private PrintWriter createErrorWriter() {
        final File errorFile = new File(IndexerConfig.getLocalIndexDirectory(), this._infoSource.getName() + ".err");
        try {
            errorFile.createNewFile();
            return new PrintWriter(new BufferedOutputStream(new FileOutputStream(errorFile)));
        }
        catch (IOException e) {
            DirectoryConnectionSourceIndexCreator._logger.log(Level.SEVERE, "Failed to open \"" + errorFile.getAbsolutePath() + "\".");
            return null;
        }
    }
    
    private void closeReader(final Reader reader) throws ProfilesException {
        try {
            reader.close();
        }
        catch (IOException e) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, e);
        }
    }
    
    private File getRdxFile() {
        return new File(IndexerConfig.getLocalIndexDirectory(), this.getIndexFileName(this._indexedInfoSource));
    }
    
    private String getIndexName() {
        if (this._indexedInfoSource == null) {
            return ProfilesBundle.getI18NMessage("profiles.message.Exact_Data_Profile", this._infoSource.getName());
        }
        return ProfilesBundle.getI18NMessage("profiles.message.Exact_Data_Profile_Version", this._infoSource.getName(), this._indexedInfoSource.getVersion());
    }
    
    private String getIndexFileName(final IndexedInfoSource indexedDirectoryConnectionSource) {
        return this.getIndexFileNamePrefix(indexedDirectoryConnectionSource) + ".rdx";
    }
    
    private String getIndexFileNamePrefix(final IndexedInfoSource indexedDirectoryConnectionSource) {
        return "DataSource." + indexedDirectoryConnectionSource.getInfoSource().getInfoSourceID() + '.' + indexedDirectoryConnectionSource.getVersion();
    }
}
