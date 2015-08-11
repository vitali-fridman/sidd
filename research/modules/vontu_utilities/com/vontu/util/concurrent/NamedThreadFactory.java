// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory
{
    private final String _threadName;
    private final AtomicInteger threadCounter;
    private final ThreadGroup _threadGroup;
    
    public NamedThreadFactory(final String threadName) {
        this.threadCounter = new AtomicInteger(1);
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            this._threadGroup = securityManager.getThreadGroup();
        }
        else {
            this._threadGroup = Thread.currentThread().getThreadGroup();
        }
        this._threadName = threadName;
    }
    
    public NamedThreadFactory(final ThreadGroup threadGroup, final String threadName) {
        this.threadCounter = new AtomicInteger(1);
        this._threadGroup = threadGroup;
        this._threadName = threadName;
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        return new Thread(this._threadGroup, runnable, this._threadName + "_" + this.threadCounter.getAndIncrement());
    }
}
