// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.software;

public enum ProtectComponent
{
    MONITOR(1, "Monitor"), 
    MANAGER(2, "Manager"), 
    SINGLETIER(3, "Single-tier");
    
    private final int _value;
    private final String _description;
    
    private ProtectComponent(final int value, final String description) {
        this._value = value;
        this._description = description;
    }
    
    public int intValue() {
        return this._value;
    }
    
    public String description() {
        return this._description;
    }
}
