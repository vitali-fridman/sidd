// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class DisposableImpl implements Disposable
{
    private volatile boolean _isDisposed;
    
    @Override
    public void dispose() throws Throwable {
        this._isDisposed = true;
    }
    
    protected void checkDisposed() throws DisposedException {
        if (this._isDisposed) {
            throw new DisposedException("The instance has been shut down.");
        }
    }
    
    @Override
    public boolean isDisposed() {
        return this._isDisposed;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!this.isDisposed()) {
            this.dispose();
        }
        super.finalize();
    }
}
