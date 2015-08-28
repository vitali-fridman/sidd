// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.communication.dataflow.Remotable;
import com.vontu.profileindex.IndexError;
import com.vontu.profiles.monitor.notification.LoadFailure;
import com.vontu.profiles.monitor.notification.LoadEvent;
import com.vontu.communication.transport.Address;
import com.vontu.communication.dataflow.Messenger;
import com.vontu.monitor.communication.sender.RemotableDeliveryReport;
import com.vontu.messaging.Registry;
import com.vontu.communication.data.IndexLoadFailureEventRemote;
import com.vontu.monitor.communication.sender.RetryQueueSender;
import com.vontu.profiles.monitor.notification.LoadObserver;

public class LoadFailureSender implements LoadObserver
{
    private final RetryQueueSender<IndexLoadFailureEventRemote> _sender;
    
    public LoadFailureSender(final String remotableId) {
        this(remotableId, Registry.getMessenger(), Registry.getControllerAddress(), (RemotableDeliveryReport<IndexLoadFailureEventRemote>)new EventDeliveryReport());
    }
    
    public LoadFailureSender(final String remotableId, final Messenger messenger, final Address address, final RemotableDeliveryReport<IndexLoadFailureEventRemote> remotableReport) {
        this((RetryQueueSender<IndexLoadFailureEventRemote>)new RetryQueueSender(messenger, address, remotableId, (RemotableDeliveryReport)remotableReport));
    }
    
    LoadFailureSender(final RetryQueueSender<IndexLoadFailureEventRemote> sender) {
        this._sender = sender;
    }
    
    @Override
    public void loadComplete(final LoadEvent event) {
    }
    
    @Override
    public void loadFailed(final LoadFailure failure) {
        if (IndexError.INDEX_LOAD_ERROR.equals(failure.getException().getError())) {
            this._sender.send((Remotable)createFailureEvent(failure.getIndex()));
        }
    }
    
    private static IndexLoadFailureEventRemote createFailureEvent(final IndexDescriptorMarshallable index) {
        final IndexLoadFailureEventRemote indexFailureEvent = new IndexLoadFailureEventRemote();
        indexFailureEvent.setProfileIndexId(String.valueOf(index.getIndexId()));
        return indexFailureEvent;
    }
}
