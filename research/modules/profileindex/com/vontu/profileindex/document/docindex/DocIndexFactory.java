// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import com.vontu.profileindex.document.docindex.rmi.RmiDocIndexProxy;
import com.vontu.logging.operational.api.OperationalLog;
import com.vontu.detection.logging.DetectionOperationLogCode;
import com.vontu.profileindex.document.DocIndex;
import com.vontu.profileindex.IndexException;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.profileindex.ProfileIndexMemory;

public class DocIndexFactory extends ProfileIndexMemory
{
    private final ProfileIndexDescriptor _indexDescriptor;
    private final SettingProvider _settingProvider;
    
    public DocIndexFactory(final SettingProvider settingProvider, final ProfileIndexDescriptor indexDescriptor) throws IndexException {
        super(settingProvider, indexDescriptor);
        this._indexDescriptor = indexDescriptor;
        this._settingProvider = settingProvider;
    }
    
    private String getProcessName() {
        return "DOCIDX_" + this._indexDescriptor.profile().profileId() + '_' + this._indexDescriptor.version();
    }
    
    public DocIndex loadInstance() throws IndexException {
        this.checkFitsInRam();
        if (this.doesFitInProcessMemory()) {
            OperationalLog.logEvent(DetectionOperationLogCode.IDM_PROFILE_LOADING_IN_PROCESS, new Object[] { this._indexDescriptor.profile().name(), this._indexDescriptor.profile().profileId(), this._indexDescriptor.version() });
            return new DocIndexImpl(new Configuration(this._settingProvider), this.getSingleStreamFactory());
        }
        OperationalLog.logEvent(DetectionOperationLogCode.IDM_PROFILE_LOADING_OUT_OF_PROCESS, new Object[] { this._indexDescriptor.profile().name(), this._indexDescriptor.profile().profileId(), this._indexDescriptor.version() });
        return new RmiDocIndexProxy(this._settingProvider, this.getProcessName(), this.getSingleStreamFactory(), this._indexDescriptor.size());
    }
}
