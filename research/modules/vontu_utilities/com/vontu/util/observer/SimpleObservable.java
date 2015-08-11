// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.observer;

public interface SimpleObservable<T>
{
    void setObserver(T p0);
    
    void removeObserver(T p0);
}
