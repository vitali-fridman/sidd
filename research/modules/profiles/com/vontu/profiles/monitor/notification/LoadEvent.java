// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.notification;

import com.vontu.communication.data.IndexDescriptorMarshallable;

public class LoadEvent
{
    private final IndexDescriptorMarshallable _index;
    private final LoadOperation _operation;
    
    public LoadEvent(final IndexDescriptorMarshallable index, final LoadOperation operation) {
        this._index = index;
        this._operation = operation;
    }
    
    public IndexDescriptorMarshallable getIndex() {
        return this._index;
    }
    
    public LoadOperation getOperation() {
        return this._operation;
    }
}
