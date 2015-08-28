// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

public enum StatusUpdateResult
{
    SUCCESS(0), 
    UNKNOWN_MONITOR(-1);
    
    private final int _value;
    
    private StatusUpdateResult(final int value) {
        this._value = value;
    }
    
    public int value() {
        return this._value;
    }
}
