// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

class DocumentIndexHeader
{
    private final int magicNumber;
    private final int k;
    private final int t;
    private final int snippetSize;
    private final int snippetCheckThreshold;
    private final int documentCount;
    private final int fingerprintTotal;
    private final int documentWithSnippetCount;
    
    public DocumentIndexHeader(final int magicNumber, final int k, final int t, final int snippetSize, final int snippetCheckThreshold, final int documentCount, final int fingerprintTotal, final int documentWithSnippetCount) {
        this.magicNumber = magicNumber;
        this.k = k;
        this.t = t;
        this.snippetSize = snippetSize;
        this.snippetCheckThreshold = snippetCheckThreshold;
        this.documentCount = documentCount;
        this.fingerprintTotal = fingerprintTotal;
        this.documentWithSnippetCount = documentWithSnippetCount;
    }
    
    public int getMagicNumber() {
        return this.magicNumber;
    }
    
    public int getK() {
        return this.k;
    }
    
    public int getT() {
        return this.t;
    }
    
    public int getDocumentCount() {
        return this.documentCount;
    }
    
    public int getDocumentWithSnippetCount() {
        return this.documentWithSnippetCount;
    }
    
    public int getFingerprintTotal() {
        return this.fingerprintTotal;
    }
    
    public int getSnippetCheckThreshold() {
        return this.snippetCheckThreshold;
    }
    
    public int getSnippetSize() {
        return this.snippetSize;
    }
}
