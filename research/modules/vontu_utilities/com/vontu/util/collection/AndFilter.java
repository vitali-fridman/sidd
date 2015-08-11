// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public class AndFilter<I> implements Filter<I>
{
    private final Filter<I>[] filters;
    
    public AndFilter(final Filter<I>... filters) {
        this.filters = filters;
    }
    
    @Override
    public boolean shouldAccept(final I item) {
        for (final Filter<I> filter : this.filters) {
            if (!filter.shouldAccept(item)) {
                return false;
            }
        }
        return true;
    }
}
