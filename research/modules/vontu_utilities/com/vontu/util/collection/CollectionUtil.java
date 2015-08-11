// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import com.vontu.util.convert.StringConvertor;
import java.util.Collection;
import java.util.Iterator;

public class CollectionUtil
{
    public static int collectionSize(final Iterator<?> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }
        return count;
    }
    
    public static <T> Collection<T> addAll(final Collection<T> destination, final Iterator<? extends T> sourceIterator) {
        while (sourceIterator.hasNext()) {
            destination.add((T)sourceIterator.next());
        }
        return destination;
    }
    
    public static <T> Collection<String> addAllAsString(final Collection<String> destination, final Iterator<T> sourceIterator, final StringConvertor<? super T> stringConvertor) {
        while (sourceIterator.hasNext()) {
            destination.add(stringConvertor.convertToString((T)sourceIterator.next()));
        }
        return destination;
    }
    
    public static <T> boolean contains(final Iterator<? super T> iter, final T o) {
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                return true;
            }
        }
        return false;
    }
    
    public static void removeAll(final Iterator<?> iter) {
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }
    
    public static <T> Iterator<T> sortedIterator(final Iterator<T> iterator, final Comparator<? super T> comparator) {
        final List<T> sortedList = new ArrayList<T>();
        addAll(sortedList, (Iterator<? extends T>)iterator);
        Collections.sort(sortedList, comparator);
        return sortedList.iterator();
    }
    
    public static <T> ArrayList<T> createArrayList(final Iterator<T> iterator) {
        return (ArrayList<T>)(ArrayList)addAll(new ArrayList<T>(), (Iterator<? extends T>)iterator);
    }
    
    public static <T> Set<T> createHashSet(final Iterator<T> iterator) {
        return (Set<T>)(Set)addAll(new HashSet<T>(), (Iterator<? extends T>)iterator);
    }
    
    public static void materializeCollection(final Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
    
    public static <T, Q> List<T> map(final Collection<? extends Q> collection, final Converter<Q, T> converter) {
        final List<T> result = new ArrayList<T>(collection.size());
        for (final Q item : collection) {
            final T newItem = converter.convert(item);
            if (newItem != null) {
                result.add(newItem);
            }
        }
        return result;
    }
    
    public static <T, Q> List<T> map(final Iterator<? extends Q> iterator, final Converter<Q, T> converter) {
        final List<T> result = new ArrayList<T>();
        for (final Q item : Iterators.toIterable(iterator)) {
            final T newItem = converter.convert(item);
            if (newItem != null) {
                result.add(newItem);
            }
        }
        return result;
    }
    
    public static <T> List<T> filter(final Collection<T> collection, final Filter<? super T> filter) {
        final List<T> result = new ArrayList<T>(collection.size());
        addAll(result, (Iterator<? extends T>)new FilteringIterator<T>((Iterator<T>)collection.iterator(), filter));
        return result;
    }
    
    public static <T> boolean contains(final Collection<T> collection, final Filter<? super T> filter) {
        for (final T element : collection) {
            if (filter.shouldAccept((T)element)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> Collection<T> removeAll(final Collection<T> collection, final Filter<T> filter) {
        final List<T> itemsToRemove = filter(collection, filter);
        if (itemsToRemove.isEmpty()) {
            return (Collection<T>)Collections.emptyList();
        }
        if (collection.removeAll(itemsToRemove)) {
            return itemsToRemove;
        }
        return (Collection<T>)Collections.emptyList();
    }
    
    public static <K, V> Collection<V> removeAll(final Collection<K> keysToRemove, final Map<K, V> map) {
        final Converter<K, V> converter = new Converter<K, V>() {
            @Override
            public V convert(final K key) {
                return map.remove(key);
            }
        };
        return map((Collection<? extends K>)keysToRemove, converter);
    }
    
    public static <Z, X> Z foldLeft(final Combiner<Z, X> f, final Z initialValue, final Collection<X> collection) {
        Z result = initialValue;
        for (final X item : collection) {
            result = f.combine(result, item);
        }
        return result;
    }
}
