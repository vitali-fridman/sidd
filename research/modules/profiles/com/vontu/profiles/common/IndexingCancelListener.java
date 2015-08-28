// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import com.vontu.model.ModelStatus;
import com.vontu.model.ModelEvent;
import com.vontu.model.ModelConnectionEvent;
import com.vontu.model.DataAccessException;
import java.util.logging.Level;
import com.vontu.model.ConnectionException;
import java.text.MessageFormat;
import com.vontu.model.data.IndexedInfoSourceStatus;
import com.vontu.model.Model;
import java.util.logging.Logger;
import com.vontu.model.ModelConnectionListener;
import com.vontu.model.ModelChangeListener;

public final class IndexingCancelListener implements ModelChangeListener, ModelConnectionListener
{
    private static final Logger _logger;
    private final Cancellable _cancellable;
    private boolean _isAttached;
    private boolean _wasCancelled;
    private final Model _model;
    private final int _statusId;
    
    public IndexingCancelListener(final Model model, final int statusId, final Cancellable cancellable) {
        this._isAttached = true;
        assert model != null;
        assert statusId > 0;
        assert cancellable != null;
        this._model = model;
        this._cancellable = cancellable;
        this._statusId = statusId;
    }
    
    private synchronized void cancelIndexing() {
        if (this._isAttached) {
            this._wasCancelled = true;
            this._cancellable.cancel();
        }
    }
    
    private void interruptIfCancelled() {
        try {
            this._model.beginTransaction();
            final IndexedInfoSourceStatus status = (IndexedInfoSourceStatus)this._model.getDataObjectByKey((Class)IndexedInfoSourceStatus.class, this._statusId);
            this._model.commitTransaction();
            if (status != null && status.getStatus() == 7) {
                this.cancelIndexing();
            }
        }
        catch (ConnectionException e2) {
            IndexingCancelListener._logger.info(MessageFormat.format("Lost database connection while reading the profile index status with ID {0}.", this._statusId));
        }
        catch (DataAccessException e) {
            IndexingCancelListener._logger.log(Level.SEVERE, MessageFormat.format("Failed to retrieve the profile index status with ID {0}.", this._statusId), (Throwable)e);
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    public void connectionStateChange(final ModelConnectionEvent event) {
        if (event.getType() == 2) {
            this.interruptIfCancelled();
        }
    }
    
    public void modelChanged(final ModelEvent event) {
        if (event.getDataObjectKey() == this._statusId) {
            this.interruptIfCancelled();
        }
    }
    
    public void notificationActivated(final String host) {
        if (ModelStatus.isConnectionUnavailable()) {
            return;
        }
        this.interruptIfCancelled();
    }
    
    public void notificationDeactivated(final String host) {
    }
    
    public synchronized void detach() {
        this._isAttached = false;
    }
    
    public void start() {
        try {
            this._model.addModelChangeListener((ModelChangeListener)this, (Class)IndexedInfoSourceStatus.class);
            this._model.addModelConnectionListener((ModelConnectionListener)this);
        }
        catch (DataAccessException e) {
            throw new ProtectRuntimeException(ProtectError.DATA_ACCESS, (Throwable)e);
        }
    }
    
    public void stop() {
        this.detach();
        try {
            this._model.removeModelChangeListener((ModelChangeListener)this);
            this._model.removeModelConnectionListener((ModelConnectionListener)this);
        }
        catch (DataAccessException e) {
            IndexingCancelListener._logger.log(Level.SEVERE, "Failed to remove model notificatcion listener.", (Throwable)e);
        }
    }
    
    public synchronized boolean isIndexingCancelled() {
        return this._wasCancelled;
    }
    
    static {
        _logger = Logger.getLogger(IndexingCancelListener.class.getName());
    }
}
