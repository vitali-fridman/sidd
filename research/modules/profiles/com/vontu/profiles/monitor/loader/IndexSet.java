// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.util.Iterator;
import com.vontu.communication.data.IndexDescriptorMarshallable;

class IndexSet implements Iterable<IndexDescriptorMarshallable>
{
    private final Iterable<IndexDescriptorMarshallable> _configSource;
    
    IndexSet(final Iterable<IndexDescriptorMarshallable> configSource) {
        this._configSource = configSource;
    }
    
    @Override
    public Iterator<IndexDescriptorMarshallable> iterator() {
        return this._configSource.iterator();
    }
    
    public boolean contains(final IndexDescriptorMarshallable indexDescriptor) {
        for (final IndexDescriptorMarshallable configItem : this._configSource) {
            if (configItem.equals((Object)indexDescriptor)) {
                return true;
            }
        }
        return false;
    }
}
