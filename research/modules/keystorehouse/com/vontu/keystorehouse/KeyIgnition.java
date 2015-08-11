// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import com.vontu.security.KeyStorehouseException;

public interface KeyIgnition
{
    void forceNotIgnited();
    
    void igniteKeys(byte[] p0) throws KeyStorehouseException;
    
    boolean isIgnited();
}
