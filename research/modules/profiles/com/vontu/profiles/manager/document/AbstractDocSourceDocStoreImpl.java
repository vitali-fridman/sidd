// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.directorycrawler.VontuFileException;
import com.vontu.directorycrawler.VontuFile;
import com.vontu.model.data.DocSourceDoc;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDocSourceDocStoreImpl implements IDocSourceDocStore
{
    protected Map<String, DocSourceDocWrapper> _documentSearch;
    
    protected AbstractDocSourceDocStoreImpl() {
        this._documentSearch = new HashMap<String, DocSourceDocWrapper>();
    }
    
    @Override
    public void add(final DocSourceDoc docSourceDoc, final VontuFile file) {
        final DocSourceDocWrapper docWrapper = this.addDocSourceDocToCollection(docSourceDoc, 3);
        docWrapper.setVontuFile(file);
    }
    
    @Override
    public DocSourceDocWrapper update(final VontuFile file) throws VontuFileException, InterruptedException {
        final DocSourceDocWrapper existingDocWrapper = this._documentSearch.get(file.getCanonicalPath());
        if (existingDocWrapper == null) {
            throw new IllegalArgumentException("The entry does not exist. Call contains() method first to check");
        }
        existingDocWrapper.setVontuFile(file);
        if (existingDocWrapper.getDocSourceDoc().getActiveInDetection() == 0) {
            existingDocWrapper.setStatus(3);
        }
        else if (DocSourceDocWrapper.isDocumentModified(existingDocWrapper)) {
            existingDocWrapper.setStatus(2);
        }
        else {
            existingDocWrapper.setStatus(1);
        }
        return existingDocWrapper;
    }
    
    @Override
    public boolean contains(final VontuFile file) throws VontuFileException, InterruptedException {
        return !this._documentSearch.isEmpty() && this._documentSearch.containsKey(file.getCanonicalPath());
    }
    
    protected abstract DocSourceDocWrapper addDocSourceDocToCollection(final DocSourceDoc p0, final int p1);
}
