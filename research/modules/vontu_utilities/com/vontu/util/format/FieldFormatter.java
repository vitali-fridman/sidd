// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.format;

import com.vontu.util.validate.StringValidator;

public class FieldFormatter
{
    static final String TRIM_SUFFIX = "...";
    static final int TRIM_AMOUNT;
    
    public static String trimField(final String value, final int size) {
        if (StringValidator.isEmptyString(value)) {
            return value;
        }
        if (value.length() <= size) {
            return value;
        }
        if (value.length() <= FieldFormatter.TRIM_AMOUNT || size < FieldFormatter.TRIM_AMOUNT) {
            return value.substring(0, size);
        }
        return value.substring(0, size - FieldFormatter.TRIM_AMOUNT) + "...";
    }
    
    static {
        TRIM_AMOUNT = "...".length();
    }
}
