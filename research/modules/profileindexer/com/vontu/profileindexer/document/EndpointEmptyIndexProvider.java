// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

public class EndpointEmptyIndexProvider implements EndpointIndexProvider
{
    @Override
    public boolean hasMoreEntries() {
        return false;
    }
    
    @Override
    public int peekNextEntryDocId() {
        throw new RuntimeException("No EndpointIndexEntry exists");
    }
    
    @Override
    public EndpointIndexEntry getNextEntry() {
        throw new RuntimeException("No EndpointIndexEntry exists");
    }
    
    @Override
    public void skipNextEntry() {
        throw new RuntimeException("No EndpointIndexEntry exists");
    }
}
