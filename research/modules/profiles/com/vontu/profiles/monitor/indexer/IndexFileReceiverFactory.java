// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.io.IOException;
import com.vontu.util.config.ConfigurationException;
import com.vontu.communication.dataflow.Receiver;
import java.io.File;
import com.vontu.communication.dataflow.ReceiverFactory;

public class IndexFileReceiverFactory implements ReceiverFactory
{
    private final File _indexFolder;
    
    public IndexFileReceiverFactory(final File indexFolder) {
        this._indexFolder = indexFolder;
    }
    
    public Receiver newInstance() {
        try {
            return (Receiver)new IndexFileReceiver(this._indexFolder);
        }
        catch (IOException e) {
            throw new ConfigurationException("Index folder " + this._indexFolder + " is invalid.");
        }
    }
}
