// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.detection.condition;

import java.util.Collection;

public interface PredicateOperator
{
    BooleanOperator operator();
    
    Collection<MultiColumnPredicate> predicates();
    
    Collection<PredicateOperator> nestedOperators();
}
