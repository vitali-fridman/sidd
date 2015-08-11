// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.system;

public class MemoryInfoWithGc implements MemoryInfo
{
    private final MemoryInfo _delegate;
    private final GarbageCollector _garbageCollector;
    
    public MemoryInfoWithGc() {
        this(new SimpleMemoryInfo(), new GarbageCollector());
    }
    
    public MemoryInfoWithGc(final MemoryInfo delegate, final GarbageCollector garbageCollector) {
        this._delegate = delegate;
        this._garbageCollector = garbageCollector;
    }
    
    @Override
    public long getAvailableJvmMemory() {
        this._garbageCollector.runGc();
        return this._delegate.getAvailableJvmMemory();
    }
    
    @Override
    public boolean jvmHasMemory(final long requestedByteCount) {
        return requestedByteCount <= this._delegate.getAvailableJvmMemory() || requestedByteCount <= this.getAvailableJvmMemory();
    }
}
