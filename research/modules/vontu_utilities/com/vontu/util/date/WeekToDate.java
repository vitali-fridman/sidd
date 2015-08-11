// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class WeekToDate extends DateRange
{
    public WeekToDate() {
        this._calendar.setFirstDayOfWeek(2);
    }
    
    public WeekToDate(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.set(7, this._calendar.getFirstDayOfWeek());
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
