// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceUtils
{
    public static List<Runnable> shutdownExecutorServiceByDeadline(final ExecutorService executor, final long shutdownDeadlineNanoTime, final boolean interruptAllRunningTasks) throws InterruptedException {
        if (interruptAllRunningTasks) {
            executor.shutdownNow();
        }
        else {
            executor.shutdown();
        }
        final long now = System.nanoTime();
        final long waitTimeInNanos = Math.max(shutdownDeadlineNanoTime - now, 0L);
        boolean isExecutorTerminated = false;
        isExecutorTerminated = executor.awaitTermination(waitTimeInNanos, TimeUnit.NANOSECONDS);
        if (!isExecutorTerminated) {
            return executor.shutdownNow();
        }
        return Collections.emptyList();
    }
    
    public static List<Runnable> shutdownExecutorServiceByDeadlineUninterruptable(final ExecutorService executor, final long shutdownDeadlineNanoTime, final boolean interruptAllRunningTasks) {
        try {
            return shutdownExecutorServiceByDeadline(executor, shutdownDeadlineNanoTime, interruptAllRunningTasks);
        }
        catch (InterruptedException ex) {
            return Collections.emptyList();
        }
    }
}
