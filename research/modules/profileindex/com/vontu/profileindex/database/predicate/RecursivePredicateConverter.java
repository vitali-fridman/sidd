// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import com.vontu.detection.condition.BooleanOperator;
import com.vontu.profileindex.database.RowMatcher;
import com.vontu.detection.condition.PredicateOperator;
import com.vontu.profileindex.database.PredicateConverter;

public class RecursivePredicateConverter implements PredicateConverter
{
    private final AndOperatorConverter _andConvereter;
    private final NotOperatorConverter _notConverter;
    private final OrOperatorConverter _orConverter;
    
    public RecursivePredicateConverter() {
        this(new OperatorCollectionConverter(), new MultiColumnPredicateConverter());
    }
    
    RecursivePredicateConverter(final OperatorCollectionConverter collectionConverter, final MultiColumnPredicateConverter leafConverter) {
        this(new AndOperatorConverter(leafConverter, collectionConverter), new NotOperatorConverter(leafConverter, collectionConverter), new OrOperatorConverter(leafConverter, collectionConverter), collectionConverter);
    }
    
    RecursivePredicateConverter(final AndOperatorConverter andConverter, final NotOperatorConverter notConverter, final OrOperatorConverter orConverter, final OperatorCollectionConverter collectionConverter) {
        this._andConvereter = andConverter;
        this._notConverter = notConverter;
        this._orConverter = orConverter;
        collectionConverter.setPredicateConverter(this);
    }
    
    @Override
    public RowMatcher createMatcher(final PredicateOperator operator) {
        switch (operator.operator()) {
            case AND: {
                return this._andConvereter.createMatcher(operator);
            }
            case NOT: {
                return this._notConverter.createMatcher(operator);
            }
            case OR: {
                return this._orConverter.createMatcher(operator);
            }
            default: {
                throw new UnsupportedOperationException("Unsupported operator: " + operator.operator());
            }
        }
    }
}
