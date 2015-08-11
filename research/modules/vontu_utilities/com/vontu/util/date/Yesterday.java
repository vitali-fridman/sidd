// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class Yesterday extends DateRange
{
    public Yesterday() {
    }
    
    public Yesterday(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.add(7, -1);
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        this._calendar.setTime(this.getToToday());
        this._calendar.add(7, -1);
        return this._calendar.getTime();
    }
}
