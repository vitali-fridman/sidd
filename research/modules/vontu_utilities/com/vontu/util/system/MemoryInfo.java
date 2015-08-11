// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.system;

public interface MemoryInfo
{
    long getAvailableJvmMemory();
    
    boolean jvmHasMemory(long p0);
}
