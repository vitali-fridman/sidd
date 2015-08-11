// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import com.vontu.util.DisposedException;
import java.util.concurrent.Executor;
import com.vontu.util.DisposableImpl;

public class WorkQueue extends DisposableImpl implements Executor
{
    private final QueueingTask _task;
    private final WorkerThread _workerThread;
    
    public WorkQueue(final String name) {
        this(Thread.currentThread().getThreadGroup(), name, false);
    }
    
    public WorkQueue(final String name, final boolean useDaemonThread) {
        this(Thread.currentThread().getThreadGroup(), name, useDaemonThread);
    }
    
    public WorkQueue(final ThreadGroup threadGroup, final String name, final boolean useDaemonThread) {
        this._task = new QueueingTask();
        this._workerThread = new WorkerThread(threadGroup, name, this._task);
        if (useDaemonThread) {
            this._workerThread.setDaemon(true);
        }
        this._workerThread.start();
    }
    
    @Override
    public synchronized void execute(final Runnable work) throws DisposedException {
        this.checkDisposed();
        this._task.queueRequest(work);
        this._workerThread.continueWork();
    }
    
    @Override
    public void dispose() throws Throwable {
        super.dispose();
        this._workerThread.stopWork();
        this._workerThread.interrupt();
    }
    
    public int getQueueSize() {
        return this._task.getQueueSize();
    }
}
