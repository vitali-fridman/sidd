// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.monitor.communication.configset.ConfigSourceObserver;
import com.vontu.monitor.communication.configset.ObservableConfigSource;
import com.vontu.communication.data.ProfileDescriptorMarshallable;
import java.util.Collection;
import com.vontu.util.DisposedException;
import com.vontu.profiles.monitor.notification.LoadObserver;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.profiles.monitor.notification.CompositeLoadObserver;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import com.vontu.util.DisposableImpl;

public class IndexLoader extends DisposableImpl
{
    protected static final Logger _logger;
    private final Executor _executor;
    private final CompositeLoadObserver _observer;
    private final IndexLoaderHelper _helper;
    
    public IndexLoader(final Iterable<IndexDescriptorMarshallable> availableIndices, final LoadedIndexMap loadedIndices, final Executor executor) {
        this._observer = new CompositeLoadObserver();
        this._executor = executor;
        this._helper = new IndexLoaderHelper(availableIndices, loadedIndices, this._observer);
    }
    
    public void addNotificationListener(final LoadObserver listener) throws DisposedException {
        this.checkDisposed();
        this._observer.addObserver(listener);
    }
    
    public void dispose() throws Throwable {
        super.dispose();
        this._observer.clearObservers();
        this._helper.dispose();
    }
    
    private void execute(final Runnable loadRequest) {
        try {
            this._executor.execute(loadRequest);
        }
        catch (DisposedException e) {
            IndexLoader._logger.info("One or more load or unload requests won't be processed because the index loader has been shut down.");
        }
    }
    
    public void loadIndex(final IndexDescriptorMarshallable index) throws DisposedException {
        this.checkDisposed();
        this.execute(new LoadIndexRequest(index, this._helper));
    }
    
    public void reconcileIndices(final Collection<ProfileDescriptorMarshallable> profileDescriptor) throws DisposedException {
        this.checkDisposed();
        this.execute(new ReconcileIndicesRequest(this._helper, profileDescriptor));
    }
    
    public void removeNotificationListener(final LoadObserver listener) throws DisposedException {
        this.checkDisposed();
        this._observer.removeObserver(listener);
    }
    
    public void unloadIndex(final IndexDescriptorMarshallable index) throws DisposedException {
        this.checkDisposed();
        this.execute(new UnloadIndexRequest(index, this._helper));
    }
    
    public void registerWithIndexSource(final ObservableConfigSource<IndexDescriptorMarshallable> indexSource) {
        indexSource.addObserver((ConfigSourceObserver)new IndexSetObserver(this));
    }
    
    static {
        _logger = Logger.getLogger(IndexLoader.class.getName());
    }
}
