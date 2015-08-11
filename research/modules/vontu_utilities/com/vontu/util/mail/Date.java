// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.DateTime;

public class Date
{
    private boolean mIsNull;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private int mZone;
    
    public Date() {
        this.mIsNull = true;
        this.mYear = 1970;
        this.mMonth = 1;
        this.mDay = 1;
        this.mHour = 0;
        this.mMinute = 0;
        this.mSecond = 0;
        this.mZone = 0;
    }
    
    public Date(final java.util.Date date, final boolean convertToLocal) {
        final long t = date.getTime();
        final DateTime dt = new DateTime();
        dt.fromSystemTime(t, convertToLocal);
        this.mIsNull = false;
        this.mYear = dt.getYear();
        this.mMonth = dt.getMonth();
        this.mDay = dt.getDay();
        this.mHour = dt.getHour();
        this.mMinute = dt.getMinute();
        this.mSecond = dt.getSecond();
        this.mZone = dt.getZone();
    }
    
    public Date(final int year, final int month, final int day, final int hour, final int minute, final int second) {
        this.mIsNull = false;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = hour;
        this.mMinute = minute;
        this.mSecond = second;
        this.mZone = 0;
    }
    
    public Date(final int year, final int month, final int day, final int hour, final int minute, final int second, final int zone) {
        this.mIsNull = false;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = hour;
        this.mMinute = minute;
        this.mSecond = second;
        this.mZone = zone;
    }
    
    public boolean isNull() {
        return this.mIsNull;
    }
    
    public int getYear() {
        return this.mYear;
    }
    
    public int getMonth() {
        return this.mMonth;
    }
    
    public int getDay() {
        return this.mDay;
    }
    
    public int getHour() {
        return this.mHour;
    }
    
    public int getMinute() {
        return this.mMinute;
    }
    
    public int getSecond() {
        return this.mSecond;
    }
    
    public int getZone() {
        return this.mZone;
    }
    
    public String getDisplayString() {
        final StringBuffer buf = new StringBuffer(16);
        buf.append((char)(this.mYear / 1000 % 10 + 48));
        buf.append((char)(this.mYear / 100 % 10 + 48));
        buf.append((char)(this.mYear / 10 % 10 + 48));
        buf.append((char)(this.mYear / 1 % 10 + 48));
        buf.append('-');
        buf.append((char)(this.mMonth / 10 % 10 + 48));
        buf.append((char)(this.mMonth / 1 % 10 + 48));
        buf.append('-');
        buf.append((char)(this.mDay / 10 % 10 + 48));
        buf.append((char)(this.mDay / 1 % 10 + 48));
        return buf.toString();
    }
}
