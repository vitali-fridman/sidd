// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import java.util.ArrayList;
import com.vontu.detection.output.DocumentMatch;
import java.util.List;
import java.io.Serializable;
import com.vontu.detection.output.DocumentConditionViolation;

public class DocumentConditionViolationImpl implements DocumentConditionViolation, Serializable
{
    private static final long serialVersionUID = 823847385L;
    private final List<DocumentMatch> _docMatches;
    
    public DocumentConditionViolationImpl() {
        this._docMatches = new ArrayList<DocumentMatch>();
    }
    
    public DocumentMatch[] matches() {
        final DocumentMatch[] docMatches = new DocumentMatch[this._docMatches.size()];
        return this._docMatches.toArray(docMatches);
    }
    
    public boolean isEmpty() {
        return this._docMatches.isEmpty();
    }
    
    void addMatch(final DocumentMatch match) {
        this._docMatches.add(match);
    }
}
