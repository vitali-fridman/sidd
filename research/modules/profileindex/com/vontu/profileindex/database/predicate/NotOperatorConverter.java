// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import com.vontu.detection.condition.MultiColumnPredicate;
import java.util.Collection;
import com.vontu.detection.condition.BooleanOperator;
import com.vontu.profileindex.database.RowMatcher;
import com.vontu.detection.condition.PredicateOperator;
import com.vontu.profileindex.database.PredicateConverter;

class NotOperatorConverter implements PredicateConverter
{
    private final MultiColumnPredicateConverter _leafConverter;
    private final OperatorCollectionConverter _operatorConverter;
    
    NotOperatorConverter(final MultiColumnPredicateConverter leafConverter, final OperatorCollectionConverter operatorConverter) {
        this._leafConverter = leafConverter;
        this._operatorConverter = operatorConverter;
    }
    
    @Override
    public RowMatcher createMatcher(final PredicateOperator operator) {
        assert operator.operator() == BooleanOperator.NOT;
        final Collection<MultiColumnPredicate> predicates = operator.predicates();
        final Collection<PredicateOperator> nestedOperators = operator.nestedOperators();
        if (predicates.size() + nestedOperators.size() > 1) {
            throw new IllegalArgumentException("Invalid NOT operator: expected exactly one predicate or nested operator");
        }
        if (!predicates.isEmpty()) {
            return new NotOperator(this._leafConverter.createMatchers(predicates).iterator().next());
        }
        if (!nestedOperators.isEmpty()) {
            return new NotOperator(this._operatorConverter.createMatchers(nestedOperators).iterator().next());
        }
        throw new IllegalArgumentException("Invalid NOT oerator: neither predicate nor nested operator is set");
    }
}
