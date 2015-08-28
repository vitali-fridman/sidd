// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import java.util.Iterator;
import com.vontu.profileindex.database.SearchTerm;
import java.util.List;
import com.vontu.profileindex.database.DatabaseRowMatch;
import java.io.Serializable;

public final class DbRowHit implements Serializable, DatabaseRowMatch
{
    private static final long serialVersionUID = 1L;
    private static final int REMOVED = -1;
    private int _rowNumber;
    private final int[] _tokenIds;
    
    public DbRowHit(final int row, final List searchTerms) {
        this._rowNumber = row;
        this._tokenIds = new int[searchTerms.size()];
        int i = 0;
        final Iterator it = searchTerms.iterator();
        while (it.hasNext()) {
            this._tokenIds[i] = it.next().getIndexInContent();
            ++i;
        }
    }
    
    @Override
    public int getRow() {
        return this._rowNumber;
    }
    
    public void remove() {
        this._rowNumber = -1;
    }
    
    boolean isRemoved() {
        return this._rowNumber == -1;
    }
    
    @Override
    public int[] getTokenIds() {
        return this._tokenIds;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("Matched ").append(this._tokenIds.length);
        stringBuilder.append(" tokens from row number ").append(this._rowNumber).append('.');
        return stringBuilder.toString();
    }
}
