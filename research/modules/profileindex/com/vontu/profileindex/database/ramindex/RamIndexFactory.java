// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.ramindex.rmi.RmiRamIndexProxy;
import com.vontu.profileindex.database.ramindex.multipart.MultipartRamIndex;
import com.vontu.profileindex.database.DatabaseIndex;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindex.ProfileIndexMemory;

public class RamIndexFactory extends ProfileIndexMemory
{
    private final int _numberOfFiles;
    private final SettingProvider _settingProvider;
    private final ProfileIndexDescriptor _indexDescriptor;
    
    public RamIndexFactory(final SettingProvider settingProvider, final ProfileIndexDescriptor indexDescriptor) {
        super(settingProvider, indexDescriptor);
        this._numberOfFiles = indexDescriptor.streams().length;
        this._settingProvider = settingProvider;
        this._indexDescriptor = indexDescriptor;
    }
    
    private String getProcessName() {
        return "IDX_" + this._indexDescriptor.profile().profileId() + '_' + this._indexDescriptor.version();
    }
    
    public DatabaseIndex loadInstance() throws IndexException {
        this.checkFitsInRam();
        if (this._numberOfFiles > 1) {
            return new MultipartRamIndex(this._settingProvider, this._indexDescriptor);
        }
        if (this.doesFitInProcessMemory()) {
            return new RamIndex(new Configuration(this._settingProvider), this.getSingleStreamFactory());
        }
        return new RmiRamIndexProxy(this._settingProvider, this.getProcessName(), this.getSingleStreamFactory(), this._indexDescriptor.size());
    }
}
