// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Collection;

class ChildProcessMonitor implements ProcessStateObserver
{
    private final Collection<ChildProcessStateObserver> _observers;
    private final ChildProcessProxy _childProcess;
    private ProcessStateMonitor _processStateMonitor;
    
    ChildProcessMonitor(final ChildProcessProxy childProcess) {
        this._observers = new CopyOnWriteArraySet<ChildProcessStateObserver>();
        this._processStateMonitor = ProcessStateMonitor.NULL_MONITOR;
        this._childProcess = childProcess;
    }
    
    void addObserver(final ChildProcessStateObserver observer) {
        this._observers.add(observer);
    }
    
    void removeObserver(final ChildProcessStateObserver observer) {
        this._observers.remove(observer);
    }
    
    @Override
    public void processWentDown(final Process process) {
        for (final ChildProcessStateObserver observer : this._observers) {
            observer.processWentDown(this._childProcess);
        }
    }
    
    synchronized void observe(final Process process) {
        this._processStateMonitor.stop();
        this._processStateMonitor.removeObserver(this);
        (this._processStateMonitor = new ProcessStateMonitor(process)).addObserver(this);
        this._processStateMonitor.start();
    }
    
    synchronized ProcessStateMonitor getProcessStateMonitor() {
        return this._processStateMonitor;
    }
}
