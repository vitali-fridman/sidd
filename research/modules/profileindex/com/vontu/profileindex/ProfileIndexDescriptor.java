// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

public interface ProfileIndexDescriptor
{
    ProtectedProfileDescriptor profile();
    
    long size();
    
    InputStreamFactory[] streams();
    
    int version();
}
