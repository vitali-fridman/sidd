// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.profiles.monitor.database.DatabaseIndexFilenameTemplate;
import com.vontu.model.data.IndexedInfoSource;
import java.io.Serializable;

public class DatabaseIndexDescriptor extends IndexDescriptor implements Serializable
{
    private static final long serialVersionUID = -5340471906674223058L;
    private final String _cryptoKeyAlias;
    
    public DatabaseIndexDescriptor(final IndexedInfoSource indexedInfoSource, final DatabaseIndexFilenameTemplate filenameTemplate) {
        super(indexedInfoSource, filenameTemplate);
        this._cryptoKeyAlias = indexedInfoSource.getKeyAlias();
    }
    
    public String getCryptoKeyAlias() {
        return this._cryptoKeyAlias;
    }
}
