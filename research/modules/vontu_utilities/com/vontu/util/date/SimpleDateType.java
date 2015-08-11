// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

public enum SimpleDateType
{
    SHORT(3), 
    MEDIUM(2), 
    LONG(1), 
    FULL(0), 
    SHORT_YYYY(3);
    
    private final int baseDateFormatType;
    
    private SimpleDateType(final int dateFormatType) {
        this.baseDateFormatType = dateFormatType;
    }
    
    public int getBaseDateFormatType() {
        return this.baseDateFormatType;
    }
}
