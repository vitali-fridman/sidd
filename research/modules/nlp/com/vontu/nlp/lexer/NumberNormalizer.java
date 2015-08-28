// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class NumberNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (NumberNormalizer._instance == null) {
            NumberNormalizer._instance = new NumberNormalizer();
        }
        return NumberNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, int position, int length) {
        final String[] normalized = { null };
        final char[] word = new char[length];
        int pos = 0;
        if (text.charAt(position) == '-') {
            word[pos++] = text.charAt(position++);
            --length;
        }
        int backPosition;
        int rightMostPunctuation;
        for (backPosition = position + length - 1, rightMostPunctuation = -1; backPosition >= position && rightMostPunctuation == -1; --backPosition) {
            if (text.charAt(backPosition) == '.' || text.charAt(backPosition) == ',') {
                rightMostPunctuation = backPosition;
            }
        }
        int leftMostPunctuation = -1;
        for (int frontPosition = position; frontPosition < position + length && leftMostPunctuation == -1; ++frontPosition) {
            if (text.charAt(frontPosition) == '.' || text.charAt(frontPosition) == ',') {
                leftMostPunctuation = frontPosition;
            }
        }
        for (int j = 0; j < length; ++j) {
            if (Character.isDigit(text.charAt(position))) {
                word[pos++] = text.charAt(position++);
            }
            else if (position == rightMostPunctuation && (rightMostPunctuation == leftMostPunctuation || text.charAt(rightMostPunctuation) != text.charAt(leftMostPunctuation))) {
                word[pos++] = '.';
                ++position;
            }
            else {
                ++position;
            }
        }
        normalized[0] = new String(word, 0, pos);
        return normalized;
    }
}
