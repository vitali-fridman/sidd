// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.AbstractExecutorService;

public class DirectExecutorService extends AbstractExecutorService
{
    private boolean isTerminated;
    private boolean isShutdown;
    
    public DirectExecutorService() {
        this.isTerminated = true;
        this.isShutdown = false;
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return true;
    }
    
    @Override
    public boolean isShutdown() {
        return this.isShutdown;
    }
    
    @Override
    public boolean isTerminated() {
        return this.isTerminated;
    }
    
    @Override
    public void shutdown() {
        this.isShutdown = true;
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return Collections.emptyList();
    }
    
    @Override
    public void execute(final Runnable command) {
        this.isTerminated = false;
        command.run();
    }
}
