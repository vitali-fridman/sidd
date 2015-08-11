// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.validate;

public class StringValidator
{
    public static boolean isEmptyString(final String string) {
        return null == string || 0 == string.trim().length();
    }
    
    public static boolean isNotEmptyString(final String string) {
        return !isEmptyString(string);
    }
    
    public static boolean areEqual(final String value1, final String value2) {
        if (null == value1) {
            return null == value2;
        }
        return value1.equals(value2);
    }
    
    public static boolean areNotEqual(final String value1, final String value2) {
        return !areEqual(value1, value2);
    }
}
