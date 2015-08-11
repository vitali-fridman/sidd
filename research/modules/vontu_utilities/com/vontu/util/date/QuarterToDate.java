// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;

public class QuarterToDate extends DateRange
{
    public QuarterToDate() {
    }
    
    public QuarterToDate(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        final int month = this._calendar.get(2);
        final int quarterStart = month / 3 * 3;
        this._calendar.set(2, quarterStart);
        this._calendar.set(5, this._calendar.getMinimum(5));
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
