// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

import cern.colt.map.OpenIntObjectHashMap;
import java.util.ArrayList;
import cern.colt.map.AbstractIntObjectMap;

class DocumentIndexEntry
{
    private final int documentId;
    private final byte[] md5;
    private final int fingerprintCount;
    private final AbstractIntObjectMap documentSnippets;
    private final ArrayList<Integer> fingerprints;
    
    public DocumentIndexEntry(final int documentId, final byte[] md5, final int fingerprintCount) {
        this.documentId = documentId;
        this.md5 = md5;
        this.fingerprintCount = fingerprintCount;
        this.documentSnippets = (AbstractIntObjectMap)new OpenIntObjectHashMap(0);
        this.fingerprints = new ArrayList<Integer>(fingerprintCount);
    }
    
    public DocumentIndexEntry(final int documentId, final byte[] md5, final int fingerprintCount, final int fingerprintSize) {
        this.documentId = documentId;
        this.md5 = md5;
        this.fingerprintCount = fingerprintCount;
        this.fingerprints = new ArrayList<Integer>(fingerprintCount);
        this.documentSnippets = (AbstractIntObjectMap)new OpenIntObjectHashMap((int)(fingerprintSize / 0.7) + 10, 0.4, 0.7);
    }
    
    public void addSnippet(final int hash, final byte[] snippet) {
        this.documentSnippets.put(hash, (Object)snippet);
    }
    
    public void addFingerprint(final int hash) {
        this.fingerprints.add(hash);
    }
    
    public int getDocumentId() {
        return this.documentId;
    }
    
    public byte[] getMd5() {
        return this.md5;
    }
    
    public int getFingerprintCount() {
        return this.fingerprintCount;
    }
    
    public AbstractIntObjectMap getDocumentSnippets() {
        return this.documentSnippets;
    }
    
    public ArrayList<Integer> getFingerprints() {
        return this.fingerprints;
    }
}
