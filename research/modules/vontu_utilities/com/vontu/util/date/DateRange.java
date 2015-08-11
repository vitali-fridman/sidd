// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import java.util.TimeZone;
import com.vontu.util.TimeService;
import java.util.Calendar;

public abstract class DateRange
{
    protected Calendar _calendar;
    private final TimeService timeService;
    
    public DateRange() {
        this(TimeZone.getDefault(), new TimeService());
    }
    
    public DateRange(final TimeZone timeZone) {
        this(timeZone, new TimeService());
    }
    
    public DateRange(final TimeZone timeZone, final TimeService timeService) {
        this._calendar = Calendar.getInstance(timeZone);
        this.timeService = timeService;
    }
    
    public abstract Date getFromDate();
    
    public abstract Date getToDate();
    
    protected Date getFromToday() {
        this._calendar.setTime(new Date(this.timeService.currentTimeMillis()));
        this._calendar.set(11, this._calendar.getMinimum(11));
        this._calendar.set(12, this._calendar.getMinimum(12));
        this._calendar.set(13, this._calendar.getMinimum(13));
        this._calendar.set(14, this._calendar.getMinimum(14));
        return this._calendar.getTime();
    }
    
    protected Date getToToday() {
        this._calendar.setTime(new Date(this.timeService.currentTimeMillis()));
        this._calendar.set(11, this._calendar.getMaximum(11));
        this._calendar.set(12, this._calendar.getMaximum(12));
        this._calendar.set(13, this._calendar.getMaximum(13));
        this._calendar.set(14, this._calendar.getMaximum(14));
        return this._calendar.getTime();
    }
}
