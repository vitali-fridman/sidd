// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.vontu.util.TimeService;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class ProgressiveBackoff
{
    private static final Logger _logger;
    private static final double RATIO = 1.5;
    private final Callable<Boolean> _callable;
    private final Progression _progression;
    private TimeService _timeService;
    
    public ProgressiveBackoff(final Callable<Boolean> callable, final int scaleFactor) {
        this(callable, scaleFactor, Integer.MAX_VALUE);
    }
    
    public ProgressiveBackoff(final Callable<Boolean> callable, final int scaleFactor, final int maxInterval) {
        this(callable, scaleFactor, maxInterval, new TimeService());
    }
    
    ProgressiveBackoff(final Callable<Boolean> callable, final int scaleFactor, final int maxInterval, final TimeService timeService) {
        if (scaleFactor < 0) {
            throw new IllegalArgumentException("Initial wait can't be negative.");
        }
        this._callable = callable;
        this._progression = new Progression(scaleFactor, 1.5, maxInterval);
        this._timeService = timeService;
    }
    
    public void call() throws InterruptedException, InvocationTargetException {
        long wait = this.getNextWait();
        long attempt = 2L;
        while (this.notComplete()) {
            if (ProgressiveBackoff._logger.isLoggable(Level.FINE)) {
                ProgressiveBackoff._logger.log(Level.FINE, "Excecuting {0}, attempt {1} delay {2}.", new Object[] { this._callable.getClass().getName(), attempt++, wait });
            }
            this._timeService.sleep(wait);
            wait = this.getNextWait();
        }
    }
    
    public boolean call(final int timeout) throws InterruptedException, InvocationTargetException {
        final long intialWait = this.getNextWait();
        if (timeout < intialWait) {
            throw new IllegalArgumentException("Timeout can't be less than initial wait.");
        }
        final long startTime = this._timeService.currentTimeMillis();
        final long abortTime = startTime + timeout;
        long wait = intialWait;
        long attempt = 2L;
        while (this.notComplete()) {
            if (wait <= 0L) {
                if (ProgressiveBackoff._logger.isLoggable(Level.FINE)) {
                    final long endtime = this._timeService.currentTimeMillis() - startTime;
                    ProgressiveBackoff._logger.log(Level.FINE, "{0} timed out after {1} attempts and {2}ms", new Object[] { this._callable.getClass().getName(), attempt, endtime });
                }
                return false;
            }
            if (ProgressiveBackoff._logger.isLoggable(Level.FINE)) {
                ProgressiveBackoff._logger.log(Level.FINE, "Excecuting {0}, attempt {1} delay {2}.", new Object[] { this._callable.getClass().getName(), attempt++, wait });
            }
            this._timeService.sleep(wait);
            final long now = this._timeService.currentTimeMillis();
            final long nextWait = this.getNextWait();
            wait = ((now + nextWait > abortTime) ? (abortTime - now) : nextWait);
        }
        return true;
    }
    
    private boolean notComplete() throws InvocationTargetException {
        try {
            return !this._callable.call();
        }
        catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }
    
    private long getNextWait() {
        return this._progression.getNext();
    }
    
    static {
        _logger = Logger.getLogger(ProgressiveBackoff.class.getName());
    }
}
