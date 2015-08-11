// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.concurrent.CopyOnWriteArraySet;
import com.vontu.util.concurrent.Latch;
import java.util.Collection;
import java.util.logging.Logger;

public class ProcessStateMonitor implements Runnable
{
    private static final Logger _logger;
    public static final ProcessStateMonitor NULL_MONITOR;
    private final Collection<ProcessStateObserver> _observers;
    private final Process _process;
    private final Latch _shutdownLatch;
    private volatile Thread _thread;
    
    public ProcessStateMonitor(final Process process) {
        this._observers = new CopyOnWriteArraySet<ProcessStateObserver>();
        this._shutdownLatch = new Latch();
        this._process = process;
    }
    
    public void start() {
        if (this._process == null) {
            return;
        }
        (this._thread = new Thread(this, "ProcessStateMonitor")).setDaemon(true);
        this._thread.setPriority(10);
        this._thread.start();
    }
    
    public void stop() {
        if (this._thread != null) {
            this._thread.interrupt();
        }
    }
    
    public void addObserver(final ProcessStateObserver observer) {
        this._observers.add(observer);
    }
    
    public void removeObserver(final ProcessStateObserver observer) {
        this._observers.remove(observer);
    }
    
    @Override
    public void run() {
        try {
            this._process.waitFor();
        }
        catch (InterruptedException e) {
            return;
        }
        this._shutdownLatch.release();
        for (final ProcessStateObserver observer : this._observers) {
            try {
                observer.processWentDown(this._process);
            }
            catch (Throwable t) {
                ProcessStateMonitor._logger.log(Level.SEVERE, "Failed to handle process down event.", t);
            }
        }
    }
    
    public boolean waitForShutdown(final int msecs) throws InterruptedException {
        return this._process == null || this._shutdownLatch.attempt(msecs);
    }
    
    public void waitForShutdown() throws InterruptedException {
        if (this._process == null) {
            return;
        }
        this._shutdownLatch.acquire();
    }
    
    static {
        _logger = Logger.getLogger(ProcessStateMonitor.class.getName());
        NULL_MONITOR = new ProcessStateMonitor(null);
    }
}
