// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;

public class RowPredicateMatcher
{
    private final SearchTermFactory _searchTermFactory;
    private final DatabaseIndex _index;
    
    public RowPredicateMatcher(final SearchTermFactory searchTermFactory, final DatabaseIndex index) {
        this._searchTermFactory = searchTermFactory;
        this._index = index;
    }
    
    public boolean[] matchRows(final MatchInfo matchInfo, final int[] columns, final String valuesCsv) throws IndexException {
        final SearchTerm[] operandSearchTerms = this._searchTermFactory.createOperandSearchTerms(valuesCsv);
        return matchInfo.matchRowsWithPredicates(columns, operandSearchTerms, this._index);
    }
}
