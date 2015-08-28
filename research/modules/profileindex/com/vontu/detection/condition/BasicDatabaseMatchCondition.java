// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.detection.condition;

public interface BasicDatabaseMatchCondition extends ProfileMatchCondition
{
    int[] columns();
    
    int columnThreshold();
    
    int[][] excludedTuples();
    
    int minMatchCount();
}
