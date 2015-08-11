// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import com.vontu.docindex.util.BinaryMatchUtil;
import com.vontu.docindex.util.IndexUtil;
import java.util.logging.Level;
import com.vontu.profileindexer.IndexerException;
import java.util.Iterator;
import com.vontu.docindex.algorithm.HashWithPosition;
import java.util.HashSet;
import com.vontu.util.unicode.CharacterConversionException;
import com.vontu.util.filesystem.FileUtil;
import java.security.NoSuchAlgorithmException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.vontu.util.config.SettingProvider;
import java.util.Set;
import com.vontu.util.config.SettingReader;
import com.twmacinta.util.MD5;
import java.security.MessageDigest;
import com.vontu.docindex.algorithm.ContentNormalizer;
import com.vontu.docindex.algorithm.HashSelector;
import java.io.DataOutputStream;
import java.io.File;
import java.util.logging.Logger;

public class DocumentIndexCreator implements DocIndexCreator
{
    private static final String SNIPPET_USE_THRESHOLD = "snippet_use_threshold";
    private static final int SNIPPET_USE_THRESHOLD_DEFAULT = 5;
    private static final String LOWEST_CONTAINMENT_PERCENT = "lowest_containment_percent";
    private static final int LOWEST_CONTAINMENT_PERCENT_DEFAULT = 10;
    private static final String SNIPPET_NUM_BYTES = "snippet_num_bytes";
    private static final int SNIPPET_NUM_BYTES_DEFAULT = 2;
    private static final String NOISE_THRESHOLD = "low_threshhold_k";
    private static final int NOISE_THRESHOLD_DEFAULT = 30;
    private static final String GUARANTEE_TRESHOLD = "high_threshhold_t";
    private static final int GUARANTEE_THRESHOLD_DEFAULT = 130;
    private static final String RAM_PER_HASH = "ram_per_hash";
    private static final int RAM_PER_HASH_DEFAULT = 38;
    private static final String RAM_PER_DOC = "ram_per_doc";
    private static final int RAM_PER_DOC_DEFAULT = 150;
    private static final String RAM_PER_SNIPPET = "ram_per_snippet";
    private static final int RAM_PER_SNIPPET_DEFAULT = 10;
    private static final String MAX_BIN_MATCH_SIZE = "max_bin_match_size";
    private static final int MAX_BIN_MATCH_SIZE_DEFAULT = 30000000;
    private static final String RETAIN_ALL_FILES = "com.vontu.profiles.documents.retainAllFiles";
    private static final boolean RETAIN_ALL_FILES_DEFAULT = false;
    public static final int MAGIC_NUMBER = 62052;
    public static final String USE_JAVA_MD5 = "use_java_md5";
    public static final boolean USE_JAVA_MD5_DEFAULT = false;
    public static final String MIN_NORMALIZED_SIZE = "min_normalized_size";
    public static final int MIN_NORMALIZED_SIZE_DEFAULT = 30;
    public static final String WHITE_LIST_COMMON_FILE = "common_whitelist_file";
    public static final String WHITE_LIST_DOCSOURCE_FILE = "docsource_whitelist_file";
    public static final int MD5_SIZE = 16;
    private static final Logger _logger;
    private static final int BUFFER_SIZE = 524288;
    private static final int INT_SIZE = 4;
    private static final int HEADER_LENGTH = 32;
    private static final int END_OF_FILE = Integer.MAX_VALUE;
    private static final int ENTRY_ADD = 0;
    private static final int ENTRY_REPLACE = 1;
    private static final int ENTRY_DELETE = 2;
    private boolean _incremental;
    private int _k;
    private int _t;
    private File _finalIndexFile;
    private File _intermediateIndexFile;
    private File _previousIndexFile;
    private DataOutputStream _indexFileStream;
    private HashSelector _hashSelector;
    private ContentNormalizer _contentNormalizer;
    private int _numDocs;
    private int _numHashes;
    private int _numDocsWithSnippets;
    private boolean _running;
    private int _averageHashesPerDocInPreviousVersion;
    private int _snippetCheckThreshold;
    private int _fingerpintSizeThresholdForSnippet;
    private int _snippetNumBytes;
    private int _incrEntryType;
    private int _incrDocId;
    private int _docId;
    private int _snippetPresent;
    private MessageDigest _digest;
    private MD5 _md5;
    private boolean _useJavaMD5;
    private int _maxBinMatchSize;
    private boolean _retainAllFiles;
    private SettingReader _settings;
    private Set<Integer> _whitelisted;
    private int _min_normalized_size;
    
    public DocumentIndexCreator(final SettingProvider settingProvider, final File indexFile) throws IOException {
        this._whitelisted = null;
        this._incremental = false;
        if (null == indexFile) {
            throw new IllegalArgumentException("NullPointer given for indexFile in constructor");
        }
        this._finalIndexFile = indexFile;
        this._intermediateIndexFile = this._finalIndexFile;
        this._settings = new SettingReader(settingProvider);
        this.initialize();
    }
    
    public DocumentIndexCreator(final SettingProvider settingProvider, final File indexFile, final File previousIndexFile) throws IOException {
        this._whitelisted = null;
        this._incremental = true;
        if (null == previousIndexFile || !previousIndexFile.exists()) {
            throw new IllegalArgumentException("A previously existing index file should be given for incremental indexing");
        }
        this._previousIndexFile = previousIndexFile;
        this._finalIndexFile = indexFile;
        this._intermediateIndexFile = new File(this._finalIndexFile.getAbsolutePath() + ".i");
        this._settings = new SettingReader(settingProvider);
        this.initialize();
    }
    
    private void initialize() throws IOException {
        this._numDocs = 0;
        this._numHashes = 0;
        this._numDocsWithSnippets = 0;
        this._running = true;
        this._averageHashesPerDocInPreviousVersion = 0;
        this._snippetCheckThreshold = this._settings.getIntSetting("snippet_use_threshold", 5);
        int lowestContainmentPercent = this._settings.getIntSetting("lowest_containment_percent", 10);
        if (lowestContainmentPercent < 1) {
            lowestContainmentPercent = 1;
        }
        this._fingerpintSizeThresholdForSnippet = this._snippetCheckThreshold * (100 / lowestContainmentPercent);
        this._snippetNumBytes = this._settings.getIntSetting("snippet_num_bytes", 2);
        this._k = this._settings.getIntSetting("low_threshhold_k", 30);
        this._t = this._settings.getIntSetting("high_threshhold_t", 130);
        if (this._incremental) {
            final DataInputStream prevVersionFileStream = new DataInputStream(new FileInputStream(this._previousIndexFile));
            if (prevVersionFileStream.readInt() != 62052) {
                throw new IOException("Magic number is not correct on previous version of index");
            }
            if (this._k != prevVersionFileStream.readInt()) {
                throw new IllegalStateException("Value of k has changed in incremental indexing");
            }
            if (this._t != prevVersionFileStream.readInt()) {
                throw new IllegalStateException("Value of t has changed in incremental indexing");
            }
            if (this._snippetNumBytes != prevVersionFileStream.readInt()) {
                throw new IllegalStateException("Number of snippet bytes has changed in incremental indexing");
            }
            if (this._snippetCheckThreshold != prevVersionFileStream.readInt()) {
                throw new IllegalStateException("Number of snippet check threshold has changed in incremental indexing");
            }
            this._numDocs = prevVersionFileStream.readInt();
            this._numHashes = prevVersionFileStream.readInt();
            this._numDocsWithSnippets = prevVersionFileStream.readInt();
            this._averageHashesPerDocInPreviousVersion = this._numHashes / this._numDocs;
            prevVersionFileStream.close();
        }
        this._contentNormalizer = new ContentNormalizer();
        this._hashSelector = new HashSelector(this._k, this._t);
        this._indexFileStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this._intermediateIndexFile), 524288));
        if (!this._incremental) {
            this.leaveSpaceForHeader(this._indexFileStream);
        }
        else {
            this._indexFileStream.writeInt(this._snippetNumBytes);
        }
        this._useJavaMD5 = this._settings.getBooleanSetting("use_java_md5", false);
        this._maxBinMatchSize = this._settings.getIntSetting("max_bin_match_size", 30000000);
        Label_0446: {
            if (this._useJavaMD5) {
                try {
                    this._digest = MessageDigest.getInstance("MD5");
                    break Label_0446;
                }
                catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("Unable to get MD5 implementation");
                }
            }
            this._md5 = new MD5();
        }
        this._retainAllFiles = this._settings.getBooleanSetting("com.vontu.profiles.documents.retainAllFiles", false);
        this._min_normalized_size = this._settings.getIntSetting("min_normalized_size", 30);
        this.readWhitelisted();
    }
    
    private void readWhitelisted() {
        final File wlFileForDocSource = new File(this._settings.getSetting("docsource_whitelist_file"));
        final File wlFileCommon = new File(this._settings.getSetting("common_whitelist_file"));
        File wlFile = null;
        if (wlFileForDocSource.exists()) {
            wlFile = wlFileForDocSource;
            DocumentIndexCreator._logger.info("Using existing whitelisted file for document source: " + wlFile.getName());
        }
        else {
            if (!wlFileCommon.exists()) {
                return;
            }
            wlFile = wlFileCommon;
            DocumentIndexCreator._logger.info("Using common whitelisted file and making a copy for this document source.");
            try {
                FileUtil.copyFile(wlFileCommon, wlFileForDocSource);
            }
            catch (IOException e) {
                DocumentIndexCreator._logger.warning("Error making copy of whitelisted file: " + e.toString());
            }
        }
        String wlText = null;
        try {
            wlText = FileUtil.getContentFromFile(wlFile, "UTF-8").toString();
        }
        catch (IOException e2) {
            DocumentIndexCreator._logger.severe("Unable to read whitelisted content file: " + wlFile.getName());
            return;
        }
        catch (CharacterConversionException e3) {
            DocumentIndexCreator._logger.severe("Unable to read whitelisted content file: " + wlFile.getName() + " - expected " + "UTF-8" + " format.");
            return;
        }
        final CharSequence normalizedContent = this._contentNormalizer.normalize(wlText);
        final Set<HashWithPosition> fingerprint = this._hashSelector.getHashes(normalizedContent);
        if (fingerprint.size() == 0) {
            DocumentIndexCreator._logger.severe("Whitelisted content file is too short: " + wlFile.getName());
            return;
        }
        this._whitelisted = new HashSet<Integer>();
        for (final HashWithPosition hp : fingerprint) {
            this._whitelisted.add(new Integer(hp.getHash()));
        }
        DocumentIndexCreator._logger.info("Read " + this._whitelisted.size() + " kgrams from whitelisted content file " + wlFile.getName());
    }
    
    private long estimateRamUsage(final int numHashes, final int numDocs, final int numDocsWithSnippets) {
        return numHashes * this._settings.getIntSetting("ram_per_hash", 38) + numDocs * this._settings.getIntSetting("ram_per_doc", 150) + numDocsWithSnippets * (this._settings.getIntSetting("ram_per_snippet", 10) + this._snippetNumBytes * 2) * this._fingerpintSizeThresholdForSnippet;
    }
    
    @Override
    public boolean addCharDocument(final int docID, final String docName, final CharSequence normalizedContent) throws IndexerException, IOException {
        return this.putCharDocument(0, docID, docName, normalizedContent);
    }
    
    @Override
    public boolean replaceCharDocument(final int docID, final String docName, final CharSequence extractedContent, final byte[] byteContent) throws IndexerException, IOException {
        if (!this._incremental) {
            throw new IllegalStateException("Can not replace document in non-incremental indexing");
        }
        return this.putCharDocument(1, docID, docName, extractedContent, byteContent);
    }
    
    private boolean putCharDocument(final int entryType, final int docID, final String docName, final CharSequence extractedContent, final byte[] byteContent) throws IOException, IndexerException {
        boolean wasCracked = true;
        if (extractedContent.length() < this._min_normalized_size) {
            this.putBinaryDocument(entryType, docID, docName, byteContent);
            wasCracked = false;
        }
        else {
            wasCracked = this.putCharDocument(entryType, docID, docName, extractedContent);
            if (!wasCracked) {
                this.putBinaryDocument(entryType, docID, docName, byteContent);
            }
        }
        return wasCracked;
    }
    
    private boolean putCharDocument(final int entryType, final int docID, final String docName, final CharSequence normalizedContent) throws IOException {
        Set<HashWithPosition> fingerprint = null;
        final int docType = 0;
        fingerprint = this._hashSelector.getHashes(normalizedContent);
        if (fingerprint.size() == 0 && DocumentIndexCreator._logger.isLoggable(Level.FINE)) {
            DocumentIndexCreator._logger.fine("Document " + docName + " is too small to be fingerprinted");
        }
        if (this._incremental) {
            this._indexFileStream.writeInt(entryType);
        }
        this._indexFileStream.writeInt(docID);
        this._indexFileStream.writeInt(0);
        this._indexFileStream.writeInt(docType);
        this._indexFileStream.writeInt(1);
        this._indexFileStream.writeInt(normalizedContent.length());
        final byte[] digestContent = IndexUtil.bytesFromCharacters(normalizedContent);
        byte[] digest;
        if (this._useJavaMD5) {
            digest = BinaryMatchUtil.generateHash(digestContent, this._digest, this._maxBinMatchSize);
        }
        else {
            digest = BinaryMatchUtil.generateHash(digestContent, this._md5, this._maxBinMatchSize);
        }
        this._indexFileStream.write(digest);
        if (fingerprint.size() > 0) {
            this.removeWhitelisted(fingerprint);
            if (fingerprint.size() == 0) {
                DocumentIndexCreator._logger.warning("Document " + docName + " has only whitelisted content!!!");
            }
        }
        this._indexFileStream.writeInt(fingerprint.size());
        boolean createSnippet = false;
        if (fingerprint.size() > 0 && fingerprint.size() < this._fingerpintSizeThresholdForSnippet) {
            createSnippet = true;
        }
        this._indexFileStream.writeInt(createSnippet ? 1 : 0);
        for (final HashWithPosition hp : fingerprint) {
            this._indexFileStream.writeInt(hp.getHash());
            if (createSnippet) {
                this._indexFileStream.write(this.getKgramSnippet(normalizedContent, hp, this._k, this._snippetNumBytes));
            }
        }
        if (entryType == 0) {
            ++this._numDocs;
            this._numHashes += fingerprint.size();
            if (createSnippet) {
                ++this._numDocsWithSnippets;
            }
        }
        return true;
    }
    
    @Override
    public void addBinaryDocument(final int docID, final String docName, final byte[] content) throws IndexerException, IOException {
        this.putBinaryDocument(0, docID, docName, content);
    }
    
    @Override
    public void replaceBinaryDocument(final int docID, final String docName, final byte[] content) throws IndexerException, IOException {
        if (!this._incremental) {
            throw new IllegalStateException("Can not replace document in non-incremental indexing");
        }
        this.putBinaryDocument(1, docID, docName, content);
    }
    
    private void putBinaryDocument(final int entryType, final int docID, final String docName, final byte[] content) throws IOException {
        final int docType = 0;
        if (this._incremental) {
            this._indexFileStream.writeInt(entryType);
        }
        this._indexFileStream.writeInt(docID);
        this._indexFileStream.writeInt(content.length);
        this._indexFileStream.writeInt(docType);
        this._indexFileStream.writeInt(0);
        this._indexFileStream.writeInt(0);
        byte[] digest;
        if (this._useJavaMD5) {
            digest = BinaryMatchUtil.generateHash(content, this._digest, this._maxBinMatchSize);
        }
        else {
            digest = BinaryMatchUtil.generateHash(content, this._md5, this._maxBinMatchSize);
        }
        this._indexFileStream.write(digest);
        this._indexFileStream.writeInt(0);
        this._indexFileStream.writeInt(0);
        if (entryType == 0) {
            ++this._numDocs;
        }
    }
    
    private void removeWhitelisted(final Set<HashWithPosition> fingerprint) {
        if (this._whitelisted == null) {
            return;
        }
        final Iterator<HashWithPosition> it = fingerprint.iterator();
        while (it.hasNext()) {
            final HashWithPosition hp = it.next();
            if (this._whitelisted.contains(new Integer(hp.getHash()))) {
                it.remove();
            }
        }
    }
    
    private byte[] getKgramSnippet(final CharSequence text, final HashWithPosition hp, final int k, final int numBytes) {
        final byte[] snippet = new byte[numBytes * 2];
        for (int i = 0, j = numBytes * 2 - 1; i < numBytes; ++i, --j) {
            snippet[i] = (byte)text.charAt(hp.getPosition() + i);
            snippet[j] = (byte)text.charAt(hp.getPosition() + k - 1 - i);
        }
        return snippet;
    }
    
    @Override
    public void deleteDocument(final int docID) throws IOException {
        if (!this._incremental) {
            throw new IllegalStateException("Can not delete document in non-incremental indexing");
        }
        this._indexFileStream.writeInt(2);
        this._indexFileStream.writeInt(docID);
        --this._numDocs;
        this._numHashes -= this._averageHashesPerDocInPreviousVersion;
    }
    
    @Override
    public void finish() throws IOException {
        this._indexFileStream.close();
        this._indexFileStream = null;
        if (!this._incremental) {
            this.writeHeader(this._finalIndexFile, this._numDocs, this._numHashes, this._numDocsWithSnippets);
        }
        else {
            this.createNextVersion();
        }
        this._running = false;
    }
    
    private void createNextVersion() throws IOException {
        final DataInputStream prevVersionFileStream = new DataInputStream(new BufferedInputStream(new FileInputStream(this._previousIndexFile), 524288));
        final DataInputStream incrFileStream = new DataInputStream(new BufferedInputStream(new FileInputStream(this._intermediateIndexFile), 524288));
        final File nextVersionIndexFile = this._finalIndexFile;
        final DataOutputStream nextVersionFileStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nextVersionIndexFile), 524288));
        this.leaveSpaceForHeader(nextVersionFileStream);
        incrFileStream.readInt();
        if (prevVersionFileStream.readInt() != 62052) {
            throw new IOException("Magic number is not correct on previos version of index");
        }
        if (prevVersionFileStream.readInt() != this._k) {
            throw new IOException("Values of k do not match between incremental and full index");
        }
        if (prevVersionFileStream.readInt() != this._t) {
            throw new IOException("Values of t do not match between incremental and full index");
        }
        if (prevVersionFileStream.readInt() != this._snippetNumBytes) {
            throw new IOException("Values of snippetNumBytes do not match between incremental and full index");
        }
        if (prevVersionFileStream.readInt() != this._snippetCheckThreshold) {
            throw new IOException("Values of snippetCheckThreshold do not match between incremental and full index");
        }
        prevVersionFileStream.readInt();
        prevVersionFileStream.readInt();
        prevVersionFileStream.readInt();
        int nextNumDocs = 0;
        int nextNumHashes = 0;
        int nextNumDocsWithSnippets = 0;
        this.getNextIncrementalDocId(incrFileStream);
        this.getNextDocId(prevVersionFileStream);
        while (this._docId != Integer.MAX_VALUE || this._incrDocId != Integer.MAX_VALUE) {
            if (this._incrDocId < this._docId) {
                if (this._incrEntryType != 0) {
                    DocumentIndexCreator._logger.severe("Wrong entry type for docID " + this._incrDocId + " in incremental file (should be ADD). Will skip");
                    if (this._incrEntryType == 1) {
                        this.skipEntry(incrFileStream);
                    }
                }
                else {
                    nextNumHashes += this.copyEntry(incrFileStream, nextVersionFileStream, this._incrDocId);
                    ++nextNumDocs;
                    nextNumDocsWithSnippets += this._snippetPresent;
                }
                this.getNextIncrementalDocId(incrFileStream);
            }
            else if (this._incrDocId == this._docId) {
                if (this._incrEntryType == 0) {
                    DocumentIndexCreator._logger.severe("Wrong entry type for docID " + this._incrDocId + " in incremental file (should not be ADD). Will skip");
                    this.skipEntry(incrFileStream);
                    this.getNextIncrementalDocId(incrFileStream);
                }
                else {
                    if (this._incrEntryType == 1) {
                        nextNumHashes += this.copyEntry(incrFileStream, nextVersionFileStream, this._incrDocId);
                        ++nextNumDocs;
                        nextNumDocsWithSnippets += this._snippetPresent;
                    }
                    this.skipEntry(prevVersionFileStream);
                    this.getNextIncrementalDocId(incrFileStream);
                    this.getNextDocId(prevVersionFileStream);
                }
            }
            else {
                nextNumHashes += this.copyEntry(prevVersionFileStream, nextVersionFileStream, this._docId);
                ++nextNumDocs;
                nextNumDocsWithSnippets += this._snippetPresent;
                this.getNextDocId(prevVersionFileStream);
            }
        }
        prevVersionFileStream.close();
        incrFileStream.close();
        nextVersionFileStream.close();
        this.writeHeader(nextVersionIndexFile, nextNumDocs, nextNumHashes, nextNumDocsWithSnippets);
        this._numHashes = nextNumHashes;
        this._numDocs = nextNumDocs;
        this._numDocsWithSnippets = nextNumDocsWithSnippets;
        if (!this._retainAllFiles) {
            this._intermediateIndexFile.delete();
        }
    }
    
    private void getNextDocId(final DataInputStream in) throws IOException {
        if (in.available() > 0) {
            this._docId = in.readInt();
        }
        else {
            this._docId = Integer.MAX_VALUE;
        }
    }
    
    private void getNextIncrementalDocId(final DataInputStream in) throws IOException {
        if (in.available() > 0) {
            this._incrEntryType = in.readInt();
            this._incrDocId = in.readInt();
        }
        else {
            this._incrDocId = Integer.MAX_VALUE;
        }
    }
    
    private int copyEntry(final DataInputStream inStream, final DataOutputStream outStream, final int docId) throws IOException {
        outStream.writeInt(docId);
        final byte[] entryHeader = new byte[32];
        inStream.readFully(entryHeader);
        outStream.write(entryHeader);
        final int numHashes = inStream.readInt();
        outStream.writeInt(numHashes);
        outStream.writeInt(this._snippetPresent = inStream.readInt());
        final int hashesAndBytesSize = (this._snippetPresent == 0) ? (numHashes * 4) : (numHashes * (4 + this._snippetNumBytes * 2));
        final byte[] hashesAndBytes = new byte[hashesAndBytesSize];
        inStream.readFully(hashesAndBytes);
        outStream.write(hashesAndBytes);
        return numHashes;
    }
    
    private void skipEntry(final DataInputStream inStream) throws IOException {
        int skipped;
        if ((skipped = inStream.skipBytes(32)) != 32) {
            throw new IOException("Unable to skip 1616 bytes. Only " + skipped + " bytes are skipped." + " Current document ID is " + this._docId + " and increment document id is " + this._incrDocId);
        }
        final int numHashes = inStream.readInt();
        final int bytesAreRetained = inStream.readInt();
        final int hashesAndBytesSize = (bytesAreRetained == 0) ? (numHashes * 4) : (numHashes * (4 + this._snippetNumBytes * 2));
        if ((skipped = inStream.skipBytes(hashesAndBytesSize)) != hashesAndBytesSize) {
            throw new IOException("Unable to skip " + hashesAndBytesSize + " bytes. Only " + skipped + " bytes are skipped." + " Current document ID is " + this._docId + " and increment document id is " + this._incrDocId);
        }
    }
    
    @Override
    public void cancel() throws IOException {
        if (this._indexFileStream != null) {
            this._indexFileStream.close();
            this._intermediateIndexFile.delete();
        }
        this._running = false;
    }
    
    @Override
    public int getNumDocuments() {
        return this._numDocs;
    }
    
    int getNumDocsWithSnippets() {
        return this._numDocsWithSnippets;
    }
    
    int getNumHashes() {
        return this._numHashes;
    }
    
    @Override
    public long getIndexSize() {
        return this.estimateRamUsage(this._numHashes, this._numDocs, this._numDocsWithSnippets);
    }
    
    @Override
    public boolean isRunning() {
        return this._running;
    }
    
    private void leaveSpaceForHeader(final DataOutputStream index) throws IOException {
        index.write(new byte[32]);
    }
    
    private void writeHeader(final File indexFile, final int numDocs, final int numHashes, final int numDocsWithRetainedBytes) throws IOException {
        final RandomAccessFile index = new RandomAccessFile(indexFile, "rw");
        index.writeInt(62052);
        index.writeInt(this._k);
        index.writeInt(this._t);
        index.writeInt(this._snippetNumBytes);
        index.writeInt(this._snippetCheckThreshold);
        index.writeInt(numDocs);
        index.writeInt(numHashes);
        index.writeInt(numDocsWithRetainedBytes);
        index.close();
    }
    
    static {
        _logger = Logger.getLogger(DocumentIndexCreator.class.getName());
    }
}
