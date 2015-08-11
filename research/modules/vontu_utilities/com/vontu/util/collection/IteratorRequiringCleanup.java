// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public interface IteratorRequiringCleanup<E>
{
    boolean hasNext();
    
    E next();
    
    void cleanUp();
}
