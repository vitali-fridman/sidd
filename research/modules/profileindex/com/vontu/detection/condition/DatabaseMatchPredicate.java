// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.detection.condition;

public interface DatabaseMatchPredicate
{
    int column();
    
    DatabaseMatchPredicateOperator operator();
    
    String operands();
}
