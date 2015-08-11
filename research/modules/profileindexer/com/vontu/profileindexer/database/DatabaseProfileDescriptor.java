// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

public interface DatabaseProfileDescriptor
{
    String name();
    
    DatabaseProfileColumn[] columns();
    
    boolean hasHeaders();
    
    int errorThreshold();
    
    char columnSeparator();
}
