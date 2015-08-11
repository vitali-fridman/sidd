// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.format;

public class ElapsedTimeFormatter
{
    protected static final String hourIdentifier = " hour(s)";
    protected static final String minuteIdentifier = " minute(s)";
    protected static final String secondIdentifier = " second(s)";
    protected static final String millisecondIdentifier = " ms";
    
    public static String format(final long elapsedTime) {
        final long hours = elapsedTime / 3600000L;
        long tempTime = elapsedTime - hours * 3600000L;
        final long minutes = tempTime / 60000L;
        tempTime -= minutes * 60000L;
        final long seconds = tempTime / 1000L;
        final long milliseconds = tempTime - seconds * 1000L;
        final StringBuffer formattedTime = new StringBuffer();
        if (0L < hours) {
            appendValue(formattedTime, hours, " hour(s)");
        }
        if (0L < minutes) {
            appendValue(formattedTime, minutes, " minute(s)");
        }
        if (0L < seconds) {
            appendValue(formattedTime, seconds, " second(s)");
        }
        appendValue(formattedTime, milliseconds, " ms");
        return formattedTime.toString();
    }
    
    private static void appendValue(final StringBuffer formattedTime, final long value, final String identifier) {
        if (0 < formattedTime.length()) {
            formattedTime.append(" ");
        }
        formattedTime.append(value).append(identifier);
    }
}
