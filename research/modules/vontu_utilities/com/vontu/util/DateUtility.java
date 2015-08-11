// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class DateUtility
{
    private static final long YEAR_1900;
    private static final Logger _logger;
    
    public static Timestamp parseDate(final String dateString) throws ParseException {
        final SimpleDateFormat format = (SimpleDateFormat)DateFormat.getDateInstance(3);
        return new Timestamp(format.parse(dateString).getTime());
    }
    
    public static Date parseEmailDate(final String dateStr) throws ParseException {
        final SimpleDateFormat format = (SimpleDateFormat)DateFormat.getDateInstance();
        format.applyPattern("EEE, dd MMM yy HH:mm:ss Z");
        final Date date = format.parse(dateStr);
        final Date localDate = new Date(date.getTime());
        return localDate;
    }
    
    public static Date failSafeParseEmailDate(final String sentDateStr) {
        Date sentDate;
        if (sentDateStr != null && sentDateStr.length() > 0) {
            try {
                sentDate = parseEmailDate(sentDateStr);
            }
            catch (ParseException e) {
                if (DateUtility._logger.isLoggable(Level.FINER)) {
                    DateUtility._logger.finer("could not parse message sent date, using current date");
                }
                sentDate = new Date();
            }
        }
        else {
            if (DateUtility._logger.isLoggable(Level.FINE)) {
                DateUtility._logger.fine("There is no sent date, using current date.");
            }
            sentDate = new Date();
        }
        if (sentDate.getTime() <= DateUtility.YEAR_1900) {
            sentDate.setTime(0L);
        }
        return sentDate;
    }
    
    static {
        _logger = Logger.getLogger(DateUtility.class.getName());
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1900, 0, 1, 0, 0, 0);
        YEAR_1900 = calendar.getTimeInMillis();
    }
}
