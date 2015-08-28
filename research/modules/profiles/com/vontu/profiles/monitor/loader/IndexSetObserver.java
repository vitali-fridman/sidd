// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.util.logging.Level;
import java.util.Iterator;
import java.util.logging.Logger;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.monitor.communication.configset.BasicConfigSourceObserver;

public class IndexSetObserver extends BasicConfigSourceObserver<IndexDescriptorMarshallable>
{
    private static final Logger _logger;
    private final IndexLoader _indexLoader;
    
    public IndexSetObserver(final IndexLoader indexLoader) {
        this._indexLoader = indexLoader;
    }
    
    public void configSourceInitialized(final Iterable<IndexDescriptorMarshallable> configSource) {
        IndexSetObserver._logger.fine("Received IndexMap initalized event: " + configSource);
        for (final IndexDescriptorMarshallable indexDescriptor : configSource) {
            this.loadIndex(indexDescriptor);
        }
    }
    
    protected void itemAdded(final IndexDescriptorMarshallable item) {
        IndexSetObserver._logger.log(Level.FINE, "Added profile index [{0}:{1} version {2}]. Will load.", IndexLogMessage.prependIndexDescriptorArgs(item, new Object[0]));
        this.loadIndex(item);
    }
    
    protected void itemRemoved(final IndexDescriptorMarshallable item) {
        IndexSetObserver._logger.log(Level.FINE, "Removed profile index [{0}:{1} version {2}]. Will unload.", IndexLogMessage.prependIndexDescriptorArgs(item, new Object[0]));
        this.unloadIndex(item);
    }
    
    protected void itemUpdated(final IndexDescriptorMarshallable item) {
        IndexSetObserver._logger.log(Level.FINE, "Updated profile index [{0}:{1} version {2}]. Will load if not loaded yet.", IndexLogMessage.prependIndexDescriptorArgs(item, new Object[0]));
        this.loadIndex(item);
    }
    
    private void loadIndex(final IndexDescriptorMarshallable indexDescrptior) {
        this._indexLoader.loadIndex(indexDescrptior);
    }
    
    private void unloadIndex(final IndexDescriptorMarshallable indexDescrptior) {
        this._indexLoader.unloadIndex(indexDescrptior);
    }
    
    static {
        _logger = Logger.getLogger(IndexSetObserver.class.getName());
    }
}
