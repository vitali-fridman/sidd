// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

public interface DataSourceColumnStatistics
{
    int column();
    
    int invalidCellCount();
    
    int noDataCellCount();
}
