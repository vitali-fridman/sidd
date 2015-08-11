// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

public interface EndpointIndexProvider
{
    boolean hasMoreEntries();
    
    int peekNextEntryDocId();
    
    EndpointIndexEntry getNextEntry();
    
    void skipNextEntry();
}
