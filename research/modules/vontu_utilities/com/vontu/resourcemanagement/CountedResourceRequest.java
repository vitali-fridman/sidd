// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class CountedResourceRequest<T extends CountedResource> extends DefaultResourceRequest<T>
{
    private final long count;
    
    public CountedResourceRequest(final long count, final Object consumer, final String explanation) {
        super(consumer, explanation);
        this.count = count;
    }
    
    protected long getCount() {
        return this.count;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.count).toHashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof CountedResourceRequest)) {
            return false;
        }
        final CountedResourceRequest request = (CountedResourceRequest)o;
        return new EqualsBuilder().appendSuper(super.equals(request)).append(this.count, request.count).isEquals();
    }
}
