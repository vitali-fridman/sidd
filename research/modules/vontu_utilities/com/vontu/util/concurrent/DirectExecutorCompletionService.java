// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

public class DirectExecutorCompletionService<T> extends ExecutorCompletionService<T>
{
    public DirectExecutorCompletionService() {
        super(new DirectExecutor());
    }
}
