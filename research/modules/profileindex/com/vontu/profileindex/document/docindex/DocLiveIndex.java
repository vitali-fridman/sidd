// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import cern.colt.function.IntObjectProcedure;
import cern.colt.list.IntArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.AbstractIntObjectMap;
import java.util.logging.Logger;

@Deprecated
public class DocLiveIndex implements DocumentIndex
{
    private static final Logger logger;
    private static final int BUFFER_SIZE = 524288;
    private AbstractIntObjectMap _kgramIndex;
    private AbstractIntIntMap _docIndex;
    private AbstractIntObjectMap _snippetIndex;
    private Map<DocDigest, Integer> _digestIndex;
    private int _k;
    private int _t;
    private int _snippetSize;
    private int _snippetCheckThreshold;
    private static final double KGRAM_REPEAT_FACTOR = 2.0;
    private static final double MAP_MIN_LOAD_FACTOR = 0.4;
    private static final double MAP_MAX_LOAD_FACTOR = 0.7;
    private static final int[] NO_HITS;
    
    public DocLiveIndex(final InputStream inputStream) throws IOException, IndexException {
        final DataInputStream indexStream = new DataInputStream(new BufferedInputStream(inputStream, 524288));
        try {
            final int magic = indexStream.readInt();
            if (magic != 62052) {
                throw new IOException("Magic number mismatch in document index file.");
            }
            this._k = indexStream.readInt();
            this._t = indexStream.readInt();
            this._snippetSize = indexStream.readInt() * 2;
            this._snippetCheckThreshold = indexStream.readInt();
            final int numDocs = indexStream.readInt();
            final int numHashes = indexStream.readInt();
            final int numDocsWithSnippets = indexStream.readInt();
            final int estimatedNumberOfUniqueHashes = (int)(numHashes / 2.0) + 1;
            this._kgramIndex = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(estimatedNumberOfUniqueHashes / 0.7) + 10, 0.4, 0.7);
            this._docIndex = (AbstractIntIntMap)new OpenIntIntHashMap((int)(numDocs / 0.7) + 10, 0.4, 0.7);
            this._snippetIndex = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(numDocsWithSnippets / 0.7) + 10, 0.4, 0.7);
            this._digestIndex = new HashMap<DocDigest, Integer>((int)(numDocs / 0.7) + 10, 0.7f);
            int docId = -1;
            int originalLength = -1;
            int docType = -1;
            int textRetrieved = -1;
            int normalizedLength = -1;
            int fingerprintSize = -1;
            int snippetPresent = -1;
            for (int i = 0; i < numDocs; ++i) {
                docId = indexStream.readInt();
                originalLength = indexStream.readInt();
                docType = indexStream.readInt();
                textRetrieved = indexStream.readInt();
                normalizedLength = indexStream.readInt();
                final byte[] md5 = new byte[16];
                indexStream.readFully(md5);
                final Object prevVal = this._digestIndex.put(new DocDigest(md5), new Integer(docId));
                if (prevVal != null && DocLiveIndex.logger.isLoggable(Level.FINE)) {
                    DocLiveIndex.logger.fine("MD5 collision between docId " + docId + " and docId " + (int)prevVal + " in document index.");
                }
                fingerprintSize = indexStream.readInt();
                snippetPresent = indexStream.readInt();
                AbstractIntObjectMap documentSnippets = null;
                if (fingerprintSize > 0 && snippetPresent == 1) {
                    documentSnippets = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(fingerprintSize / 0.7) + 10, 0.4, 0.7);
                }
                if (fingerprintSize > 0) {
                    this._docIndex.put(docId, fingerprintSize);
                }
                for (int j = 0; j < fingerprintSize; ++j) {
                    final int hash = indexStream.readInt();
                    if (snippetPresent == 1) {
                        final byte[] snippet = new byte[this._snippetSize];
                        indexStream.readFully(snippet);
                        documentSnippets.put(hash, (Object)snippet);
                    }
                    IntArrayList docs = (IntArrayList)this._kgramIndex.get(hash);
                    if (docs == null) {
                        docs = new IntArrayList(1);
                        this._kgramIndex.put(hash, (Object)docs);
                    }
                    docs.add(docId);
                }
                if (documentSnippets != null) {
                    this._snippetIndex.put(docId, (Object)documentSnippets);
                }
            }
            this._kgramIndex.forEachPair((IntObjectProcedure)new IntObjectProcedure() {
                public boolean apply(final int hash, final Object docs) {
                    ((IntArrayList)docs).trimToSize();
                    return true;
                }
            });
        }
        catch (OutOfMemoryError e) {
            throw new IndexException(IndexError.INDEX_NOT_ENOUGH_RAM_TO_LOAD, e);
        }
        finally {
            indexStream.close();
        }
    }
    
    @Override
    public int getSnippetCheckThreshold() {
        return this._snippetCheckThreshold;
    }
    
    @Override
    public int getK() {
        return this._k;
    }
    
    @Override
    public int getT() {
        return this._t;
    }
    
    @Override
    public int[] lookupHash(final int hash) {
        final IntArrayList docs = (IntArrayList)this._kgramIndex.get(hash);
        if (docs == null) {
            return DocLiveIndex.NO_HITS;
        }
        return docs.elements();
    }
    
    @Override
    public byte[] getSnippet(final int docId, final int hash) {
        final AbstractIntObjectMap documentSnipets = (AbstractIntObjectMap)this._snippetIndex.get(docId);
        if (documentSnipets == null) {
            DocLiveIndex.logger.severe("No snippets in the Index for docID=" + docId);
            return null;
        }
        final byte[] snippet = (byte[])documentSnipets.get(hash);
        if (snippet == null) {
            DocLiveIndex.logger.severe("No snippet for requested hash in the Index for docId=" + docId + " for hash=" + hash);
            return null;
        }
        return snippet;
    }
    
    @Override
    public int lookupFingerprintCount(final int docId) {
        return this._docIndex.get(docId);
    }
    
    @Override
    public int lookupDocumentId(final byte[] md5) {
        final Integer docId = this._digestIndex.get(new DocDigest(md5));
        if (docId == null) {
            return -1;
        }
        return docId;
    }
    
    static {
        logger = Logger.getLogger(DocLiveIndex.class.getName());
        NO_HITS = new int[0];
    }
}
