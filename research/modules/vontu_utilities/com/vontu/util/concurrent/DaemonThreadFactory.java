// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory
{
    private static final Logger _logger;
    private final ThreadFactory _threadFactory;
    
    public DaemonThreadFactory() {
        this._threadFactory = Executors.defaultThreadFactory();
    }
    
    public DaemonThreadFactory(final ThreadFactory threadFactory) {
        this._threadFactory = threadFactory;
    }
    
    public DaemonThreadFactory(final String threadName) {
        this._threadFactory = new NamedThreadFactory(threadName);
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        DaemonThreadFactory._logger.fine("Creating new daemon thread for runnable " + runnable);
        final Thread thread = this._threadFactory.newThread(runnable);
        thread.setDaemon(true);
        return thread;
    }
    
    static {
        _logger = Logger.getLogger(DaemonThreadFactory.class.getName());
    }
}
