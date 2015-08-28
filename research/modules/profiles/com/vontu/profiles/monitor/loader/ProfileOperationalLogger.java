// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.profiles.monitor.notification.LoadFailure;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.logging.operational.api.OperationalLog;
import com.vontu.detection.logging.DetectionOperationLogCode;
import com.vontu.profiles.monitor.notification.LoadOperation;
import com.vontu.profiles.monitor.notification.LoadEvent;
import com.vontu.profiles.monitor.notification.LoadObserver;

public class ProfileOperationalLogger implements LoadObserver
{
    @Override
    public void loadComplete(final LoadEvent event) {
        final LoadOperation operation = event.getOperation();
        final IndexDescriptorMarshallable index = event.getIndex();
        if (operation == LoadOperation.LOAD_INDEX) {
            OperationalLog.logEvent(DetectionOperationLogCode.PROFILE_LOADED, IndexLogMessage.prependIndexDescriptorArgs(index, new Object[0]));
        }
        else if (operation == LoadOperation.UNLOAD_INDEX) {
            OperationalLog.logEvent(DetectionOperationLogCode.PROFILE_UNLOADED, IndexLogMessage.prependIndexDescriptorArgs(index, new Object[0]));
        }
    }
    
    @Override
    public void loadFailed(final LoadFailure event) {
        final LoadOperation operation = event.getOperation();
        final IndexDescriptorMarshallable index = event.getIndex();
        final Throwable indexException = (Throwable)event.getException();
        if (operation == LoadOperation.LOAD_INDEX) {
            OperationalLog.logError(DetectionOperationLogCode.PROFILE_LOAD_FAILED, indexException, IndexLogMessage.prependIndexDescriptorArgs(index, new Object[0]));
        }
        else if (operation == LoadOperation.UNLOAD_INDEX) {
            OperationalLog.logError(DetectionOperationLogCode.PROFILE_UNLOAD_FAILED, indexException, IndexLogMessage.prependIndexDescriptorArgs(index, new Object[0]));
        }
    }
}
