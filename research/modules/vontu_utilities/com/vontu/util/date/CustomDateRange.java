// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.TimeZone;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.Date;

public class CustomDateRange extends DateRange
{
    private Date _fromDate;
    private Date _toDate;
    
    public CustomDateRange(final String fromDate, final Date toDate, final DateFormat dateFormat) throws ParseException {
        this._fromDate = this.parseFromDate(fromDate, dateFormat);
        this._toDate = toDate;
    }
    
    public CustomDateRange(final String fromDate, final String toDate, final DateFormat dateFormat) throws ParseException {
        this._fromDate = this.parseFromDate(fromDate, dateFormat);
        this._toDate = this.parseToDate(toDate, dateFormat);
    }
    
    public CustomDateRange(final Date fromDate, final String toDate, final DateFormat dateFormat) throws ParseException {
        this._fromDate = fromDate;
        this._toDate = this.parseToDate(toDate, dateFormat);
    }
    
    public CustomDateRange(final String fromDate, final String toDate, final DateFormat dateFormat, final TimeZone timeZone) throws ParseException {
        super(timeZone);
        this._fromDate = this.parseFromDate(fromDate, dateFormat);
        this._toDate = this.parseToDate(toDate, dateFormat);
    }
    
    private Date parseFromDate(final String strDate, final DateFormat dateFormat) throws ParseException {
        this._calendar.setTime(dateFormat.parse(strDate));
        this._calendar.set(11, this._calendar.getMinimum(11));
        this._calendar.set(12, this._calendar.getMinimum(12));
        this._calendar.set(13, this._calendar.getMinimum(13));
        this._calendar.set(14, this._calendar.getMinimum(14));
        return this._calendar.getTime();
    }
    
    private Date parseToDate(final String strDate, final DateFormat dateFormat) throws ParseException {
        this._calendar.setTime(dateFormat.parse(strDate));
        this._calendar.set(11, this._calendar.getMaximum(11));
        this._calendar.set(12, this._calendar.getMaximum(12));
        this._calendar.set(13, this._calendar.getMaximum(13));
        this._calendar.set(14, this._calendar.getMaximum(14));
        return this._calendar.getTime();
    }
    
    @Override
    public Date getFromDate() {
        return this._fromDate;
    }
    
    @Override
    public Date getToDate() {
        return this._toDate;
    }
}
