// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.text.ParseException;
import java.util.Date;

public class CustomTimestampRange extends DateRange
{
    public static final String DEFAULT_DATE_FORMAT = "M/d/yy '-' h:mm:s:S a";
    private Date _fromDate;
    private Date _toDate;
    
    public CustomTimestampRange(final String fromDate, final String toDate) throws ParseException {
        this.initializeDateRange(fromDate, toDate);
    }
    
    public CustomTimestampRange(final String fromDate, final String toDate, final TimeZone timeZone) throws ParseException {
        super(timeZone);
        this.initializeDateRange(fromDate, toDate);
    }
    
    private void initializeDateRange(final String fromDate, final String toDate) throws ParseException {
        final SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy '-' h:mm:s:S a");
        this._calendar.setTime(formatter.parse(fromDate));
        this._fromDate = this._calendar.getTime();
        final Date temp = formatter.parse(toDate);
        this._calendar.setTime(temp);
        this._toDate = this._calendar.getTime();
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
