// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

import cern.colt.function.IntObjectProcedure;
import java.util.Iterator;
import cern.colt.list.IntArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import com.vontu.profileindex.document.docindex.DocDigest;
import java.util.Map;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.AbstractIntObjectMap;
import java.util.logging.Logger;
import com.vontu.profileindex.document.docindex.DocumentIndex;

class LiveDocumentIndex implements DocumentIndex
{
    private Logger logger;
    public static final double KGRAM_REPEAT_FACTOR = 2.0;
    public static final double MAP_MIN_LOAD_FACTOR = 0.4;
    public static final double MAP_MAX_LOAD_FACTOR = 0.7;
    private final DocumentIndexHeader header;
    private final AbstractIntObjectMap kgramIndex;
    private final AbstractIntIntMap docIndex;
    private final AbstractIntObjectMap snippetIndex;
    private final Map<DocDigest, Integer> digestIndex;
    public static final int[] NO_HITS;
    
    public LiveDocumentIndex(final DocumentIndexHeader header) {
        this.logger = Logger.getLogger(LiveDocumentIndex.class.getName());
        this.header = header;
        final int estimatedNumberOfUniqueHashes = (int)(header.getFingerprintTotal() / 2.0) + 1;
        this.kgramIndex = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(estimatedNumberOfUniqueHashes / 0.7) + 10, 0.4, 0.7);
        this.docIndex = (AbstractIntIntMap)new OpenIntIntHashMap((int)(header.getDocumentCount() / 0.7) + 10, 0.4, 0.7);
        this.snippetIndex = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(header.getDocumentWithSnippetCount() / 0.7) + 10, 0.4, 0.7);
        this.digestIndex = new HashMap<DocDigest, Integer>((int)(header.getDocumentCount() / 0.7) + 10, 0.7f);
    }
    
    DocumentIndexHeader getHeader() {
        return this.header;
    }
    
    @Override
    public int getK() {
        return this.header.getK();
    }
    
    @Override
    public int getT() {
        return this.header.getT();
    }
    
    @Override
    public int getSnippetCheckThreshold() {
        return this.header.getSnippetCheckThreshold();
    }
    
    public void add(final DocumentIndexEntry entry) {
        final Integer existingDocumentId = this.digestIndex.put(new DocDigest(entry.getMd5()), new Integer(entry.getDocumentId()));
        if (existingDocumentId != null && this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("MD5 collision between docId [" + entry.getDocumentId() + "] and docId [" + existingDocumentId + "] in document index");
        }
        if (entry.getFingerprintCount() > 0) {
            this.docIndex.put(entry.getDocumentId(), entry.getFingerprintCount());
        }
        final AbstractIntObjectMap documentSnippets = entry.getDocumentSnippets();
        if (!documentSnippets.isEmpty()) {
            this.snippetIndex.put(entry.getDocumentId(), (Object)documentSnippets);
        }
        for (final Integer fingerprint : entry.getFingerprints()) {
            IntArrayList fingerprintDocuments = (IntArrayList)this.kgramIndex.get((int)fingerprint);
            if (fingerprintDocuments == null) {
                fingerprintDocuments = new IntArrayList(1);
                this.kgramIndex.put((int)fingerprint, (Object)fingerprintDocuments);
            }
            fingerprintDocuments.add(entry.getDocumentId());
        }
    }
    
    public void compact() {
        this.kgramIndex.forEachPair((IntObjectProcedure)new IntObjectProcedure() {
            public boolean apply(final int hash, final Object docs) {
                ((IntArrayList)docs).trimToSize();
                return true;
            }
        });
    }
    
    @Override
    public int[] lookupHash(final int hash) {
        final IntArrayList docs = (IntArrayList)this.kgramIndex.get(hash);
        if (docs == null) {
            return LiveDocumentIndex.NO_HITS;
        }
        return docs.elements();
    }
    
    @Override
    public byte[] getSnippet(final int documentId, final int hash) {
        final AbstractIntObjectMap documentSnipets = (AbstractIntObjectMap)this.snippetIndex.get(documentId);
        if (documentSnipets == null) {
            this.logger.severe("No snippets in the Index for docID=" + documentId);
            return null;
        }
        final byte[] snippet = (byte[])documentSnipets.get(hash);
        if (snippet == null) {
            this.logger.severe("No snippet for requested hash in the Index for docId=" + documentId + " for hash=" + hash);
            return null;
        }
        return snippet;
    }
    
    @Override
    public int lookupFingerprintCount(final int documentId) {
        return this.docIndex.get(documentId);
    }
    
    @Override
    public int lookupDocumentId(final byte[] md5) {
        final Integer documentId = this.digestIndex.get(new DocDigest(md5));
        if (documentId == null) {
            return -1;
        }
        return documentId;
    }
    
    AbstractIntIntMap getDocIndex() {
        return this.docIndex;
    }
    
    Map<DocDigest, Integer> getDigestIndex() {
        return this.digestIndex;
    }
    
    AbstractIntObjectMap getKgramIndex() {
        return this.kgramIndex;
    }
    
    AbstractIntObjectMap getSnippetIndex() {
        return this.snippetIndex;
    }
    
    static {
        NO_HITS = new int[0];
    }
}
