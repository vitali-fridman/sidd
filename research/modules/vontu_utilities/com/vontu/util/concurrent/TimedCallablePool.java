// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import edu.oswego.cs.dl.util.concurrent.SynchronousChannel;
import edu.oswego.cs.dl.util.concurrent.Channel;
import edu.oswego.cs.dl.util.concurrent.Executor;
import edu.oswego.cs.dl.util.concurrent.TimeoutException;
import edu.oswego.cs.dl.util.concurrent.FutureResult;
import java.util.Iterator;
import java.util.EventObject;
import java.util.logging.Level;
import edu.oswego.cs.dl.util.concurrent.Callable;
import java.util.TimerTask;
import java.util.HashSet;
import edu.oswego.cs.dl.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public final class TimedCallablePool implements TimedCallableFactory
{
    private static final int _DEFAULT_KEEP_ALIVE_TIME = 300000;
    private static final Logger _logger;
    private static final String _DEFAULT_THREAD_NAME = "Worker";
    private final List<Worker> _availableWorkers;
    private volatile boolean _isShutdown;
    private long _keepAliveTime;
    private Collection<RunawayThreadNotificationListener> _listeners;
    private final int _MAX_THREAD_ID;
    private final int _MAX_TIMED_OUT_THREADS;
    private int _nextThreadId;
    private final Set<Worker> _timedOutWorkers;
    private final Timer _timedOutWorkersChecker;
    private final String _threadName;
    
    public TimedCallablePool(final int runawayTimeout, final int maxTimedOutThreads) {
        this(runawayTimeout, maxTimedOutThreads, "Worker");
    }
    
    public TimedCallablePool(final int runawayTimeout, final int maxTimedOutThreads, final String threadName) {
        this._availableWorkers = new ArrayList<Worker>();
        this._keepAliveTime = 300000L;
        this._listeners = (Collection<RunawayThreadNotificationListener>)new CopyOnWriteArrayList();
        this._nextThreadId = 0;
        this._timedOutWorkers = new HashSet<Worker>();
        this._timedOutWorkersChecker = new Timer(true);
        this._MAX_TIMED_OUT_THREADS = maxTimedOutThreads;
        this._MAX_THREAD_ID = this._MAX_TIMED_OUT_THREADS + 100;
        this._threadName = threadName;
        this._timedOutWorkersChecker.schedule(new RunawayThreadChecker(runawayTimeout), 0L, runawayTimeout / 2);
    }
    
    public void addListener(final RunawayThreadNotificationListener listener) {
        this._listeners.add(listener);
    }
    
    public synchronized long getKeepAliveTime() {
        return this._keepAliveTime;
    }
    
    @Override
    public Callable getTimedCallable(final Callable callable, final long timeout) {
        return (Callable)new TimedCallable(callable, timeout);
    }
    
    private Worker getWorker() {
        Worker worker = null;
        int newThreadId = 0;
        synchronized (this._availableWorkers) {
            if (this._availableWorkers.size() > 0) {
                final int preQueueSize = this._availableWorkers.size();
                try {
                    worker = this._availableWorkers.remove(this._availableWorkers.size() - 1);
                }
                catch (RuntimeException e) {
                    final String message = "Unable to remove a worker form the queue. Initial queue size [" + preQueueSize + "], Current queue size [" + this._availableWorkers.size() + "]. ";
                    TimedCallablePool._logger.log(Level.SEVERE, message, e);
                    throw e;
                }
            }
            else {
                this._nextThreadId = ((this._nextThreadId >= this._MAX_THREAD_ID) ? 1 : (this._nextThreadId + 1));
                newThreadId = this._nextThreadId;
            }
        }
        if (worker == null) {
            final String name = this._threadName + ' ' + newThreadId;
            worker = new Worker(name);
            worker.start();
            if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
                TimedCallablePool._logger.fine("New thread \"" + name + "\" was created.");
            }
        }
        else if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
            TimedCallablePool._logger.fine("Thread \"" + worker.getName() + "\" was retrieved from the pool. " + "Number of threads left in the pool: " + this._availableWorkers.size() + '.');
        }
        return worker;
    }
    
    private void notifyListeners(final EventObject eventObject) {
        final Iterator<RunawayThreadNotificationListener> iterator = this._listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().limitExceeded(eventObject);
        }
    }
    
    public void removeListener(final RunawayThreadNotificationListener listener) {
        this._listeners.remove(listener);
    }
    
    private void returnWorker(final Worker worker) {
        if (this._isShutdown) {
            return;
        }
        synchronized (this._availableWorkers) {
            if (null == worker) {
                TimedCallablePool._logger.log(Level.WARNING, "Adding null worker to available worker queue", new Exception("Adding null worker to available worker queue"));
            }
            this._availableWorkers.add(worker);
        }
        if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
            TimedCallablePool._logger.fine("Thread \"" + worker.getName() + "\" completed execution in " + String.valueOf(System.currentTimeMillis() - worker.startTimeMillis()) + " milliseconds" + " and was added to the pool of available threads." + " Number of threads in the pool: " + this._availableWorkers.size() + '.');
        }
    }
    
    public synchronized void setKeepAliveTime(final long msecs) {
        this._keepAliveTime = msecs;
    }
    
    public void shutdown() {
        this._isShutdown = true;
        synchronized (this._availableWorkers) {
            for (final Worker worker : this._availableWorkers) {
                worker.interrupt();
            }
        }
    }
    
    private void timeOutWorker(final Worker worker) {
        worker.setPriority(1);
        worker.interrupt();
        final int timedOutThreadCount;
        synchronized (this._timedOutWorkers) {
            this._timedOutWorkers.add(worker);
            timedOutThreadCount = this._timedOutWorkers.size();
        }
        if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
            TimedCallablePool._logger.fine("Thread \"" + worker.getName() + "\" is added to the list of timed out threads. " + "Number of timed out threads: " + timedOutThreadCount + '.');
        }
        if (timedOutThreadCount >= this._MAX_TIMED_OUT_THREADS) {
            TimedCallablePool._logger.warning("Maximum allowed number of timed out threads of " + this._MAX_TIMED_OUT_THREADS + " is exceeded.");
            this.notifyListeners(new EventObject(this));
        }
    }
    
    static {
        _logger = Logger.getLogger(TimedCallablePool.class.getName());
    }
    
    private final class RunawayThreadChecker extends TimerTask
    {
        private final int _timeout;
        
        RunawayThreadChecker(final int timeout) {
            this._timeout = timeout;
        }
        
        @Override
        public void run() {
            final Set<Worker> hung = new HashSet<Worker>();
            synchronized (TimedCallablePool.this._timedOutWorkers) {
                hung.addAll(TimedCallablePool.this._timedOutWorkers);
            }
            for (final Worker timedOutThread : hung) {
                final long elapsedTime = System.currentTimeMillis() - timedOutThread.startTimeMillis();
                if (elapsedTime > this._timeout) {
                    TimedCallablePool._logger.warning("A runaway thread \"" + timedOutThread.getName() + "\" is detected. It didn't finish within " + elapsedTime / 1000L + " seconds.");
                    TimedCallablePool.this.notifyListeners(new EventObject(this));
                    break;
                }
            }
        }
    }
    
    private final class TimedCallable implements Callable
    {
        private final Callable _callable;
        private final long _callMsecs;
        
        TimedCallable(final Callable callable, final long callMsecs) {
            this._callable = callable;
            this._callMsecs = callMsecs;
        }
        
        public Object call() throws Exception {
            final FutureResult result = new FutureResult();
            final Worker worker = TimedCallablePool.this.getWorker();
            boolean wasTimedOut = false;
            try {
                worker.execute(result.setter(this._callable));
                return result.timedGet(this._callMsecs);
            }
            catch (TimeoutException e) {
                wasTimedOut = true;
                if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
                    TimedCallablePool._logger.fine("The operation exceeded the allowed timeout of " + this._callMsecs + " milliseconds.");
                }
                throw e;
            }
            catch (InterruptedException e2) {
                wasTimedOut = true;
                throw e2;
            }
            finally {
                if (wasTimedOut) {
                    TimedCallablePool.this.timeOutWorker(worker);
                }
                else {
                    TimedCallablePool.this.returnWorker(worker);
                }
            }
        }
    }
    
    private final class Worker extends Thread implements Executor
    {
        private final Channel _workChannel;
        private long _startTimeMillis;
        
        public Worker(final String name) {
            super(name);
            this._workChannel = (Channel)new SynchronousChannel();
        }
        
        public void execute(final Runnable command) throws InterruptedException {
            this._workChannel.put((Object)command);
        }
        
        public synchronized long startTimeMillis() {
            return this._startTimeMillis;
        }
        
        private synchronized void setStartTime() {
            this._startTimeMillis = System.currentTimeMillis();
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    final Runnable work = (Runnable)this._workChannel.poll(TimedCallablePool.this.getKeepAliveTime());
                    if (work == null) {
                        synchronized (TimedCallablePool.this._availableWorkers) {
                            if (!TimedCallablePool.this._availableWorkers.contains(this)) {
                                continue;
                            }
                        }
                        break;
                    }
                    this.setStartTime();
                    work.run();
                    boolean wasTimedOut = false;
                    synchronized (TimedCallablePool.this._timedOutWorkers) {
                        if (TimedCallablePool.this._timedOutWorkers.remove(this)) {
                            Thread.interrupted();
                            this.setPriority(5);
                            TimedCallablePool.this.returnWorker(this);
                            wasTimedOut = true;
                        }
                    }
                    if (!wasTimedOut || !TimedCallablePool._logger.isLoggable(Level.FINE)) {
                        continue;
                    }
                    TimedCallablePool._logger.fine("Worker thread \"" + this.getName() + "\" became available and was removed from the timed out thread list.");
                }
                if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
                    TimedCallablePool._logger.fine("Worker thread \"" + this.getName() + "\" is exiting because its keep alive interval has expired.");
                }
            }
            catch (InterruptedException e2) {
                if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
                    TimedCallablePool._logger.fine("Worker thread \"" + this.getName() + "\" was interrupted. It won't process any more work.");
                }
            }
            catch (Error e) {
                TimedCallablePool._logger.log(Level.SEVERE, "System error occurred. Exiting.", e);
                System.exit(-1);
            }
            finally {
                final boolean wasRemoved;
                synchronized (TimedCallablePool.this._availableWorkers) {
                    wasRemoved = TimedCallablePool.this._availableWorkers.remove(this);
                }
                if (TimedCallablePool._logger.isLoggable(Level.FINE)) {
                    TimedCallablePool._logger.fine("Worker thread \"" + this.getName() + "\" finished." + (wasRemoved ? (" It was removed from the pool of available threads. Number of threads left in the pool: " + TimedCallablePool.this._availableWorkers.size() + '.') : ""));
                }
            }
        }
    }
    
    public interface RunawayThreadNotificationListener
    {
        void limitExceeded(EventObject p0);
    }
}
