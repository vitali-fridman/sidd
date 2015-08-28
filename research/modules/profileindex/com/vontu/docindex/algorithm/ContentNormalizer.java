// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.algorithm;

import com.vontu.util.CharArrayCharSequence;
import cern.colt.list.IntArrayList;
import cern.colt.list.CharArrayList;

public class ContentNormalizer
{
    private static final int REDUCTION_FACTOR = 2;
    private int[] _originalPosition;
    
    public ContentNormalizer() {
        this._originalPosition = null;
    }
    
    public CharSequence normalize(final CharSequence in) {
        final CharArrayList out = new CharArrayList(in.length() / 2);
        final IntArrayList offset = new IntArrayList(in.length() / 2);
        for (int i = 0; i < in.length(); ++i) {
            if (Character.isLetterOrDigit(in.charAt(i))) {
                out.add(Character.toLowerCase(in.charAt(i)));
                offset.add(i);
            }
        }
        out.trimToSize();
        offset.trimToSize();
        this._originalPosition = offset.elements();
        return (CharSequence)new CharArrayCharSequence(out.elements());
    }
    
    public static CharSequence normalizeStatelessly(final CharSequence in) {
        final char[] out = new char[in.length()];
        int currentOutIndex = 0;
        for (int i = 0; i < in.length(); ++i) {
            final char currentIn;
            if (Character.isLetterOrDigit(currentIn = in.charAt(i))) {
                out[currentOutIndex++] = Character.toLowerCase(currentIn);
            }
        }
        return (CharSequence)new CharArrayCharSequence(out, 0, currentOutIndex);
    }
    
    public int getOriginialPosition(final int cleanedPosition) {
        return this._originalPosition[cleanedPosition];
    }
}
