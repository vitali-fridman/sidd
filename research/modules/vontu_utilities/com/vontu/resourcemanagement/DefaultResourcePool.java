// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import com.vontu.util.concurrent.TimeoutRuntimeException;
import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.vontu.util.TimeService;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

public abstract class DefaultResourcePool<R extends Resource, T extends ResourceRequest<R>> implements ResourcePool<R, T>
{
    private static final int INDEFINITE_WAIT = 60;
    private static final TimeUnit INDEFINITE_WAIT_UNIT;
    private static final Logger logger;
    private final AtomicBoolean shouldShutDown;
    protected final Lock resourcesLock;
    protected final Map<R, ResourceStatus> resourceStatuses;
    private final ResourceAllocationStrategy resourceAllocationStrategy;
    private final TimeService timeService;
    
    protected static Logger getLogger() {
        return DefaultResourcePool.logger;
    }
    
    protected DefaultResourcePool(final ResourceAllocationStrategy resourceAllocationStrategy, final TimeService timeService) {
        this.shouldShutDown = new AtomicBoolean(false);
        this.resourcesLock = new ReentrantLock();
        this.resourceStatuses = new HashMap<R, ResourceStatus>();
        this.resourceAllocationStrategy = resourceAllocationStrategy;
        this.timeService = timeService;
    }
    
    protected DefaultResourcePool() {
        this(new TimeService());
    }
    
    protected DefaultResourcePool(final TimeService timeService) {
        this(new AllowAllStrategy(), timeService);
    }
    
    @Override
    public void release(final R resource) throws IllegalArgumentException, ShutdownException {
        final Callable<Void> releaseResources = new ReleaseResourcesOperation((Resource)resource, this);
        this.tryToModifyResources(releaseResources);
    }
    
    public void shutDown() {
        this.shouldShutDown.getAndSet(true);
    }
    
    protected <V> V tryToModifyResources(final Callable<V> resourceModifier, final long wait, final TimeUnit waitUnit) throws InterruptedException {
        this.lockResources(wait, waitUnit);
        try {
            return this.tryToModifyResourcesImpl(resourceModifier);
        }
        finally {
            this.unlockResources();
        }
    }
    
    protected <V> V tryToModifyResources(final Callable<V> resourceModifier) {
        this.lockResources();
        try {
            return (V)this.tryToModifyResourcesImpl((Callable<Object>)resourceModifier);
        }
        finally {
            this.unlockResources();
        }
    }
    
    private <V> V tryToModifyResourcesImpl(final Callable<V> resourceModifier) {
        this.assertNotShuttingDown();
        try {
            final V result = resourceModifier.call();
            this.assertNotShuttingDown();
            return result;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
    
    protected void handleRelease(final R resource) throws InterruptedException {
    }
    
    protected final void unlockResources() {
        this.resourcesLock.unlock();
    }
    
    protected void registerResource(final R resource, final ResourceStatus status) {
        final ResourceStatus priorValue = this.resourceStatuses.put(resource, status);
        if (priorValue != null) {
            final String message = "A prior value [" + priorValue + "] was found for a " + "new resource.";
            throw new IllegalStateException(message);
        }
    }
    
    protected long getTimeToWaitInMillis(final long endTimeInMillis) {
        return endTimeInMillis - this.getCurrentTimeInMillis();
    }
    
    protected long getEndTimeInMillis(final long maximumWait, final TimeUnit maximumWaitUnit) {
        return this.getCurrentTimeInMillis() + maximumWaitUnit.toMillis(maximumWait);
    }
    
    protected ResourceAllocationStrategy getResourceAllocationStrategy() {
        return this.resourceAllocationStrategy;
    }
    
    protected void assertNotShuttingDown() {
        if (this.shouldShutDown()) {
            throw new ShutdownException();
        }
    }
    
    private long getCurrentTimeInMillis() {
        return this.timeService.currentTimeMillis();
    }
    
    private boolean shouldShutDown() {
        return this.shouldShutDown.get();
    }
    
    protected void lockResources(final long maximumWait, final TimeUnit maximumWaitUnit) throws InterruptedException, TimeoutRuntimeException {
        final boolean lockAcquired = this.resourcesLock.tryLock(maximumWait, maximumWaitUnit);
        if (!lockAcquired) {
            final String conditionMessage = "the resource lock could be acquired";
            throw new TimeoutRuntimeException(maximumWait, maximumWaitUnit, conditionMessage);
        }
    }
    
    protected void lockResources() {
        this.resourcesLock.lock();
    }
    
    static {
        INDEFINITE_WAIT_UNIT = TimeUnit.SECONDS;
        logger = Logger.getLogger(DefaultResourcePool.class.getName());
    }
    
    private static final class ReleaseResourcesOperation<E extends Resource, Q extends ResourceRequest<E>> implements Callable<Void>
    {
        private final E resource;
        private final DefaultResourcePool<E, Q> pool;
        
        private ReleaseResourcesOperation(final E resource, final DefaultResourcePool<E, Q> pool) {
            this.resource = resource;
            this.pool = pool;
        }
        
        @Override
        public Void call() throws InterruptedException {
            DefaultResourcePool.getLogger().fine("Trying to release resource [" + this.resource + "].");
            final ResourceStatus resourceStatus = this.pool.resourceStatuses.remove(this.resource);
            if (resourceStatus == null) {
                final String message = "This resource [" + this.resource + "] is not being " + "tracked by this pool so it cannot be released by " + "this pool.";
                throw new IllegalArgumentException(message);
            }
            this.pool.handleRelease(this.resource);
            resourceStatus.setReleased();
            DefaultResourcePool.getLogger().fine("Resoruce [" + this.resource + "] was released.");
            return null;
        }
    }
}
