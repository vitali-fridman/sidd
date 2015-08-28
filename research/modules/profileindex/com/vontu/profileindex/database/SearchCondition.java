// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

public interface SearchCondition
{
    int getColumnMask();
    
    String getInfoSourceId();
    
    int[] getExceptionTuples();
    
    int getMinimumMatches();
    
    int getThreshold();
}
