// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import java.util.Iterator;
import java.util.ArrayList;
import com.vontu.profileindex.database.RowMatcher;
import com.vontu.detection.condition.PredicateOperator;
import java.util.Collection;
import com.vontu.profileindex.database.PredicateConverter;

class OperatorCollectionConverter
{
    private PredicateConverter _predicateConverter;
    
    void setPredicateConverter(final PredicateConverter predicateConveter) {
        this._predicateConverter = predicateConveter;
    }
    
    Collection<RowMatcher> createMatchers(final Collection<PredicateOperator> operators) {
        final Collection<RowMatcher> matchers = new ArrayList<RowMatcher>();
        for (final PredicateOperator operator : operators) {
            matchers.add(this._predicateConverter.createMatcher(operator));
        }
        return matchers;
    }
}
