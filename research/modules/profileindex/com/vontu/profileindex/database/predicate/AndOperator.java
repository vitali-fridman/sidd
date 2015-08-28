// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import com.vontu.profileindex.IndexException;
import java.util.Iterator;
import com.vontu.util.BooleanArray;
import com.vontu.profileindex.database.RowPredicateMatcher;
import com.vontu.profileindex.database.MatchInfo;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import com.vontu.profileindex.database.RowMatcher;

class AndOperator implements RowMatcher
{
    private final Collection<RowMatcher> _operands;
    private static final boolean[] NOT_INITIALIZED;
    
    AndOperator(final Collection<RowMatcher> operands) {
        this._operands = new ArrayList<RowMatcher>(operands);
    }
    
    Collection<RowMatcher> getOperands() {
        return Collections.unmodifiableCollection((Collection<? extends RowMatcher>)this._operands);
    }
    
    @Override
    public boolean[] matchRows(final MatchInfo matchInfo, final RowPredicateMatcher predicateMatcher) throws IndexException {
        boolean[] matches = AndOperator.NOT_INITIALIZED;
        for (final RowMatcher operand : this._operands) {
            final boolean[] nextMatches = operand.matchRows(matchInfo, predicateMatcher);
            matches = ((matches == AndOperator.NOT_INITIALIZED) ? nextMatches : BooleanArray.and(matches, nextMatches));
        }
        return matches;
    }
    
    static {
        NOT_INITIALIZED = new boolean[0];
    }
}
