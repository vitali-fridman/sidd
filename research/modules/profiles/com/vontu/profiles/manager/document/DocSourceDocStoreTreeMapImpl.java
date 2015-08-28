// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import java.util.Iterator;
import com.vontu.model.data.DocSourceDoc;
import java.util.Collection;
import java.util.TreeMap;

public class DocSourceDocStoreTreeMapImpl extends AbstractDocSourceDocStoreImpl
{
    private final TreeMap<Integer, DocSourceDocWrapper> _sortedDocuments;
    
    public DocSourceDocStoreTreeMapImpl(final Collection<DocSourceDoc> docSourceDocs) {
        this._sortedDocuments = new TreeMap<Integer, DocSourceDocWrapper>();
        for (final DocSourceDoc docSourceDoc : docSourceDocs) {
            this.addDocSourceDocToCollection(docSourceDoc, 0);
        }
    }
    
    @Override
    public Collection<DocSourceDocWrapper> getSortedDocuments() {
        return this._sortedDocuments.values();
    }
    
    @Override
    protected DocSourceDocWrapper addDocSourceDocToCollection(final DocSourceDoc docSourceDoc, final int status) {
        final DocSourceDocWrapper docSourceDocWrapper = new DocSourceDocWrapper(docSourceDoc, status);
        this._documentSearch.put(docSourceDoc.getName(), docSourceDocWrapper);
        this._sortedDocuments.put(new Integer(docSourceDoc.getDocSourceDocID()), docSourceDocWrapper);
        return docSourceDocWrapper;
    }
}
