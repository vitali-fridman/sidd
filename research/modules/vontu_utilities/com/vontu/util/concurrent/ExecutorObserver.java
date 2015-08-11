// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

public interface ExecutorObserver<R extends Runnable>
{
    void afterExecute(R p0, Throwable p1);
}
