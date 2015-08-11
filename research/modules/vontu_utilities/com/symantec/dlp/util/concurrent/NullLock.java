// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public final class NullLock implements Lock
{
    @Override
    public void lock() {
    }
    
    @Override
    public void lockInterruptibly() throws InterruptedException {
    }
    
    @Override
    public boolean tryLock() {
        return true;
    }
    
    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return true;
    }
    
    @Override
    public void unlock() {
    }
    
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
