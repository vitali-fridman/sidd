// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class BooleanArray
{
    public static boolean[] or(final boolean[] array1, final boolean[] array2) {
        assert array1.length == array2.length;
        final boolean[] result = new boolean[array1.length];
        for (int i = 0; i < array1.length; ++i) {
            result[i] = (array1[i] | array2[i]);
        }
        return result;
    }
    
    public static boolean[] and(final boolean[] array1, final boolean[] array2) {
        assert array1.length == array2.length;
        final boolean[] result = new boolean[array1.length];
        for (int i = 0; i < array1.length; ++i) {
            result[i] = (array1[i] & array2[i]);
        }
        return result;
    }
    
    public static boolean[] not(final boolean[] original) {
        final boolean[] result = new boolean[original.length];
        for (int i = 0; i < original.length; ++i) {
            result[i] = !original[i];
        }
        return result;
    }
}
