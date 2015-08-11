// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.config;

public class ConstantValueProvider<T> implements ConfigValueProvider<T>
{
    private final T value;
    
    public ConstantValueProvider(final T value) {
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return this.value;
    }
}
