// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import java.util.LinkedList;
import java.util.List;

final class SearchTermHitsForRow
{
    private final int _row;
    private final List _searchTerms;
    
    SearchTermHitsForRow(final int row, final List cells) {
        this._row = row;
        this._searchTerms = new LinkedList();
        for (int i = 0; i < cells.size(); ++i) {
            this._searchTerms.add(cells.get(i).searchTerm);
        }
    }
    
    int getRow() {
        return this._row;
    }
    
    List getSearchTerms() {
        return this._searchTerms;
    }
}
