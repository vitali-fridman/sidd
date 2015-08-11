// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDateFormatFactory
{
    public SimpleDateFormat createSimpleDateFormat(final SimpleDateType type, final Locale locale) {
        final SimpleDateFormat simpleFormat = (SimpleDateFormat)DateFormat.getDateInstance(type.getBaseDateFormatType(), locale);
        if (type == SimpleDateType.SHORT_YYYY) {
            this.changeToYYYYFormat(simpleFormat);
        }
        return simpleFormat;
    }
    
    private void changeToYYYYFormat(final SimpleDateFormat simpleFormat) {
        final String pattern = simpleFormat.toPattern();
        if (!pattern.contains("yyyy")) {
            final String yyyyPattern = pattern.replace("yy", "yyyy");
            simpleFormat.applyPattern(yyyyPattern);
        }
    }
}
