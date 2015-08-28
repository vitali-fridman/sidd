// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

public interface ProfileIndexFactory
{
    ProfileIndex loadInstance(ProfileIndexDescriptor p0) throws IndexException;
}
