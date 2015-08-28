// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.IndexDescriptorMarshallable;

class UnloadIndexRequest implements Runnable
{
    private final IndexDescriptorMarshallable _index;
    private final IndexLoaderHelper _loader;
    
    UnloadIndexRequest(final IndexDescriptorMarshallable index, final IndexLoaderHelper loader) {
        assert index != null;
        assert loader != null;
        this._index = index;
        this._loader = loader;
    }
    
    @Override
    public void run() {
        this._loader.unloadIndex(this._index.getProfile().getProfileID());
    }
}
