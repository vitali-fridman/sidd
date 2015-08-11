// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import com.vontu.util.collection.CollectionUtil;
import com.vontu.util.collection.Converter;
import com.vontu.util.collection.Filter;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import com.vontu.util.TimeService;
import java.util.Collection;

class TimestampedObjectCollection<E>
{
    private final Collection<TimestampedObject<E>> timestampedItems;
    private final TimeService timeService;
    
    public TimestampedObjectCollection() {
        this(new TimeService());
    }
    
    TimestampedObjectCollection(final TimeService timeService) {
        this.timestampedItems = new ArrayList<TimestampedObject<E>>();
        this.timeService = timeService;
    }
    
    public synchronized void add(final E item) {
        if (this.contains(item)) {
            final String message = "Cannot add this item [" + item + "].  It is already in the " + "list.";
            throw new IllegalArgumentException(message);
        }
        final TimestampedObject<E> timestampedTargetServer = new TimestampedObject<E>(item, this.timeService);
        this.timestampedItems.add(timestampedTargetServer);
    }
    
    public synchronized Collection<E> removeItemsOlderThan(final long age, final TimeUnit timeUnit) {
        final long ageInMillis = timeUnit.toMillis(age);
        final long maximumTimestamp = this.timeService.currentTimeMillis() - ageInMillis;
        final Filter<TimestampedObject<E>> filter = new Filter<TimestampedObject<E>>() {
            @Override
            public boolean shouldAccept(final TimestampedObject<E> item) {
                return item.getTimestampInMillis() < maximumTimestamp;
            }
        };
        final Collection<TimestampedObject<E>> removedItems = this.remove(filter);
        final Converter<TimestampedObject<E>, E> converter = new Converter<TimestampedObject<E>, E>() {
            @Override
            public E convert(final TimestampedObject<E> source) {
                return source.getObject();
            }
        };
        return CollectionUtil.map(removedItems, converter);
    }
    
    public synchronized boolean contains(final E destination) {
        final Filter<TimestampedObject<E>> filter = new Filter<TimestampedObject<E>>() {
            @Override
            public boolean shouldAccept(final TimestampedObject<E> item) {
                return destination.equals(item.getObject());
            }
        };
        return CollectionUtil.contains(this.timestampedItems, filter);
    }
    
    public synchronized boolean remove(final Object object) {
        final Filter<TimestampedObject<E>> filter = new Filter<TimestampedObject<E>>() {
            @Override
            public boolean shouldAccept(final TimestampedObject<E> item) {
                return item.getObject().equals(object);
            }
        };
        return !this.remove(filter).isEmpty();
    }
    
    private Collection<TimestampedObject<E>> remove(final Filter<TimestampedObject<E>> filter) {
        return CollectionUtil.removeAll(this.timestampedItems, filter);
    }
    
    private static class TimestampedObject<O>
    {
        private final O object;
        private final long timestampInMillis;
        
        TimestampedObject(final O object, final TimeService timeService) {
            this.object = object;
            this.timestampInMillis = timeService.currentTimeMillis();
        }
        
        O getObject() {
            return this.object;
        }
        
        long getTimestampInMillis() {
            return this.timestampInMillis;
        }
    }
}
