// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import com.vontu.util.TimeService;
import java.util.TimeZone;

public class LastXDays extends DateRange
{
    private int days;
    
    public LastXDays(final int days) {
        this.days = days;
    }
    
    public LastXDays(final TimeZone timeZone, final int days) {
        super(timeZone);
        this.days = days;
    }
    
    public LastXDays(final TimeZone timeZone, final TimeService timeService, final int days) {
        super(timeZone, timeService);
        this.days = days;
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        this._calendar.add(5, (this.days - 1) * -1);
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        return this.getToToday();
    }
}
