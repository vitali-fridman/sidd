// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

public class AllowAllStrategy implements ResourceAllocationStrategy
{
    @Override
    public int hashCode() {
        return AllowAllStrategy.class.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof AllowAllStrategy;
    }
}
