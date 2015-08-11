// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class YearToDate extends DateRange
{
    public YearToDate() {
    }
    
    public YearToDate(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.set(5, this._calendar.getMinimum(5));
        this._calendar.set(2, this._calendar.getMinimum(2));
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
