// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerThread extends Thread
{
    private static final Logger logger;
    private Object _monitor;
    private Task _task;
    private volatile boolean _work;
    private long _sleepInterval;
    
    public WorkerThread(final String name, final Task task) {
        super(name);
        this._monitor = new Object();
        this._work = true;
        this._sleepInterval = 0L;
        this._task = task;
    }
    
    public WorkerThread(final ThreadGroup threadGroup, final String threadName, final Task task) {
        this(threadGroup, threadName, 0L, task);
    }
    
    public WorkerThread(final ThreadGroup threadGroup, final String threadName, final long sleepInterval, final Task task) {
        super(threadGroup, threadName);
        this._monitor = new Object();
        this._work = true;
        this._sleepInterval = 0L;
        this._sleepInterval = sleepInterval;
        this._task = task;
    }
    
    public Task getTask() {
        return this._task;
    }
    
    public long getSleepInterval() {
        return this._sleepInterval;
    }
    
    @Override
    public void run() {
        while (this._work) {
            if (this._task.hasWork()) {
                try {
                    this._task.performWork();
                    if (this._sleepInterval <= 0L) {
                        continue;
                    }
                    try {
                        Thread.sleep(this._sleepInterval);
                    }
                    catch (InterruptedException ex) {}
                }
                catch (Exception e) {
                    WorkerThread.logger.log(Level.SEVERE, "Task failed to perform work.", e);
                }
            }
            else {
                synchronized (this._monitor) {
                    if (!this._work) {
                        continue;
                    }
                    try {
                        this._monitor.wait();
                    }
                    catch (InterruptedException ex2) {}
                }
            }
        }
    }
    
    public void continueWork() {
        synchronized (this._monitor) {
            this._monitor.notify();
        }
    }
    
    public void stopWork() {
        synchronized (this._monitor) {
            this._work = false;
            this._monitor.notify();
        }
    }
    
    static {
        logger = Logger.getLogger(WorkerThread.class.getName());
    }
}
