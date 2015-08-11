// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.Executor;

public class DirectExecutor implements Executor
{
    @Override
    public void execute(final Runnable command) {
        command.run();
    }
}
