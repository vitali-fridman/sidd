// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.convert;

public class IntegerConvertor
{
    public static Integer convert(final String number) {
        try {
            return Integer.valueOf(number);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
