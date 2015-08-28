// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.notification;

import com.vontu.profiles.common.IndexDescriptor;
import com.vontu.profiles.common.ProfilesException;
import java.io.Serializable;

public final class IndexFailure implements Serializable
{
    private static final long serialVersionUID = 6339604577445168854L;
    private final ProfilesException _exception;
    private final IndexDescriptor _index;
    
    public IndexFailure(final IndexDescriptor index, final ProfilesException exception) {
        this._index = index;
        this._exception = exception;
    }
    
    public IndexDescriptor getIndex() {
        return this._index;
    }
    
    public ProfilesException getException() {
        return this._exception;
    }
}
