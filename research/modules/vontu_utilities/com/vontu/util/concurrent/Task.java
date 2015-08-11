// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

public interface Task
{
    boolean hasWork();
    
    void performWork();
}
