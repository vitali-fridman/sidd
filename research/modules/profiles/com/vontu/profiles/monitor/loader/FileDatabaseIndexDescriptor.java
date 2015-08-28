// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.IndexDescriptorMarshallable;
import java.io.File;
import com.vontu.communication.data.DatabaseIndexDescriptorMarshallable;
import com.vontu.profileindex.database.DatabaseProfileIndexDescriptor;

class FileDatabaseIndexDescriptor extends FileIndexDescriptor implements DatabaseProfileIndexDescriptor
{
    private final String _cryptoKeyAlias;
    
    FileDatabaseIndexDescriptor(final DatabaseIndexDescriptorMarshallable indexDescriptor, final File indexFolder) {
        super((IndexDescriptorMarshallable)indexDescriptor, indexFolder);
        this._cryptoKeyAlias = indexDescriptor.getCryptoKeyAlias();
    }
    
    public String cryptoKeyAlias() {
        return this._cryptoKeyAlias;
    }
}
