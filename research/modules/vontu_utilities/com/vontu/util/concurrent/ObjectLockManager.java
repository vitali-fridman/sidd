// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.logging.Level;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ObjectLockManager<T>
{
    private static final Logger _logger;
    private final Set<T> _lockedObjects;
    
    public ObjectLockManager() {
        this._lockedObjects = new HashSet<T>();
    }
    
    public void acquireLock(final T lockObject) throws InterruptedException {
        ObjectLockManager._logger.log(Level.FINEST, "{1}: Acquiring a lock for {0}.", new Object[] { lockObject, Thread.currentThread().getName() });
        synchronized (this._lockedObjects) {
            while (!this._lockedObjects.add(lockObject)) {
                this._lockedObjects.wait();
            }
        }
        ObjectLockManager._logger.log(Level.FINEST, "{1}: Acquired a lock for {0}.", new Object[] { lockObject, Thread.currentThread().getName() });
    }
    
    public void releaseLock(final T lockObject) {
        synchronized (this._lockedObjects) {
            this._lockedObjects.remove(lockObject);
            this._lockedObjects.notifyAll();
        }
        ObjectLockManager._logger.log(Level.FINEST, "{1}: Released the lock for {0}.", new Object[] { lockObject, Thread.currentThread().getName() });
    }
    
    public void releaseAll() {
        synchronized (this._lockedObjects) {
            this._lockedObjects.clear();
            this._lockedObjects.notifyAll();
        }
    }
    
    static {
        _logger = Logger.getLogger(ObjectLockManager.class.getName());
    }
}
