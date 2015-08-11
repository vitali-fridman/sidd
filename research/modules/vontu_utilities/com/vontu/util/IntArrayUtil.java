// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Iterator;
import java.util.Collection;
import cern.colt.list.IntArrayList;

public final class IntArrayUtil
{
    public static int[] fromCsv(final String csv) throws NumberFormatException {
        final String[] values = csv.split(",");
        final IntArrayList list = new IntArrayList(values.length);
        for (int i = 0; i < values.length; ++i) {
            if (values[i].trim().length() > 0) {
                list.add(Integer.parseInt(values[i].trim()));
            }
        }
        list.trimToSize();
        return list.elements();
    }
    
    public static String toCsv(final int[] array) {
        final StringBuffer stringBuilder = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(String.valueOf(array[i]));
        }
        return stringBuilder.toString();
    }
    
    public static int[] fromObjectArray(final Integer[] objectArray) {
        final int[] intArray = new int[objectArray.length];
        for (int i = 0; i < objectArray.length; ++i) {
            intArray[i] = objectArray[i];
        }
        return intArray;
    }
    
    public static Integer[] toObjectArray(final int[] intArray) {
        final Integer[] objectArray = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; ++i) {
            objectArray[i] = new Integer(intArray[i]);
        }
        return objectArray;
    }
    
    public static String toCsvFromObjectList(final Collection<Integer> objectList) {
        final StringBuffer stringBuilder = new StringBuffer();
        for (final Integer intObject : objectList) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(intObject.toString());
        }
        return stringBuilder.toString();
    }
}
