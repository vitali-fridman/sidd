// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;

public interface RowMatcher
{
    boolean[] matchRows(MatchInfo p0, RowPredicateMatcher p1) throws IndexException;
}
