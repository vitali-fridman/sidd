// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

public interface ConnectionPoolStatisticsMXBean
{
    void setMaxAllowedActiveConnections(int p0);
    
    int getMaxAllowedActiveConnections();
    
    int getActiveConnectionCount();
    
    int getIdleConnectionCount();
    
    int getMaxIdleConnections();
    
    void setMaxIdleConnections(int p0);
}
