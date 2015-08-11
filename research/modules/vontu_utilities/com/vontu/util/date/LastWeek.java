// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class LastWeek extends DateRange
{
    public LastWeek() {
        this._calendar.setFirstDayOfWeek(2);
    }
    
    public LastWeek(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.add(5, -7);
        this._calendar.set(7, this._calendar.getFirstDayOfWeek());
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        this._calendar.setTime(this.getToToday());
        this._calendar.set(7, this._calendar.getFirstDayOfWeek());
        this._calendar.add(5, -1);
        return this._calendar.getTime();
    }
}
