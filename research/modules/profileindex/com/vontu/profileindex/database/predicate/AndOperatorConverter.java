// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import java.util.Collection;
import java.util.ArrayList;
import com.vontu.detection.condition.BooleanOperator;
import com.vontu.profileindex.database.RowMatcher;
import com.vontu.detection.condition.PredicateOperator;
import com.vontu.profileindex.database.PredicateConverter;

class AndOperatorConverter implements PredicateConverter
{
    private final MultiColumnPredicateConverter _leafConverter;
    private final OperatorCollectionConverter _operatorConverter;
    
    AndOperatorConverter(final MultiColumnPredicateConverter leafConverter, final OperatorCollectionConverter operatorConverter) {
        this._leafConverter = leafConverter;
        this._operatorConverter = operatorConverter;
    }
    
    @Override
    public RowMatcher createMatcher(final PredicateOperator operator) {
        assert operator.operator() == BooleanOperator.AND;
        if (operator.nestedOperators().isEmpty() && operator.predicates().isEmpty()) {
            throw new IllegalArgumentException("Invalid AND operator: neither predicates nor nested operands are set");
        }
        final Collection<RowMatcher> matchers = new ArrayList<RowMatcher>();
        matchers.addAll(this._leafConverter.createMatchers(operator.predicates()));
        matchers.addAll(this._operatorConverter.createMatchers(operator.nestedOperators()));
        return new AndOperator(matchers);
    }
}
