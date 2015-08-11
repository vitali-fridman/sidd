// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class InformationSize
{
    private final long count;
    private final InformationUnit unit;
    
    public InformationSize(final long count, final InformationUnit unit) {
        this.count = count;
        this.unit = unit;
    }
    
    long getCount() {
        return this.count;
    }
    
    InformationUnit getUnit() {
        return this.unit;
    }
    
    public long getSizeInBytes() {
        return InformationUnit.BYTES.convert(this.count, this.unit);
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.count).append((Object)this.unit).toHashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof InformationSize)) {
            return false;
        }
        final InformationSize size = (InformationSize)object;
        return new EqualsBuilder().append(this.count, size.count).append((Object)this.unit, (Object)size.unit).isEquals();
    }
}
