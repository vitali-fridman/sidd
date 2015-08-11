// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.logging.Level;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import java.util.concurrent.LinkedBlockingQueue;

public class OffliningBlockingQueue<E> extends LinkedBlockingQueue<E>
{
    private static final long serialVersionUID = -7686805690713893841L;
    private static final Logger logger;
    private static final ThreadFactory threadFactory;
    private final TimestampedObjectCollection<E> offlineItems;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long defaultMaxAge;
    private final TimeUnit defaultMaxAgeTimeUnit;
    
    public OffliningBlockingQueue() {
        this(1L, TimeUnit.MINUTES, null);
    }
    
    public OffliningBlockingQueue(final long maximumOfflineItemAge, final TimeUnit maximumAgeTimeUnit, final long restoreInterval, final TimeUnit restoreIntervalTimeUnit) {
        this(maximumOfflineItemAge, maximumAgeTimeUnit, Executors.newSingleThreadScheduledExecutor(OffliningBlockingQueue.threadFactory));
        final Runnable task = this.createOfflineItemRestoreTask();
        this.scheduledExecutorService.scheduleWithFixedDelay(task, 0L, restoreInterval, restoreIntervalTimeUnit);
    }
    
    OffliningBlockingQueue(final TimestampedObjectCollection<E> offlineItems, final ScheduledExecutorService scheduledExecutorService, final long defaultMaxAge, final TimeUnit defaultMaxAgeTimeUnit) {
        if (defaultMaxAge < 0L) {
            final String message = "Default max age [" + defaultMaxAge + "] cannot be less than 0.";
            throw new IllegalArgumentException(message);
        }
        if (defaultMaxAgeTimeUnit == null) {
            throw new IllegalArgumentException("Time unit cannot be null.");
        }
        this.offlineItems = offlineItems;
        this.scheduledExecutorService = scheduledExecutorService;
        this.defaultMaxAge = defaultMaxAge;
        this.defaultMaxAgeTimeUnit = defaultMaxAgeTimeUnit;
    }
    
    private OffliningBlockingQueue(final long maximumOfflineItemAge, final TimeUnit maximumAgeTimeUnit, final ScheduledExecutorService scheduledExecutorService) {
        this((TimestampedObjectCollection)new TimestampedObjectCollection(), scheduledExecutorService, maximumOfflineItemAge, maximumAgeTimeUnit);
    }
    
    public boolean isOnline(final E destination) {
        return this.contains(destination);
    }
    
    public boolean isOffline(final E item) {
        return this.offlineItems.contains(item);
    }
    
    public void takeOffline(final E item) {
        if (this.removeFromOnlineItems(item)) {
            this.offlineItems.add(item);
        }
    }
    
    public void restoreOfflineItemsOlderThan(final long age, final TimeUnit timeUnit) {
        final Collection<E> itemsToRestore = this.offlineItems.removeItemsOlderThan(age, timeUnit);
        OffliningBlockingQueue.logger.finer("Restoring " + itemsToRestore.size() + " offline items.");
        if (itemsToRestore.isEmpty()) {
            return;
        }
        for (final E item : itemsToRestore) {
            if (this.contains(item)) {
                final String message = "Cannot restore destination " + item + ".  It is already on the online queue.";
                OffliningBlockingQueue.logger.warning(message);
            }
            else {
                this.add(item);
            }
        }
    }
    
    @Override
    public boolean remove(final Object object) {
        final boolean onlineItemsModified = this.removeFromOnlineItems(object);
        final boolean offlineItemsModified = this.offlineItems.remove(object);
        return onlineItemsModified || offlineItemsModified;
    }
    
    public void shutDown() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.shutDown();
        }
        finally {
            super.finalize();
        }
    }
    
    private boolean removeFromOnlineItems(final Object object) {
        return super.remove(object);
    }
    
    private Runnable createOfflineItemRestoreTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    OffliningBlockingQueue.this.restoreOldOfflineItems();
                }
                catch (Throwable t) {
                    final String message = "An unexpected error occurred while trying to restore failed target servers.";
                    OffliningBlockingQueue.logger.log(Level.WARNING, message, t);
                }
            }
        };
    }
    
    private void restoreOldOfflineItems() {
        this.restoreOfflineItemsOlderThan(this.defaultMaxAge, this.defaultMaxAgeTimeUnit);
    }
    
    static {
        logger = Logger.getLogger(OffliningBlockingQueue.class.getName());
        threadFactory = new DaemonThreadFactory(new NamedThreadFactory(OffliningBlockingQueue.class.getSimpleName()));
    }
}
