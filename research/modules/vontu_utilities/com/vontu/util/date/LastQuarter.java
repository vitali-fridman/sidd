// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Date;
import com.vontu.util.TimeService;
import java.util.TimeZone;

public class LastQuarter extends DateRange
{
    public LastQuarter() {
    }
    
    public LastQuarter(final TimeZone timeZone) {
        super(timeZone);
    }
    
    public LastQuarter(final TimeZone timeZone, final TimeService timeService) {
        super(timeZone, timeService);
    }
    
    @Override
    public Date getFromDate() {
        this._calendar.setTime(this.getFromToday());
        final int currentMonth = this._calendar.get(2);
        final int lastQuarterDiff = currentMonth % 3 + 3;
        this._calendar.add(2, -lastQuarterDiff);
        this._calendar.set(5, this._calendar.getActualMinimum(5));
        return this._calendar.getTime();
    }
    
    @Override
    public Date getToDate() {
        this._calendar.setTime(this.getToToday());
        final int currentMonth = this._calendar.get(2);
        final int lastQuarterDiff = currentMonth % 3 + 1;
        this._calendar.add(2, -lastQuarterDiff);
        this._calendar.set(5, this._calendar.getActualMaximum(5));
        return this._calendar.getTime();
    }
}
