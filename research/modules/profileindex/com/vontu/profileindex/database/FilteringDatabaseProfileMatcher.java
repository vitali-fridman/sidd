// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.ConditionViolation;
import com.vontu.detection.condition.FilteringDatabaseMatchCondition;

public interface FilteringDatabaseProfileMatcher
{
    ConditionViolation detectViolation(FilteringDatabaseMatchCondition p0, CryptographicContent p1) throws IndexException;
}
