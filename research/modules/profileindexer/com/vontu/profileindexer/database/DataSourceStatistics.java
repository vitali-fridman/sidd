// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

public interface DataSourceStatistics
{
    int rowCount();
    
    int cellCount();
    
    int invalidRowCount();
    
    int tooShortRowCount();
    
    int tooLongRowCount();
    
    int noDataCellCount();
    
    DataSourceColumnStatistics[] columnStatistics();
}
