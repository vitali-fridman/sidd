// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.concurrent;

import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import com.vontu.util.concurrent.DaemonThreadFactory;
import com.vontu.util.concurrent.NamedThreadFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class DeadlineTaskQueue
{
    private static final Logger logger;
    private static final AtomicLong nextTaskNumber;
    private final TreeSet<DeadlineTask> tasks;
    private final ExecutorService dispatcherExecutor;
    private final ExecutorService workersExecutor;
    private final ReentrantLock queueLock;
    private final Condition wakeUpCondition;
    
    public DeadlineTaskQueue(final String namePrefixForThreads, final int workerCount) {
        this(createDispatcherExecutor(namePrefixForThreads + "DeadlineTaskQueue", namePrefixForThreads + "Dispatcher"), createWorkersExecutor(namePrefixForThreads + "DeadlineTaskQueue", namePrefixForThreads + "Worker", workerCount));
    }
    
    DeadlineTaskQueue(final ExecutorService dispatcherExecutor, final ExecutorService workersExecutor) {
        this.tasks = new TreeSet<DeadlineTask>();
        this.queueLock = new ReentrantLock();
        this.wakeUpCondition = this.queueLock.newCondition();
        this.dispatcherExecutor = dispatcherExecutor;
        this.workersExecutor = workersExecutor;
        dispatcherExecutor.execute(new Dispatcher());
    }
    
    public DeadlineTask schedule(final Runnable task, final long deadlineInNanos) {
        this.queueLock.lock();
        try {
            this.wakeUpDispatcherIfEarliestDeadlineHasChanged(deadlineInNanos);
            final DeadlineTask deadlineTask = new DeadlineTask(task, deadlineInNanos, DeadlineTaskQueue.nextTaskNumber.getAndIncrement());
            if (!this.tasks.add(deadlineTask)) {
                throw new IllegalStateException("Unable to add task to deadline queue.");
            }
            return deadlineTask;
        }
        finally {
            this.queueLock.unlock();
        }
    }
    
    private void wakeUpDispatcherIfEarliestDeadlineHasChanged(final long newDeadline) {
        final long earliestDeadline = this.getEarliestDeadline();
        if (newDeadline < earliestDeadline) {
            this.wakeUpDispatcher();
        }
    }
    
    private long getEarliestDeadline() {
        if (this.tasks.size() == 0) {
            return Long.MAX_VALUE;
        }
        final DeadlineTask earliestDeadlineTask = this.tasks.first();
        return earliestDeadlineTask.deadlineInNanos;
    }
    
    private void wakeUpDispatcher() {
        this.wakeUpCondition.signal();
    }
    
    private long executeAllTasksWhoseDeadlineHasArrived() throws InterruptedException {
        final long now = System.nanoTime();
        final Iterator<DeadlineTask> tasksIterator = this.tasks.iterator();
        while (tasksIterator.hasNext()) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final DeadlineTask task = tasksIterator.next();
            final long earliestDeadline = task.deadlineInNanos;
            if (earliestDeadline > now) {
                return earliestDeadline;
            }
            tasksIterator.remove();
            task.hasStarted = true;
            this.workersExecutor.execute(task);
        }
        return Long.MAX_VALUE;
    }
    
    private void sleepUntilDeadline(final long deadline) throws InterruptedException {
        final long durationToSleepInNanos = deadline - System.nanoTime();
        if (durationToSleepInNanos > 0L) {
            this.wakeUpCondition.awaitNanos(durationToSleepInNanos);
        }
    }
    
    public void shutdown(final long shutdownDeadlineNanoTime) {
        ExecutorServiceUtils.shutdownExecutorServiceByDeadlineUninterruptable(this.dispatcherExecutor, shutdownDeadlineNanoTime, true);
        ExecutorServiceUtils.shutdownExecutorServiceByDeadlineUninterruptable(this.workersExecutor, shutdownDeadlineNanoTime, false);
    }
    
    private static ExecutorService createDispatcherExecutor(final String threadGroupName, final String dispatcherThreadName) {
        final ThreadFactory threadFactory = createThreadFactory(threadGroupName, dispatcherThreadName);
        final ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
        return executor;
    }
    
    private static ExecutorService createWorkersExecutor(final String threadGroupName, final String workerThreadName, final int workerCount) {
        final ThreadFactory threadFactory = createThreadFactory(threadGroupName, workerThreadName);
        final ExecutorService executor = Executors.newFixedThreadPool(workerCount, threadFactory);
        return executor;
    }
    
    private static ThreadFactory createThreadFactory(final String threadGroupName, final String threadName) {
        final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
        final ThreadFactory threadFactory = new DaemonThreadFactory(new NamedThreadFactory(threadGroup, threadName));
        return threadFactory;
    }
    
    static {
        logger = Logger.getLogger(DeadlineTaskQueue.class.getName());
        nextTaskNumber = new AtomicLong();
    }
    
    private class Dispatcher implements Runnable
    {
        @Override
        public void run() {
            DeadlineTaskQueue.this.queueLock.lock();
            try {
                while (true) {
                    final long earliestRemainingDeadline = DeadlineTaskQueue.this.executeAllTasksWhoseDeadlineHasArrived();
                    DeadlineTaskQueue.this.sleepUntilDeadline(earliestRemainingDeadline);
                }
            }
            catch (InterruptedException ex2) {}
            catch (RejectedExecutionException ex) {
                DeadlineTaskQueue.logger.log(Level.WARNING, "At least one task was rejected in dispatcher thread.", ex);
            }
            catch (Throwable t) {
                DeadlineTaskQueue.logger.log(Level.SEVERE, "Unexpected exception in dispatcher thread.", t);
            }
            finally {
                DeadlineTaskQueue.this.queueLock.unlock();
            }
        }
    }
    
    public class DeadlineTask implements Comparable<DeadlineTask>, Runnable
    {
        private final Runnable runnableTask;
        private final long deadlineInNanos;
        private final long taskNumber;
        private volatile boolean hasStarted;
        
        public DeadlineTask(final Runnable runnableTask, final long deadlineInNanos, final long taskNumber) {
            this.hasStarted = false;
            this.runnableTask = runnableTask;
            this.deadlineInNanos = deadlineInNanos;
            this.taskNumber = taskNumber;
        }
        
        @Override
        public void run() {
            this.runnableTask.run();
        }
        
        public boolean cancel() {
            DeadlineTaskQueue.this.queueLock.lock();
            try {
                if (this.hasStarted) {
                    return false;
                }
                final boolean wasRemoved = DeadlineTaskQueue.this.tasks.remove(this);
                if (!wasRemoved) {
                    throw new IllegalStateException("Unable to remove task from deadline queue.");
                }
                return true;
            }
            finally {
                DeadlineTaskQueue.this.queueLock.unlock();
            }
        }
        
        @Override
        public int compareTo(final DeadlineTask other) {
            final long deadlineDiff = this.deadlineInNanos - other.deadlineInNanos;
            if (deadlineDiff < 0L) {
                return -1;
            }
            if (deadlineDiff > 0L) {
                return 1;
            }
            final long uniqueTaskNumberDiff = this.taskNumber - other.taskNumber;
            if (uniqueTaskNumberDiff < 0L) {
                return -1;
            }
            if (uniqueTaskNumberDiff > 0L) {
                return 1;
            }
            return 0;
        }
    }
}
