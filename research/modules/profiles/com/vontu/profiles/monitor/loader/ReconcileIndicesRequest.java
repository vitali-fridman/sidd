// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.util.ArrayList;
import com.vontu.communication.data.ProfileDescriptorMarshallable;
import java.util.Collection;

class ReconcileIndicesRequest implements Runnable
{
    private final IndexLoaderHelper _loader;
    private final Collection<ProfileDescriptorMarshallable> _profileDescriptors;
    
    ReconcileIndicesRequest(final IndexLoaderHelper loader, final Collection<ProfileDescriptorMarshallable> profileDescriptors) {
        this._loader = loader;
        this._profileDescriptors = new ArrayList<ProfileDescriptorMarshallable>(profileDescriptors);
    }
    
    @Override
    public void run() {
        this._loader.reconcileIndices(this._profileDescriptors);
    }
}
