// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import com.vontu.util.observer.NullNotifier;
import com.vontu.util.observer.Observable;
import com.vontu.util.observer.Notifier;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class TimedThreadPoolExecutor<T extends TimedRunnable> extends ThreadPoolExecutor
{
    private TimerQueue timerQueue;
    private final ExecutorObservable<ExecutorObserver<T>> executorObservable;
    
    public TimedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.timerQueue = null;
        this.executorObservable = new ExecutorObservable<ExecutorObserver<T>>();
    }
    
    public void addObserver(final ExecutorObserver<T> observer) {
        this.executorObservable.addObserver(observer);
    }
    
    @Override
    public void execute(final Runnable command) {
        this.assertCommandIsValid(command);
        super.execute(command);
    }
    
    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        final T timedRunnable = this.cast(r);
        timedRunnable.finished();
        this.timerQueue.removeTimerTask(timedRunnable.getTimerTask());
        if (t != null) {
            timedRunnable.reportException(t);
        }
        Thread.interrupted();
        final Notifier<ExecutorObserver<T>> notifier = new AfterExecuteNotifier<T>(t, (T)timedRunnable);
        this.executorObservable.executeForEachObserver(notifier);
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        final T timedRunnable = this.cast(r);
        if (this.timerQueue == null) {
            this.timerQueue = new TimerQueue("TimedThreadPool Task Runaway Watcher");
        }
        this.timerQueue.addTimerExpiringIn(timedRunnable.getTimerTask(), timedRunnable.getTimeout(), TimeUnit.MILLISECONDS);
        timedRunnable.started();
        super.beforeExecute(t, r);
    }
    
    private void assertCommandIsValid(final Runnable command) {
        if (!(command instanceof TimedRunnable)) {
            final String message = "A runnable passed to TimedThreadPoolExecutor#execute must be a subclass of type " + TimedRunnable.class.getName();
            throw new IllegalArgumentException(message);
        }
    }
    
    private T cast(final Runnable command) {
        this.assertCommandIsValid(command);
        return (T)command;
    }
    
    private static final class AfterExecuteNotifier<T2 extends TimedRunnable> implements Notifier<ExecutorObserver<T2>>
    {
        private final Throwable t;
        private final T2 timedRunnable;
        
        private AfterExecuteNotifier(final Throwable t, final T2 timedRunnable) {
            this.t = t;
            this.timedRunnable = timedRunnable;
        }
        
        @Override
        public void notify(final ExecutorObserver<T2> observer) {
            observer.afterExecute(this.timedRunnable, this.t);
        }
    }
    
    private static final class ExecutorObservable<E extends ExecutorObserver<?>> extends Observable<E>
    {
        private final Notifier<E> nullNotifier;
        
        ExecutorObservable() {
            super(ObserverPolicy.DUPLICATE_OBSERVERS_NOT_ALLOWED);
            this.nullNotifier = new NullNotifier<E>();
        }
        
        @Override
        protected Notifier<E> getNewObserverNotifier() {
            return this.nullNotifier;
        }
        
        public void executeForEachObserver(final Notifier<E> notifier) {
            super.executeForEachObserver(notifier);
        }
    }
}
