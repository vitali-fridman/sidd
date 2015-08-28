// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import com.vontu.profileindex.IndexException;
import com.vontu.util.BooleanArray;
import com.vontu.profileindex.database.RowPredicateMatcher;
import com.vontu.profileindex.database.MatchInfo;
import com.vontu.profileindex.database.RowMatcher;

class NotOperator implements RowMatcher
{
    private final RowMatcher _operand;
    
    NotOperator(final RowMatcher operand) {
        this._operand = operand;
    }
    
    RowMatcher getOperand() {
        return this._operand;
    }
    
    @Override
    public boolean[] matchRows(final MatchInfo matchInfo, final RowPredicateMatcher predicateMatcher) throws IndexException {
        return BooleanArray.not(this._operand.matchRows(matchInfo, predicateMatcher));
    }
}
