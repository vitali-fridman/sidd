// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class DefaultResourceRequest<R extends Resource> implements ResourceRequest<R>
{
    protected final Object consumer;
    protected final String explanation;
    
    protected DefaultResourceRequest(final Object consumer, final String explanation) {
        this.consumer = consumer;
        this.explanation = explanation;
    }
    
    @Override
    public Object getConsumer() {
        return this.consumer;
    }
    
    @Override
    public String getExplanation() {
        return this.explanation;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.consumer).append((Object)this.explanation).toHashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof DefaultResourceRequest)) {
            return false;
        }
        final DefaultResourceRequest<?> request = (DefaultResourceRequest<?>)o;
        return new EqualsBuilder().append(this.consumer, request.consumer).append((Object)this.explanation, (Object)request.explanation).isEquals();
    }
}
