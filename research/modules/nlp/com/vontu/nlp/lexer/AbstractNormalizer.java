// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public abstract class AbstractNormalizer implements Normalizer
{
    private static Normalizer _instance;
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String[] normalized = { text.subSequence(position, position + length).toString() };
        return normalized;
    }
    
    @Override
    public String[] normalize(final String value) {
        return this.normalize(value, 0, value.length());
    }
    
    public static String removeNonDigits(final CharSequence text) {
        return removeNonDigits(text, 0, text.length());
    }
    
    public static String removeNonDigits(final CharSequence text, int position, final int length) {
        final char[] word = new char[length];
        int pos = 0;
        for (int i = 0; i < length; ++i) {
            if (Character.isDigit(text.charAt(position))) {
                word[pos++] = text.charAt(position++);
            }
            else {
                ++position;
            }
        }
        return new String(word, 0, pos);
    }
    
    public static boolean isOnlyDigits(final String value) {
        for (int i = 0; i < value.length(); ++i) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
