// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import java.io.Serializable;
import com.vontu.detection.output.DocumentMatch;

public class DocumentMatchImpl implements DocumentMatch, Serializable
{
    private static final long serialVersionUID = 8325486L;
    private static final int EXACT_CONTAINMENT = 100;
    private boolean _isExact;
    private int _docId;
    private int _containment;
    
    DocumentMatchImpl(final int docId, final int containment) {
        this(docId, containment, false);
    }
    
    DocumentMatchImpl(final int docId) {
        this(docId, 100, true);
    }
    
    DocumentMatchImpl(final int docId, final int containment, final boolean isExact) {
        this._docId = docId;
        this._containment = containment;
        this._isExact = isExact;
    }
    
    public int getContainment() {
        return this._containment;
    }
    
    public int getDocId() {
        return this._docId;
    }
    
    public boolean isExact() {
        return this._isExact;
    }
}
