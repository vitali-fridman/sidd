// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import com.vontu.profiles.ProfilesBundle;
import java.io.FilenameFilter;
import com.vontu.model.Transaction;
import com.vontu.profiles.common.Cancellable;
import com.vontu.security.KeyStorehouseException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.database.DatabaseIndexCreatorResult;
import com.vontu.keystorehouse.KeyContainer;
import com.vontu.keystorehouse.KeyStorehouse;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindexer.database.DatabaseIndexCreator;
import com.vontu.vontukeystorehouse.common.KeyStorehouseManager;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.vontu.util.unicode.EncodingUtils;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import com.vontu.model.data.CompoundCondition;
import com.vontu.model.data.DatabaseInfoCondition;
import com.vontu.model.data.util.ConditionType;
import java.text.FieldPosition;
import java.text.DecimalFormat;
import java.util.Collection;
import com.vontu.model.data.Condition;
import java.util.Iterator;
import com.vontu.model.data.policy.ConditionFinder;
import com.vontu.model.data.Policy;
import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.profiles.manager.ProfileManagerError;
import java.io.File;
import com.vontu.model.DataAccessException;
import com.vontu.profiles.common.ProfilesError;
import com.vontu.model.data.Data;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.profiles.common.ProfilesException;
import com.vontu.logging.SystemEventUtil;
import java.util.logging.Level;
import com.vontu.profiles.common.IndexStatus;
import com.vontu.model.data.InfoSource;
import com.vontu.model.data.DataSource;
import com.vontu.model.Model;
import com.vontu.profiles.manager.InfoSourceIndexCreator;

public final class DataSourceIndexCreator extends InfoSourceIndexCreator
{
    private static final String FILE_NAME_PREFIX = "DataSource";
    private static final double _MAX_DETECTION_INACCURACY = 1.0E-9;
    public static final int PROXIMITY_RADIUS_DEFAULT = 35;
    public static final int TERMSTARTPOS = 1;
    public static final int TERMENDPOS = 29;
    public static final int ROWSTARTPOS = 31;
    public static final int ROWENDPOS = 41;
    public static final int COLSTARTPOS = 43;
    public static final int COLENDPOS = 53;
    
    public DataSourceIndexCreator(final Model model, final DataSource dataSource, final String triggerName) {
        super(model, (InfoSource)dataSource, triggerName, InfoSourceIndexCreator.getEventIdPrefix("database"));
    }
    
    @Override
    protected void indexInfoSourceOnManager() throws InterruptedException {
        IndexStatus indexStatus = null;
        try {
            this._indexedInfoSource = this.createIndexedInfoSource();
            indexStatus = new IndexStatus(this._model, this._indexedInfoSource, -1, 1);
            this.checkDataFile();
            this.doIndex(indexStatus);
        }
        catch (ProfilesException e) {
            if (indexStatus != null) {
                indexStatus.setFailed(e.getError());
            }
            DataSourceIndexCreator._logger.log(Level.SEVERE, e.getMessage(), e.getCause());
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
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e, "Failed to create exact data profile \"" + this._infoSource.getName() + "\" version " + indexVersion + '.');
        }
        finally {
            Model.cleanupTransaction();
        }
        return indexedDataSource;
    }
    
    private void checkDataFile() throws ProfilesException {
        final String dataFilePath = this._infoSource.getImportPath() + File.separator + this._infoSource.getFileName();
        final File databaseDumpFile = new File(dataFilePath);
        long dumpFileSize = 0L;
        if (databaseDumpFile.exists() && databaseDumpFile.canRead()) {
            dumpFileSize = databaseDumpFile.length();
        }
        if (DataSourceIndexCreator._logger.isLoggable(Level.FINE)) {
            DataSourceIndexCreator._logger.fine("Done looking for dump file " + dataFilePath + ". Found size " + dumpFileSize + '.');
        }
        if (dumpFileSize == 0L) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_DATA_FILE_NOT_FOUND, "Data file for " + this.getIndexName() + " could not be found.");
        }
    }
    
    private static void cleanupDataFile(final File dataFile) {
        if (IndexerConfig.shouldRemoveDumpFile()) {
            if (dataFile.exists() && !dataFile.delete()) {
                DataSourceIndexCreator._logger.severe("Error deleting database dump file " + dataFile.getName());
            }
            else if (DataSourceIndexCreator._logger.isLoggable(Level.FINE)) {
                DataSourceIndexCreator._logger.fine("Database dump file " + dataFile.getName() + " deleted.");
            }
        }
        else if (DataSourceIndexCreator._logger.isLoggable(Level.FINE)) {
            DataSourceIndexCreator._logger.fine("Database dump file " + dataFile.getName() + " retained.");
        }
    }
    
    private void checkPolicyAccuracy(final DataSourceIndexCreatorResult indexInfo) throws ProfilesException {
        Collection<Policy> policies = null;
        try {
            final Policy qbePolicy = (Policy)this._model.newDataObject((Class)Policy.class, false);
            qbePolicy.setIsDeleted(0);
            this._model.beginTransaction();
            policies = (Collection<Policy>)this._model.getDataCollectionByExample((Data)qbePolicy, Model.ComparisonType.WILDCARD);
            this._model.commitTransaction();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e);
        }
        finally {
            Model.cleanupTransaction();
        }
        for (final Policy policy : policies) {
            final ConditionFinder conditionExtractor = new ConditionFinder(policy);
            final StringBuffer inaccurateConditions = new StringBuffer(128);
            this.collectInaccurateConditions(indexInfo, conditionExtractor.getDetectionOrConditions(), inaccurateConditions);
            if (inaccurateConditions.length() > 0) {
                InfoSourceIndexCreator.logSystemEvent(Level.WARNING, "com.vontu.profiles.database.policy_inaccurate", policy.getName(), this.getIndexName(), inaccurateConditions.toString());
            }
        }
    }
    
    private void collectInaccurateConditions(final DataSourceIndexCreatorResult indexInfo, final Iterator<Condition> conditionIterator, final StringBuffer conditions) {
        final DecimalFormat formatter = new DecimalFormat("#.##E00");
        final FieldPosition position = new FieldPosition(0);
        while (conditionIterator.hasNext()) {
            final Condition condition = conditionIterator.next();
            switch (ConditionType.forCondition(condition)) {
                case DATABASE_INFO: {
                    final DatabaseInfoCondition databaseInfoCondition = (DatabaseInfoCondition)condition;
                    if (databaseInfoCondition.getDataSource().equals(this._infoSource)) {
                        final int columnCount = databaseInfoCondition.getThreshold();
                        final double inaccuracy = indexInfo.estimateFalsePositiveRate(columnCount, 35);
                        if (inaccuracy <= 1.0E-9) {
                            continue;
                        }
                        conditions.append(" Rule \"").append(condition.getName()).append('\"');
                        if (Double.compare(inaccuracy, 1.0) == 0) {
                            conditions.append(" will have false positive matches in all messages.");
                        }
                        else {
                            conditions.append(" will have false positive rate of ");
                            formatter.format(inaccuracy, conditions, position).append(" per message.");
                        }
                        continue;
                    }
                    continue;
                }
                case COMPOUND: {
                    final CompoundCondition compoundCondition = (CompoundCondition)condition;
                    this.collectInaccurateConditions(indexInfo, compoundCondition.conditionIterator(), conditions);
                    continue;
                }
            }
        }
    }
    
    private static PrintWriter createErrorWriter(final File dataSourceFile) {
        final File errorFile = new File(dataSourceFile.getAbsolutePath() + ".err");
        try {
            errorFile.createNewFile();
            return new PrintWriter(new BufferedOutputStream(new FileOutputStream(errorFile)));
        }
        catch (IOException e) {
            DataSourceIndexCreator._logger.log(Level.SEVERE, "Failed to open \"" + errorFile.getAbsolutePath() + "\".");
            return null;
        }
    }
    
    private Reader createDataSourceReader(final File dataSourceFile) throws ProfilesException {
        if (!(this._infoSource instanceof DataSource)) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
        }
        try {
            final String encoding = ((DataSource)this._infoSource).getCharacterEncoding();
            final BufferedInputStream fins = new BufferedInputStream(new FileInputStream(dataSourceFile));
            if (encoding.equalsIgnoreCase("UTF-8")) {
                EncodingUtils.skipUtf8Bom(fins);
            }
            return new BufferedReader(new InputStreamReader(fins, encoding));
        }
        catch (FileNotFoundException e) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_DATA_FILE_NOT_FOUND, e);
        }
        catch (UnsupportedEncodingException e2) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, e2);
        }
        catch (IOException e3) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_FILE_ACCESS_ERROR, e3);
        }
    }
    
    private static void closeReader(final Reader reader) throws ProfilesException {
        try {
            reader.close();
        }
        catch (IOException e) {
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, e);
        }
    }
    
    private DataSourceIndexCreatorResult importIndex(final File dataSourceFile) throws ProfilesException, IOException {
        final FileInputStream fileInput = new FileInputStream(dataSourceFile);
        final ObjectInputStream input = new ObjectInputStream(fileInput);
        try {
            final DataSourceIndexCreatorResult result = (DataSourceIndexCreatorResult)input.readObject();
            result.postCreationStage((DatabaseProfileDescriptor)new DataSourceAdapter((DataSource)this._infoSource), this.getRdxFile());
            return result;
        }
        catch (ClassNotFoundException e) {
            throw new ProfilesException(ProfilesError.PROFILES_CORRUPTED, e);
        }
        finally {
            input.close();
            fileInput.close();
        }
    }
    
    private DataSourceIndexCreatorResult createIndex(final File dataSourceFile) throws ProfilesException, InterruptedException, IndexerException, IOException, KeyStorehouseException {
        final Reader dataSourceReader = this.createDataSourceReader(dataSourceFile);
        final PrintWriter errorWriter = IndexerConfig.shouldCreateErrorFile() ? createErrorWriter(dataSourceFile) : null;
        try {
            final KeyStorehouse keyStore = KeyStorehouseManager.getEDMKeyStore();
            final KeyContainer key = keyStore.getKeyContainerWithLatestKeyAlias();
            final DatabaseIndexCreator indexer = new DatabaseIndexCreator((SettingProvider)new IndexerPropertiesTranslator(), key);
            final DatabaseIndexCreatorResult result = indexer.createIndex((DatabaseProfileDescriptor)new DataSourceAdapter((DataSource)this._infoSource), dataSourceReader, this.getRdxFile(), errorWriter);
            dataSourceReader.close();
            return new SimpleDataSourceIndexCreatorResult(result);
        }
        finally {
            closeReader(dataSourceReader);
            if (errorWriter != null) {
                errorWriter.close();
            }
        }
    }
    
    private void doIndex(final IndexStatus indexStatus) throws ProfilesException, InterruptedException {
        this.setIndexerThread(Thread.currentThread());
        indexStatus.startCancelListener(this);
        final File dataSourceFile = new File(this._infoSource.getImportPath(), this._infoSource.getFileName());
        try {
            DataSourceIndexCreatorResult indexingResult;
            if ("PDX".equals(((DataSource)this._infoSource).getFileFormat())) {
                indexingResult = this.importIndex(dataSourceFile);
            }
            else {
                indexingResult = this.createIndex(dataSourceFile);
            }
            final Transaction transaction = this._model.beginTransaction();
            transaction.waitLock((Data)this._indexedInfoSource, 1, false);
            this._indexedInfoSource.setKeyAlias(indexingResult.cryptoKeyAlias());
            this._indexedInfoSource.setFileSize((int)(indexingResult.size() / indexingResult.fileCount() / 1024L));
            this._indexedInfoSource.setNumberOfSubIndexes(indexingResult.fileCount());
            this._model.commitTransaction();
            this.switchOverToNewIndexedInfoSource();
            indexStatus.update(6);
            InfoSourceIndexCreator.logSystemEvent(Level.INFO, "com.vontu.profiles.database.created", this.getIndexName(), this._infoSource.getFileName(), String.valueOf(indexingResult.getProcessedRowCount()), String.valueOf(indexingResult.getInvalidRowCount()));
            DataSourceIndexCreator._logger.info(indexingResult.toString());
            cleanupDataFile(dataSourceFile);
            this.checkPolicyAccuracy(indexingResult);
        }
        catch (ProfilesException e) {
            this.cleanIndexFilesOnFailure();
            throw e;
        }
        catch (DataAccessException e2) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e2, "Failed to update " + this.getIndexName() + " after index completion.");
        }
        catch (IndexerException e3) {
            this.cleanIndexFilesOnFailure();
            throw InfoSourceIndexCreator.translateIndexerException(e3);
        }
        catch (InterruptedException e4) {
            this.cleanIndexFilesOnFailure();
            this.handleInterrupt(e4, indexStatus);
        }
        catch (Throwable t) {
            this.cleanIndexFilesOnFailure();
            DataSourceIndexCreator._logger.log(Level.SEVERE, "Unexpected exception while creating " + this.getIndexName(), t);
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, "Unexpected error occurred while creating " + this.getIndexName() + ". " + t.getMessage());
        }
        finally {
            Model.cleanupTransaction();
            indexStatus.stopCancelListener();
        }
    }
    
    private void cleanIndexFilesOnFailure() {
        if (this._indexedInfoSource != null) {
            final String filePrefix = getIndexFileNamePrefix(this._indexedInfoSource) + ".";
            final File indexDir = IndexerConfig.getLocalIndexDirectory();
            final File[] arr$;
            final File[] toDelete = arr$ = indexDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.startsWith(filePrefix);
                }
            });
            for (final File f : arr$) {
                f.delete();
            }
        }
    }
    
    public static String getIndexFileName(final IndexedInfoSource indexedDataSource) {
        return getIndexFileNamePrefix(indexedDataSource) + ".rdx";
    }
    
    static String getIndexFileNamePrefix(final IndexedInfoSource indexedDataSource) {
        return "DataSource." + indexedDataSource.getInfoSource().getInfoSourceID() + '.' + indexedDataSource.getVersion();
    }
    
    private String getIndexName() {
        if (this._indexedInfoSource == null) {
            return ProfilesBundle.getI18NMessage("profiles.message.Exact_Data_Profile", this._infoSource.getName());
        }
        return ProfilesBundle.getI18NMessage("profiles.message.Exact_Data_Profile_Version", this._infoSource.getName(), this._indexedInfoSource.getVersion());
    }
    
    private File getRdxFile() {
        return new File(IndexerConfig.getLocalIndexDirectory(), getIndexFileName(this._indexedInfoSource));
    }
}
