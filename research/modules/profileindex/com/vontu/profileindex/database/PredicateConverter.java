// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.detection.condition.PredicateOperator;

public interface PredicateConverter
{
    RowMatcher createMatcher(PredicateOperator p0);
}
