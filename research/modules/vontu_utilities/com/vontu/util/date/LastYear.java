// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class LastYear extends DateRange
{
    public LastYear() {
    }
    
    public LastYear(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.set(2, this._calendar.getMinimum(2));
        this._calendar.set(5, this._calendar.getMinimum(5));
        this._calendar.add(1, -1);
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        this._calendar.setTime(this.getToToday());
        this._calendar.set(2, this._calendar.getMaximum(2));
        this._calendar.set(5, this._calendar.getMaximum(5));
        this._calendar.add(1, -1);
        return this._calendar.getTime();
    }
}
