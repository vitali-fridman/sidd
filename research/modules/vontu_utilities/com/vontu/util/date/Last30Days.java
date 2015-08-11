// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class Last30Days extends DateRange
{
    public Last30Days() {
    }
    
    public Last30Days(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.add(5, -29);
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
