// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

public interface ProfileIndex
{
    ProfileIndexDescriptor getDescriptor();
    
    void unload() throws IndexException;
}
