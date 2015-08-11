// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class NullReadWriteLock implements ReadWriteLock
{
    @Override
    public Lock readLock() {
        return new NullLock();
    }
    
    @Override
    public Lock writeLock() {
        return new NullLock();
    }
}
