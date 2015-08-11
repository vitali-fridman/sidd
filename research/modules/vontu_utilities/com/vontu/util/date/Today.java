// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class Today extends DateRange
{
    public Today() {
    }
    
    public Today(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        return this.getFromToday();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
