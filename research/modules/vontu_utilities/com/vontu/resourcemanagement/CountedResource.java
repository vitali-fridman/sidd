// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

public abstract class CountedResource extends DefaultResource
{
    private final long count;
    
    public CountedResource(final long count, final ResourceStatus status) {
        super(status);
        this.count = count;
    }
    
    protected long getCount() throws IllegalStateException {
        this.assertNotReleased();
        return this.count;
    }
}
