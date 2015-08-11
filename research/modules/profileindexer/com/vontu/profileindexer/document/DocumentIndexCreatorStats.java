// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

public class DocumentIndexCreatorStats
{
    DocumentIndexCreator _docIndexCreator;
    
    public DocumentIndexCreatorStats(final DocumentIndexCreator docIndexCreator) {
        this._docIndexCreator = docIndexCreator;
    }
    
    public int getNumDocuments() {
        return this._docIndexCreator.getNumDocuments();
    }
    
    public int getNumDocsWithSnippets() {
        return this._docIndexCreator.getNumDocsWithSnippets();
    }
    
    public int getNumHashes() {
        return this._docIndexCreator.getNumHashes();
    }
    
    public long getIndexSize() {
        return this._docIndexCreator.getIndexSize();
    }
}
