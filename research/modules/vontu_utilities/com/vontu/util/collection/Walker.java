// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public interface Walker<T>
{
    void cleanUp();
    
    boolean isCleanedUp();
    
    T getCurrentEntry();
    
    T advance();
}
