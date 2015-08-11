// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public final class GenericHashCodeBuilder
{
    private static final int _MULTIPLIER = 37;
    private static final int _PRIME = 17;
    private int _value;
    
    public GenericHashCodeBuilder() {
        this._value = 17;
    }
    
    public GenericHashCodeBuilder append(final int member) {
        this._value = this._value * 37 + member;
        return this;
    }
    
    public GenericHashCodeBuilder append(final boolean member) {
        return this.append(member ? 1 : 0);
    }
    
    public GenericHashCodeBuilder append(final Object member) {
        return (member == null) ? this : this.append(member.hashCode());
    }
    
    public GenericHashCodeBuilder append(final long member) {
        return this.append((int)(member ^ member >>> 32));
    }
    
    public GenericHashCodeBuilder append(final float member) {
        return this.append(Float.floatToIntBits(member));
    }
    
    public GenericHashCodeBuilder append(final double member) {
        return this.append(Double.doubleToLongBits(member));
    }
    
    public int getResult() {
        return this._value;
    }
}
