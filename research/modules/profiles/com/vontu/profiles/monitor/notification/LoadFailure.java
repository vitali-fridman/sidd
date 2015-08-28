// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.notification;

import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.profileindex.IndexException;

public class LoadFailure
{
    private final IndexException _exception;
    private final IndexDescriptorMarshallable _index;
    private final LoadOperation _operation;
    
    public LoadFailure(final IndexDescriptorMarshallable index, final LoadOperation operation, final IndexException exception) {
        assert index != null;
        assert exception != null;
        assert operation != null;
        this._index = index;
        this._exception = exception;
        this._operation = operation;
    }
    
    public IndexDescriptorMarshallable getIndex() {
        return this._index;
    }
    
    public IndexException getException() {
        return this._exception;
    }
    
    public LoadOperation getOperation() {
        return this._operation;
    }
}
