// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.Collections;
import com.vontu.detection.condition.MultiColumnPredicate;
import java.util.Collection;
import com.vontu.detection.condition.BooleanOperator;
import com.vontu.detection.condition.DatabaseMatchPredicate;
import com.vontu.detection.condition.PredicateOperator;
import com.vontu.detection.condition.DatabaseMatchCondition;
import com.vontu.detection.condition.FilteringDatabaseMatchCondition;

class DatabaseMatchConditionAdapter implements FilteringDatabaseMatchCondition
{
    private final DatabaseMatchCondition _adaptee;
    private final PredicateOperator _filterExpression;
    
    DatabaseMatchConditionAdapter(final DatabaseMatchCondition adaptee) {
        this._adaptee = adaptee;
        final DatabaseMatchPredicate matchPredicate = adaptee.predicate();
        this._filterExpression = ((matchPredicate == null) ? null : adaptPredicate(matchPredicate));
    }
    
    private static DatabaseMatchPredicateAdapter adaptPredicate(final DatabaseMatchPredicate matchPredicate) {
        if (matchPredicate.operands() == null || matchPredicate.operands().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid predicate: operands are missing");
        }
        return new DatabaseMatchPredicateAdapter(matchPredicate);
    }
    
    @Override
    public int[] columns() {
        return this._adaptee.columns();
    }
    
    @Override
    public int columnThreshold() {
        return this._adaptee.columnThreshold();
    }
    
    @Override
    public int[][] excludedTuples() {
        return this._adaptee.excludedTuples();
    }
    
    @Override
    public int minMatchCount() {
        return this._adaptee.minMatchCount();
    }
    
    @Override
    public String profileId() {
        return this._adaptee.profileId();
    }
    
    @Override
    public String conditionId() {
        return this._adaptee.conditionId();
    }
    
    @Override
    public String name() {
        return this._adaptee.name();
    }
    
    @Override
    public PredicateOperator filterExpression() {
        return this._filterExpression;
    }
    
    private static class DatabaseMatchPredicateAdapter implements PredicateOperator
    {
        private final DatabaseMatchPredicate _matchPredicate;
        
        DatabaseMatchPredicateAdapter(final DatabaseMatchPredicate matchPredicate) {
            this._matchPredicate = matchPredicate;
        }
        
        @Override
        public BooleanOperator operator() {
            return BooleanOperator.AND;
        }
        
        @Override
        public Collection<MultiColumnPredicate> predicates() {
            return (Collection<MultiColumnPredicate>)Collections.singleton(new SingleColumnPredicateAdapter(this._matchPredicate));
        }
        
        @Override
        public Collection<PredicateOperator> nestedOperators() {
            return (Collection<PredicateOperator>)Collections.emptyList();
        }
    }
    
    private static class SingleColumnPredicateAdapter implements MultiColumnPredicate
    {
        private DatabaseMatchPredicate _matchPredicate;
        
        SingleColumnPredicateAdapter(final DatabaseMatchPredicate matchPredicate) {
            this._matchPredicate = matchPredicate;
        }
        
        @Override
        public int[] columns() {
            return new int[] { this._matchPredicate.column() };
        }
        
        @Override
        public String valuesCsv() {
            return this._matchPredicate.operands();
        }
    }
}
