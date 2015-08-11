// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class CollectionFilter
{
    public static <T> List<T> applyFilter(final Collection<T> src, final Filter<T> criteria) {
        final List<T> dest = new ArrayList<T>();
        for (final T dp : src) {
            if (criteria.shouldAccept(dp)) {
                dest.add(dp);
            }
        }
        return dest;
    }
}
