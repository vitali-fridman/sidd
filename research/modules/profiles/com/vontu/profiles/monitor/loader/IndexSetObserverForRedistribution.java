// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.util.logging.Level;
import com.vontu.communication.transport.Address;
import com.vontu.communication.dataflow.ShipmentException;
import com.vontu.communication.dataflow.AccessReport;
import com.vontu.communication.dataflow.Marshallable;
import java.util.Collection;
import com.vontu.communication.data.IndexDescriptorSetMarshallable;
import java.util.Iterator;
import java.util.HashMap;
import com.vontu.messaging.Registry;
import com.vontu.communication.dataflow.Messenger;
import java.util.Map;
import java.util.logging.Logger;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.monitor.communication.configset.BasicConfigSourceObserver;

public class IndexSetObserverForRedistribution extends BasicConfigSourceObserver<IndexDescriptorMarshallable>
{
    private static Logger logger;
    private Map<Integer, IndexDescriptorMarshallable> profileIdToIndexDescriptorMap;
    private Messenger messenger;
    
    public IndexSetObserverForRedistribution() {
        this(Registry.getMessenger());
    }
    
    public IndexSetObserverForRedistribution(final Messenger messenger) {
        this.profileIdToIndexDescriptorMap = new HashMap<Integer, IndexDescriptorMarshallable>();
        this.messenger = messenger;
    }
    
    protected void itemAdded(final IndexDescriptorMarshallable indexDescriptor) {
        this.putIndexDescriptor(indexDescriptor);
        this.broadcastIndexDescriptorSetMarshallable();
    }
    
    protected void itemRemoved(final IndexDescriptorMarshallable indexDescriptor) {
        final Integer profileId = Integer.parseInt(indexDescriptor.getProfile().getProfileID());
        this.profileIdToIndexDescriptorMap.remove(profileId);
        this.broadcastIndexDescriptorSetMarshallable();
    }
    
    protected void itemUpdated(final IndexDescriptorMarshallable indexDescriptor) {
        this.putIndexDescriptor(indexDescriptor);
        this.broadcastIndexDescriptorSetMarshallable();
    }
    
    public void configSourceInitialized(final Iterable<IndexDescriptorMarshallable> indexDescriptors) {
        for (final IndexDescriptorMarshallable indexDescriptor : indexDescriptors) {
            this.putIndexDescriptor(indexDescriptor);
        }
        this.broadcastIndexDescriptorSetMarshallable();
    }
    
    private void putIndexDescriptor(final IndexDescriptorMarshallable indexDescriptor) {
        if (!indexDescriptor.getEndpointBaseFileName().equals("")) {
            final Integer profileId = Integer.parseInt(indexDescriptor.getProfile().getProfileID());
            this.profileIdToIndexDescriptorMap.put(profileId, indexDescriptor);
        }
    }
    
    private void broadcastIndexDescriptorSetMarshallable() {
        final IndexDescriptorSetMarshallable indexDescriptorSetMarshallable = new IndexDescriptorSetMarshallable();
        indexDescriptorSetMarshallable.getIndexDescriptors().addAll((Collection)this.profileIdToIndexDescriptorMap.values());
        try {
            this.messenger.expose("Endpoint Document Profile Redistribution Channel", (Marshallable)indexDescriptorSetMarshallable, (AccessReport)new IndexDescriptorSetMarshallableAccessReport());
        }
        catch (ShipmentException e) {
            e.printStackTrace();
        }
    }
    
    static {
        IndexSetObserverForRedistribution.logger = Logger.getLogger(IndexSetObserverForRedistribution.class.getName());
    }
    
    private class IndexDescriptorSetMarshallableAccessReport extends AccessReport
    {
        protected void onDataAccess(final Address accessor) {
            IndexSetObserverForRedistribution.logger.log(Level.FINEST, "IndexDescriptorSet: " + IndexSetObserverForRedistribution.this.profileIdToIndexDescriptorMap.values() + "   retrieved by address : " + accessor);
        }
    }
}
