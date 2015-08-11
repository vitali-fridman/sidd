// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities
{
    public static long getTimeBetweenDates(final Date date1, final Date date2) {
        return date1.getTime() - date2.getTime();
    }
    
    public static long getTimeUntilMidnight() {
        return getTimeUntilNextOccurenceOfHour(0);
    }
    
    public static long getTimeUntilNextOccurenceOfHour(final int hour) {
        final Calendar currentCalendar = Calendar.getInstance();
        final Calendar midnightCalendar = Calendar.getInstance();
        midnightCalendar.add(6, 1);
        midnightCalendar.set(11, hour);
        midnightCalendar.set(12, 0);
        midnightCalendar.set(13, 0);
        return getTimeBetweenDates(midnightCalendar.getTime(), currentCalendar.getTime());
    }
    
    public static String getTimeDifferenceString(long timeDifferenceInMillis) {
        boolean isNegative = false;
        if (timeDifferenceInMillis < 0L) {
            isNegative = true;
            timeDifferenceInMillis = -timeDifferenceInMillis;
        }
        final int millis = (int)(timeDifferenceInMillis % 1000L);
        timeDifferenceInMillis /= 1000L;
        final int seconds = (int)(timeDifferenceInMillis % 60L);
        timeDifferenceInMillis /= 60L;
        final int minutes = (int)(timeDifferenceInMillis % 60L);
        timeDifferenceInMillis /= 60L;
        final int hours = (int)timeDifferenceInMillis;
        if (isNegative) {
            return String.format("-%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        }
        return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
