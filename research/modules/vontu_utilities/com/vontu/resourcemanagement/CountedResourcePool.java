// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import com.vontu.util.concurrent.TimeoutRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CountedResourcePool<R extends CountedResource, T extends CountedResourceRequest<R>> extends DefaultResourcePool<R, T>
{
    private final AtomicLong count;
    private final Condition released;
    private final long maxCount;
    
    public CountedResourcePool(final long maxCount) {
        this.maxCount = maxCount;
        this.count = new AtomicLong(maxCount);
        this.released = this.resourcesLock.newCondition();
    }
    
    @Override
    public R tryAcquire(final T request, final long maximumWait, final TimeUnit maximumWaitUnit) throws TimeoutRuntimeException, InterruptedException, ShutdownException {
        final long endTimeInMillis = this.getEndTimeInMillis(maximumWait, maximumWaitUnit);
        this.assertNotShuttingDown();
        DefaultResourcePool.getLogger().fine("Trying to acquire counted resource for request [" + request + "].");
        this.lockResources(this.getTimeToWaitInMillis(endTimeInMillis), TimeUnit.MILLISECONDS);
        try {
            final long size = request.getCount();
            while (!this.isAvailable(size)) {
                final long releaseWait = this.getTimeToWaitInMillis(endTimeInMillis);
                final TimeUnit releaseWaitUnit = TimeUnit.MILLISECONDS;
                if (!this.released.await(releaseWait, releaseWaitUnit)) {
                    final String conditionMessage = "any counted resources were released";
                    throw new TimeoutRuntimeException(releaseWait, releaseWaitUnit, conditionMessage);
                }
            }
            return this.acquireImpl(size);
        }
        finally {
            this.unlockResources();
        }
    }
    
    private R acquireImpl(final long count) {
        this.allocate(count);
        try {
            final ResourceStatus status = new ResourceStatus();
            final R resource = this.createResource(count, status);
            this.registerResource(resource, status);
            return resource;
        }
        catch (RuntimeException e) {
            this.deallocate(count);
            throw e;
        }
    }
    
    protected abstract R createResource(final long p0, final ResourceStatus p1);
    
    @Override
    protected void handleRelease(final CountedResource resource) throws InterruptedException {
        this.deallocate(resource.getCount());
        this.released.signalAll();
    }
    
    private void deallocate(final long decrement) {
        if (this.count.get() + decrement > this.maxCount) {
            this.count.set(this.maxCount);
            return;
        }
        this.count.getAndAdd(decrement);
    }
    
    private void allocate(final long increment) throws IllegalArgumentException {
        if (this.count.get() < increment) {
            final String message = "Cannot allocate more than the pool size.";
            throw new IllegalArgumentException(message);
        }
        this.count.getAndAdd(-increment);
    }
    
    private boolean isAvailable(final long size) {
        return this.count.get() >= size;
    }
    
    protected long getMaxCount() {
        return this.maxCount;
    }
}
