// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Properties;

public class OrderedProperties extends Properties
{
    private final Set<Object> _keys;
    
    public OrderedProperties() {
        this._keys = new LinkedHashSet<Object>();
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        this._keys.add(key);
        return super.put(key, value);
    }
    
    @Override
    public Object remove(final Object key) {
        this._keys.remove(key);
        return super.remove(key);
    }
    
    @Override
    public Set<Object> keySet() {
        return this._keys;
    }
}
