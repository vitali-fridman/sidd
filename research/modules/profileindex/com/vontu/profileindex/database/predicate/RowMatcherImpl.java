// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.RowPredicateMatcher;
import com.vontu.profileindex.database.MatchInfo;
import com.vontu.profileindex.database.RowMatcher;

class RowMatcherImpl implements RowMatcher
{
    private final int[] _columns;
    private final String _values;
    
    RowMatcherImpl(final int[] columns, final String operands) {
        this._columns = columns.clone();
        this._values = operands;
    }
    
    int[] getColumns() {
        return this._columns.clone();
    }
    
    String getValues() {
        return this._values;
    }
    
    @Override
    public boolean[] matchRows(final MatchInfo matchInfo, final RowPredicateMatcher predicateMatcher) throws IndexException {
        return predicateMatcher.matchRows(matchInfo, this._columns, this._values);
    }
}
