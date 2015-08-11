// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.system;

public class SimpleMemoryInfo implements MemoryInfo
{
    private final Runtime _runtime;
    
    public SimpleMemoryInfo() {
        this(Runtime.getRuntime());
    }
    
    public SimpleMemoryInfo(final Runtime runtime) {
        this._runtime = runtime;
    }
    
    @Override
    public long getAvailableJvmMemory() {
        return this._runtime.freeMemory() + this._runtime.maxMemory() - this._runtime.totalMemory();
    }
    
    @Override
    public boolean jvmHasMemory(final long requestedByteCount) {
        return requestedByteCount <= this.getAvailableJvmMemory();
    }
}
