// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.DatabaseIndexDescriptorMarshallable;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import java.io.File;

class FileIndexDescriptorFactory
{
    private final File indexFolder;
    
    FileIndexDescriptorFactory(final File indexFolder) {
        this.indexFolder = indexFolder;
    }
    
    ProfileIndexDescriptor newInstance(final IndexDescriptorMarshallable marshallable) {
        if (marshallable instanceof DatabaseIndexDescriptorMarshallable) {
            return (ProfileIndexDescriptor)new FileDatabaseIndexDescriptor((DatabaseIndexDescriptorMarshallable)marshallable, this.indexFolder);
        }
        return (ProfileIndexDescriptor)new FileIndexDescriptor(marshallable, this.indexFolder);
    }
}
