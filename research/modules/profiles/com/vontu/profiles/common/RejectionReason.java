// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

public enum RejectionReason
{
    FILE_EXISTS(-1), 
    DELETE_FAILED(-2), 
    RENAME_FAILED(-3);
    
    private final int _value;
    
    private RejectionReason(final int value) {
        this._value = value;
    }
    
    public int value() {
        return this._value;
    }
}
