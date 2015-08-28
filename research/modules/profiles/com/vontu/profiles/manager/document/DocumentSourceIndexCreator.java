// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.util.config.SettingProvider;
import java.util.Map;
import com.vontu.util.config.MapSettingProvider;
import java.util.HashMap;
import com.vontu.model.data.ContentRoot;
import com.vontu.communication.data.ContentRootMarshallable;
import com.vontu.communication.data.CourseMarshallable;
import com.vontu.model.data.Course;
import com.vontu.cracker.DocumentFormatConstants;
import com.vontu.model.Transaction;
import com.vontu.profileindexer.IndexerException;
import com.vontu.cracker.DocumentFormat;
import com.vontu.cracker.EncodedString;
import com.vontu.cracker.jni.NativeEncodedString;
import com.vontu.util.unicode.CharacterEncoding;
import com.vontu.util.ByteContent;
import java.io.File;
import com.vontu.model.data.DocSourceDoc;
import java.util.Iterator;
import java.util.Collection;
import com.vontu.directorycrawler.ProfilerFileSystemCrawler;
import com.vontu.profiles.manager.ProfileManagerError;
import java.io.IOException;
import com.vontu.directorycrawler.VontuFileException;
import com.vontu.profiles.common.Cancellable;
import com.vontu.logging.SystemEventUtil;
import java.util.logging.Level;
import com.vontu.profiles.common.IndexStatus;
import com.vontu.logging.operational.api.OperationalLog;
import com.vontu.profiles.log.ProfilesOperationalLogCodes;
import com.vontu.model.DataAccessException;
import com.vontu.profiles.common.ProfilesException;
import java.text.MessageFormat;
import com.vontu.profiles.common.ProfilesError;
import com.vontu.model.data.Data;
import com.vontu.model.data.IndexedInfoSource;
import com.vontu.util.unicode.CharacterEncodingManager;
import com.vontu.model.data.InfoSource;
import com.vontu.util.config.SettingReader;
import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.model.data.DocSource;
import com.vontu.model.Model;
import com.vontu.docindex.algorithm.IDMEndpointNormalizer;
import com.vontu.docindex.algorithm.ContentNormalizer;
import com.vontu.util.ByteArrayToCharSequenceConverter;
import com.vontu.discover.mapper.CourseMapper;
import com.vontu.profileindexer.document.EndpointDocumentIndexCreator;
import com.vontu.profileindexer.document.DocumentIndexCreator;
import com.vontu.profiles.manager.InfoSourceIndexCreator;

public class DocumentSourceIndexCreator extends InfoSourceIndexCreator
{
    private DocumentIndexCreator _indexCreator;
    private EndpointDocumentIndexCreator endpointDocumentIndexCreator;
    private String _extractDirectory;
    private int _numDocAdded;
    private int _numDocUpdated;
    private int _numDocRemoved;
    private int _numDocUnchanged;
    private int _numDocBig;
    private int _numDocNoAccess;
    private int _numDocuments;
    private boolean _incremental;
    private final CrackingProcessor _crackerProcessor;
    private final CourseMapper _courseMapper;
    private final DocumentRepositoryFactory docRepositoryFactory;
    private final ProfilerFileSystemCrawlerCreator profilerFileSystemCrawlerCreator;
    private ByteArrayToCharSequenceConverter byteArrayToCharSequenceConverter;
    private final ContentNormalizer contentNormalizer;
    private int minNormalizedSize;
    private final IDMEndpointNormalizer idmEndpointNormalizer;
    public static final String MIN_NORMALIZED_SIZE = "min_normalized_size";
    public static final int MIN_NORMALIZED_SIZE_DEFAULT = 30;
    
    public DocumentSourceIndexCreator(final Model model, final DocSource docSource, final String triggerName) {
        this(model, docSource, triggerName, new CrackingProcessor(ContentExtractionServiceForDocumentIndexer.getInstance(), IndexerConfig.getSettings()), new DocumentRepositoryFactory(), new ProfilerFileSystemCrawlerCreator(), new CourseMapper(), new ContentNormalizer(), new IDMEndpointNormalizer(), new SettingReader(IndexerConfig.getSettingProvider()));
    }
    
    DocumentSourceIndexCreator(final Model model, final DocSource docSource, final String triggerName, final CrackingProcessor crackingProcessor, final DocumentRepositoryFactory documentRepositoryFactory, final ProfilerFileSystemCrawlerCreator profilerFileSystemCrawlerCreator, final CourseMapper courseMapper, final ContentNormalizer contentNormalizer, final IDMEndpointNormalizer idmEndpointNormalizer, final SettingReader settingReader) {
        super(model, (InfoSource)docSource, triggerName, InfoSourceIndexCreator.getEventIdPrefix("document"));
        this._extractDirectory = null;
        this._numDocAdded = 0;
        this._numDocUpdated = 0;
        this._numDocRemoved = 0;
        this._numDocUnchanged = 0;
        this._numDocBig = 0;
        this._numDocNoAccess = 0;
        this._numDocuments = 0;
        this._crackerProcessor = crackingProcessor;
        this._courseMapper = courseMapper;
        this.docRepositoryFactory = documentRepositoryFactory;
        this.profilerFileSystemCrawlerCreator = profilerFileSystemCrawlerCreator;
        this.byteArrayToCharSequenceConverter = new ByteArrayToCharSequenceConverter(CharacterEncodingManager.getInstance());
        this.contentNormalizer = contentNormalizer;
        this.idmEndpointNormalizer = idmEndpointNormalizer;
        this.minNormalizedSize = settingReader.getIntSetting("min_normalized_size", 30);
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
            indexedDataSource.setNumOfDocuments(0);
            indexedDataSource.setFileSize(0);
            indexedDataSource.setTriggerName(this._triggerName);
            indexedDataSource.setNumberOfSubIndexes(1);
            this._model.beginTransaction();
            this._model.persistDataObject((Data)indexedDataSource, true);
            this._model.commitTransaction();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e, MessageFormat.format("Failed to create document profile \"{0}\" version {1}", this._infoSource.getName(), indexVersion));
        }
        finally {
            Model.cleanupTransaction();
        }
        return indexedDataSource;
    }
    
    public void indexInfoSourceOnManager() throws InterruptedException {
        OperationalLog.logEvent(ProfilesOperationalLogCodes.IDM_INDEXER_STARTING, new Object[] { this._infoSource.getInfoSourceID(), this._infoSource.getVersion(), this._infoSource.getName() });
        IndexStatus indexStatus = null;
        try {
            this._indexedInfoSource = this.createIndexedInfoSource();
            indexStatus = new IndexStatus(this._model, this._indexedInfoSource, -1, 1);
            checkKeyStorehouse();
            this.doIndex(indexStatus);
            OperationalLog.logEvent(ProfilesOperationalLogCodes.IDM_INDEXER_COMPLETE, new Object[] { this._infoSource.getInfoSourceID(), this._infoSource.getVersion(), this._infoSource.getName(), this._numDocuments });
        }
        catch (ProfilesException e) {
            if (indexStatus != null) {
                indexStatus.setFailed(e.getError());
            }
            OperationalLog.logError(ProfilesOperationalLogCodes.IDM_INDEXER_FAILED, (Throwable)e, new Object[] { this._infoSource.getInfoSourceID(), this._infoSource.getVersion(), this._infoSource.getName() });
            SystemEventUtil.log(-1, Level.SEVERE, "com.vontu.profiles.document.creation_failed", new String[] { e.getMessage(), e.getMessage() });
        }
    }
    
    private void doIndex(final IndexStatus indexStatus) throws ProfilesException, InterruptedException {
        this.setIndexerThread(Thread.currentThread());
        indexStatus.startCancelListener(this);
        ProfilerFileSystemCrawler crawler = null;
        Collection<DocSourceDocWrapper> documents = null;
        try {
            final DocSource docSource = (DocSource)this._infoSource;
            crawler = this.createCrawler(docSource);
            if (crawler == null) {
                indexStatus.update(26);
                InfoSourceIndexCreator.logSystemEvent(Level.WARNING, "com.vontu.profiles.document.profile.none_indexed", this._infoSource.getName());
                return;
            }
            this.createIndexCreator(this._incremental = this.canDoIncrementalIndexing(this._indexedInfoSource.getVersion(), IndexerConfig.getLocalIndexDirectory().getAbsolutePath(), this._infoSource.getInfoSourceID()));
            final DocumentRepository documentsRepository = this.docRepositoryFactory.createDocumentRepository(this._model, this._infoSource, crawler);
            documents = documentsRepository.getDocuments();
            final Iterator<DocSourceDocWrapper> iter = documents.iterator();
            int loopCount = 0;
            final int docCountInterval = IndexerConfig.getDocCountUpdateInterval();
            int lastId = Integer.MIN_VALUE;
            while (iter.hasNext()) {
                final DocSourceDocWrapper document = iter.next();
                final DocSourceDoc docSourceDoc = document.getDocSourceDoc();
                final int docId = docSourceDoc.getDocSourceDocID();
                if (docId < lastId) {
                    throw new IllegalArgumentException("DocId " + docId + " should be greater then previous " + lastId);
                }
                lastId = docId;
                try {
                    if (this._incremental) {
                        switch (document.getStatus()) {
                            case 3: {
                                this.putDocumentInIndexer(document, docSourceDoc.getDocSourceDocID(), docSourceDoc.getName(), false);
                                ++this._numDocAdded;
                                break;
                            }
                            case 2: {
                                this.putDocumentInIndexer(document, docSourceDoc.getDocSourceDocID(), docSourceDoc.getName(), true);
                                ++this._numDocUpdated;
                                break;
                            }
                            case 0: {
                                if (docSourceDoc.getActiveInDetection() == 1) {
                                    this._indexCreator.deleteDocument(docSourceDoc.getDocSourceDocID());
                                    ++this._numDocRemoved;
                                    break;
                                }
                                break;
                            }
                            case 1: {
                                ++this._numDocUnchanged;
                                break;
                            }
                            default: {
                                throw new RuntimeException("Progamming error: invalid Document status.");
                            }
                        }
                    }
                    else if (document.getStatus() != 0) {
                        this.putDocumentInIndexer(document, docSourceDoc.getDocSourceDocID(), docSourceDoc.getName(), false);
                        ++this._numDocAdded;
                    }
                    if (this._indexCreator.getIndexSize() >= getMaxIndexSize()) {
                        final int filesIndexed = this._numDocAdded + this._numDocUpdated + this._numDocUnchanged;
                        InfoSourceIndexCreator.logSystemEvent(Level.WARNING, "com.vontu.profiles.document.profile.max_size", this._infoSource.getName(), String.valueOf(filesIndexed), String.valueOf(documents.size()));
                        break;
                    }
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
                catch (VontuFileException e) {
                    document.setStatus(0);
                    this.handleRecoverableVontuFileExecption(e);
                }
                catch (IOException e2) {
                    document.setStatus(0);
                    ++this._numDocNoAccess;
                    if (DocumentSourceIndexCreator._logger.isLoggable(Level.FINE)) {
                        DocumentSourceIndexCreator._logger.log(Level.FINE, "Error during document indexing", e2.getMessage());
                    }
                }
                if (++loopCount != docCountInterval) {
                    continue;
                }
                loopCount = 0;
                this.updateDocumentCount();
            }
            this.updateDocSourceDocStatus(documents);
            this._numDocuments = this.getNumIndexed();
            if (this._numDocuments == 0) {
                this._indexCreator.cancel();
                this.endpointDocumentIndexCreator.cancelIndexCreation();
                indexStatus.update(23);
                InfoSourceIndexCreator.logSystemEvent(Level.WARNING, "com.vontu.profiles.document.profile.none_indexed", this._infoSource.getName());
            }
            else {
                this.logDocumentProfileCreation(indexStatus, documentsRepository.getNumFiles());
                this.switchOverToNewIndexedInfoSource();
            }
        }
        catch (InterruptedException e3) {
            Model.cleanupTransaction();
            this.handleInterrupt(e3, indexStatus);
        }
        catch (VontuFileException e4) {
            handleUnrecoverableVontuFileException(e4);
        }
        catch (Throwable t) {
            DocumentSourceIndexCreator._logger.log(Level.SEVERE, "Error during document indexing", t);
            Model.cleanupTransaction();
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
        }
        finally {
            this.cleanup(crawler, indexStatus);
        }
    }
    
    private boolean canDoIncrementalIndexing(final int version, final String absolutePath, final int infoSourceID) {
        final DocumentIndexFileGeneratorImpl fileGen = new DocumentIndexFileGeneratorImpl(absolutePath, version, infoSourceID);
        final File prevFullIndex = fileGen.getIndexFile(version - 1, false);
        return prevFullIndex.exists() && prevFullIndex.isFile();
    }
    
    private void putDocumentInIndexer(final DocSourceDocWrapper document, final int docSourceDocId, final String docName, final boolean replace) throws IOException, InterruptedException, VontuFileException, IndexerException {
        EncodedString crackedContent = null;
        boolean wasCracked = false;
        byte[] content;
        if (document.getFile().length() <= IndexerConfig.getMaxDocumentSize()) {
            content = document.getFile().getByteArray();
            try {
                crackedContent = this._crackerProcessor.crackDocument(content, null, docName);
            }
            catch (DocumentExtractionException e) {
                DocumentSourceIndexCreator._logger.log(Level.INFO, "Unable to perform content extraction on [" + docName + "] while indexing" + e);
            }
            if (null != crackedContent) {
                wasCracked = (crackedContent.getBytes() != null && crackedContent.getBytes().length > 0);
            }
            else {
                DocumentSourceIndexCreator._logger.log(Level.INFO, "Unable to perform content extraction on [" + docName + "] while indexing");
            }
        }
        else {
            content = document.getFile().getByteArray(IndexerConfig.getMaxDocumentSize());
            crackedContent = (EncodedString)new FakeEncodedString();
            wasCracked = false;
        }
        if (!wasCracked) {
            if (replace) {
                this._indexCreator.replaceBinaryDocument(docSourceDocId, docName, content);
            }
            else {
                this._indexCreator.addBinaryDocument(docSourceDocId, docName, content);
                this.endpointDocumentIndexCreator.addDocument(docSourceDocId, content);
            }
            document.setCrackable(wasCracked);
        }
        else {
            final CharacterEncoding encoding = this.getEncodingForDocument(crackedContent);
            final ByteContent byteContent = new ByteContent(crackedContent.getBytes(), encoding);
            final CharSequence decodedContent = byteContent.getDecodedContent();
            final CharSequence normalizedContent = this.contentNormalizer.normalize(decodedContent);
            final String unknownEncoding = "UNKNOWN";
            if (crackedContent.getEncodingName().equalsIgnoreCase(unknownEncoding)) {
                final ByteArrayToCharSequenceConverter.Result result = this.byteArrayToCharSequenceConverter.convert(crackedContent.getBytes(), CharacterEncoding.UNKNOWN);
                final CharSequence charSequence = result.getChars();
                crackedContent = (EncodedString)new NativeEncodedString("UTF-8", charSequence.toString().getBytes("UTF-8"));
            }
            final CharSequence normalizedContentForEndpoint = this.idmEndpointNormalizer.normalize(new String(crackedContent.getBytes(), crackedContent.getEncodingName()));
            boolean finalCracked = true;
            if (normalizedContent.length() < this.minNormalizedSize) {
                this._indexCreator.addBinaryDocument(docSourceDocId, docName, content);
                finalCracked = false;
            }
            else {
                this._indexCreator.addCharDocument(docSourceDocId, docName, normalizedContent);
            }
            if (normalizedContentForEndpoint.length() < this.minNormalizedSize) {
                this.endpointDocumentIndexCreator.addDocument(docSourceDocId, content);
            }
            else {
                this.endpointDocumentIndexCreator.addDocument(docSourceDocId, normalizedContentForEndpoint.toString().getBytes("UTF-8"));
            }
            document.setCrackable(finalCracked);
        }
        try {
            final DocumentFormat docFormat = this._crackerProcessor.getFormatForFile(content);
            document.setFileType(docFormat);
        }
        catch (DocumentExtractionException e) {
            DocumentSourceIndexCreator._logger.log(Level.INFO, "Unable to get Document format for [" + docName + "] while indexing" + e);
        }
    }
    
    private CharacterEncoding getEncodingForDocument(final EncodedString crackedContent) {
        return (crackedContent.getEncodingName() != null) ? CharacterEncodingManager.getInstance().getEncoding(crackedContent.getEncodingName()) : null;
    }
    
    private void updateIndexedInfoSource(final int noDocuments, long fileSize) throws ProfilesException, InterruptedException {
        try {
            fileSize /= 1024L;
            final Transaction transaction = this._model.beginTransaction();
            transaction.waitLock((Data)this._indexedInfoSource, 1, false);
            this._indexedInfoSource.setNumOfDocuments(noDocuments);
            this._indexedInfoSource.setFileSize((int)fileSize);
            this._model.commitTransaction();
        }
        catch (DataAccessException e) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, (Throwable)e, "Failed to update " + this._infoSource.getName() + " after index completion.");
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    private void activateDocSourceDoc(final DocSourceDoc document, final DocumentFormat fileType, final boolean isCrackable) throws DataAccessException, InterruptedException {
        final Transaction transaction = this._model.currentTransaction();
        transaction.waitLock((Data)document, 1, false);
        if (fileType != null) {
            document.setFileType(fileType.getId());
        }
        else {
            document.setFileType(DocumentFormatConstants.UNKNOWN.getId());
        }
        if (document.getActiveInDetection() == 0) {
            document.setActiveInDetection(1);
        }
        if (isCrackable) {
            document.setIsCrackable(1);
        }
        else {
            document.setIsCrackable(0);
        }
    }
    
    private void deactivateDocSourceDoc(final DocSourceDoc document) throws DataAccessException, InterruptedException {
        this._model.currentTransaction().waitLock((Data)document, 1, false);
        document.setActiveInDetection(0);
    }
    
    private CourseMarshallable convertCourseAndUpdateScanDepth(final Course course) throws DataAccessException, InterruptedException, ProfilesException {
        final CourseMarshallable discoverCourse = this._courseMapper.convert(course);
        for (final ContentRootMarshallable contentRoot : discoverCourse.getContentRoots()) {
            contentRoot.setMaxDepth(IndexerConfig.getMaxDirectoryDepth());
        }
        return discoverCourse;
    }
    
    private static long getMaxIndexSize() {
        return IndexerConfig.getMaxLoadedIndexMemory() - IndexerConfig.getIndexProcessMemoryReserve();
    }
    
    private ProfilerFileSystemCrawler createCrawler(final DocSource docSource) throws InterruptedException, ProfilesException, DataAccessException {
        final String fileName = docSource.getFileName();
        if (!"No Archive Files".equals(fileName)) {
            final Iterator<ContentRoot> iter = (Iterator<ContentRoot>)docSource.getCourse().contentRootIterator();
            if (iter.hasNext()) {
                final ContentRoot contentRoot = iter.next();
                if (contentRoot.getIsDeleted() == 0) {
                    this._extractDirectory = contentRoot.getUri();
                    this.unpackZip(System.getProperty("com.vontu.documents.dir") + File.separator + docSource.getFileName());
                }
            }
        }
        return this.profilerFileSystemCrawlerCreator.createCrawler(docSource.getName(), this.convertCourseAndUpdateScanDepth(docSource.getCourse()));
    }
    
    private void unpackZip(final String zipFile) throws ProfilesException {
        final File rootDirFile = new File(this._extractDirectory);
        try {
            rootDirFile.delete();
            final Unzip unzip = new Unzip(this._extractDirectory, zipFile);
            unzip.unzip();
            new File(zipFile).delete();
        }
        catch (Throwable e) {
            rootDirFile.delete();
            throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_FILE_ACCESS_ERROR, "Cannot extract zip file " + zipFile + " to " + this._extractDirectory);
        }
    }
    
    private void createIndexCreator(final boolean incremental) throws IOException, IndexerException {
        final DocumentIndexFileGeneratorImpl docIndexFileGen = new DocumentIndexFileGeneratorImpl(IndexerConfig.getLocalIndexDirectory().getAbsolutePath(), this._infoSource.getInfoSourceID(), this._indexedInfoSource.getVersion());
        final File wlFileForDocSource = docIndexFileGen.getWhiteListedFileForDocSource();
        final File wlFileCommon = docIndexFileGen.getWhiteListedFileCommon();
        final Map<String, String> whiteListSettingsMap = new HashMap<String, String>();
        whiteListSettingsMap.put("common_whitelist_file", wlFileCommon.getAbsolutePath());
        whiteListSettingsMap.put("docsource_whitelist_file", wlFileForDocSource.getAbsolutePath());
        final IndexerSettingProvider settings = new IndexerSettingProvider(IndexerConfig.getSettingProvider());
        settings.insertSettingProvider((SettingProvider)new MapSettingProvider((Map)whiteListSettingsMap));
        final File nextIndexFile = docIndexFileGen.getIndexFile();
        if (!incremental) {
            this._indexCreator = new DocumentIndexCreator((SettingProvider)settings, nextIndexFile);
        }
        else {
            final File prevFile = docIndexFileGen.getPrevIndexFile();
            this._indexCreator = new DocumentIndexCreator((SettingProvider)settings, nextIndexFile, prevFile);
        }
        this.endpointDocumentIndexCreator = EndpointIndexCreatorFactory.create(settings, incremental, docIndexFileGen);
    }
    
    private void logDocumentProfileCreation(final IndexStatus indexStatus, final int numFilesInRepository) throws IOException, InterruptedException, ProfilesException {
        this.updateIndexedInfoSource(this._numDocuments, this._indexCreator.getIndexSize());
        this._indexCreator.finish();
        this.endpointDocumentIndexCreator.finish();
        indexStatus.update(6);
        String incrementalMsg = "";
        if (this._incremental) {
            incrementalMsg = " Comparing to last indexing run, " + this._numDocAdded + " new document(s) were added, " + this._numDocUpdated + " document(s) were updated, " + this._numDocUnchanged + " document(s) were unchanged, and " + this._numDocRemoved + " document(s) were removed.";
        }
        String excludeMsg = " ";
        if (this._numDocBig != 0) {
            excludeMsg = excludeMsg + this._numDocBig + " file(s) are too big to index. ";
        }
        if (this._numDocNoAccess != 0) {
            excludeMsg = excludeMsg + this._numDocNoAccess + " file(s) can not be read properly. ";
        }
        InfoSourceIndexCreator.logSystemEvent(Level.INFO, "com.vontu.profiles.document.created", this._infoSource.getName(), this._infoSource.getImportPath(), Integer.toString(numFilesInRepository), excludeMsg, Integer.toString(this._numDocuments), incrementalMsg);
    }
    
    private void updateDocSourceDocStatus(final Collection<DocSourceDocWrapper> documents) throws DataAccessException, InterruptedException {
        this._model.beginTransaction();
        try {
            for (final DocSourceDocWrapper docWrapper : documents) {
                switch (docWrapper.getStatus()) {
                    case 2:
                    case 3: {
                        this.activateDocSourceDoc(docWrapper.getDocSourceDoc(), docWrapper.getFileType(), docWrapper.isCrackable());
                        continue;
                    }
                    case 0: {
                        this.deactivateDocSourceDoc(docWrapper.getDocSourceDoc());
                        continue;
                    }
                }
            }
            this._model.commitTransaction();
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    private void cleanup(final ProfilerFileSystemCrawler crawler, final IndexStatus indexStatus) {
        if (crawler != null) {
            crawler.unmount();
        }
        if (this._indexCreator != null && this._indexCreator.isRunning()) {
            try {
                this._indexCreator.cancel();
            }
            catch (IOException e) {
                if (DocumentSourceIndexCreator._logger.isLoggable(Level.SEVERE)) {
                    DocumentSourceIndexCreator._logger.log(Level.SEVERE, "Cannot shutdown the Index Creator for document profile.", e);
                }
            }
        }
        if (this._extractDirectory != null && !IndexerConfig.retainZipContent()) {
            final File dir = new File(this._extractDirectory);
            deleteDir(dir);
        }
        indexStatus.stopCancelListener();
    }
    
    private void handleRecoverableVontuFileExecption(final VontuFileException e) throws VontuFileException {
        try {
            if (e.getNtStatus() != 11 && e.getNtStatus() != 15) {
                if (DocumentSourceIndexCreator._logger.isLoggable(Level.FINE)) {
                    DocumentSourceIndexCreator._logger.log(Level.FINE, "Cannot access file: ", (Throwable)e);
                    DocumentSourceIndexCreator._logger.log(Level.FINE, "File name is " + ((e.getCulprit() != null) ? e.getCulprit().getCanonicalPath() : "unknown"));
                }
                throw e;
            }
            if (DocumentSourceIndexCreator._logger.isLoggable(Level.FINE)) {
                DocumentSourceIndexCreator._logger.log(Level.FINE, "Cannot access file: ", (Throwable)e);
                DocumentSourceIndexCreator._logger.log(Level.FINE, "File name is " + ((e.getCulprit() != null) ? e.getCulprit().getCanonicalPath() : "unknown"));
            }
        }
        catch (InterruptedException ex) {}
        ++this._numDocNoAccess;
    }
    
    private static void handleUnrecoverableVontuFileException(final VontuFileException e) throws InterruptedException, ProfilesException {
        DocumentSourceIndexCreator._logger.log(Level.SEVERE, "Error during document indexing", (Throwable)e);
        SystemEventUtil.log(-1, Level.SEVERE, "com.vontu.profiles.document.creation_failed", new String[] { e.getSimpleMessage(), e.getMessage() });
        switch (e.getNtStatus()) {
            case 12: {
                throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_NETWORK_DISCONNECT);
            }
            case 11: {
                throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_INVALID_LOGIN);
            }
            default: {
                throw new ProfilesException(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR);
            }
        }
    }
    
    private int getNumIndexed() {
        return this._numDocAdded + this._numDocUpdated + this._numDocUnchanged;
    }
    
    private void updateDocumentCount() throws ProfilesException {
        final Transaction transaction = this._model.beginTransaction();
        try {
            if (transaction.tryLock((Data)this._indexedInfoSource, 1, false)) {
                this._indexedInfoSource.setNumOfDocuments(this.getNumIndexed());
            }
            this._model.commitTransaction();
        }
        catch (Throwable t) {
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, t, MessageFormat.format("Failed to update document count while indexing \"{0}\"", this._infoSource.getName()));
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    private static boolean deleteDir(final File file) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (int i = 0; i < files.length; ++i) {
                final boolean result = deleteDir(files[i]);
                if (!result) {
                    return false;
                }
            }
        }
        return file.delete();
    }
    
    private static final class FakeEncodedString implements EncodedString
    {
        public String getEncodingName() {
            return null;
        }
        
        public byte[] getBytes() {
            return new byte[0];
        }
    }
}
