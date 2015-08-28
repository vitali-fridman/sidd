// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;

public interface DatabaseIndex
{
    DatabaseRowMatch[] findMatches(SearchTerm[] p0, int p1, int p2, int[] p3, int p4, boolean p5) throws IndexException;
    
    boolean[] validateRows(SearchTerm[][] p0, int[] p1, SearchCondition p2) throws IndexException;
}
