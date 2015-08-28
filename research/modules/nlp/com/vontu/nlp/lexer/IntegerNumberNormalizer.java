// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class IntegerNumberNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (IntegerNumberNormalizer._instance == null) {
            IntegerNumberNormalizer._instance = new IntegerNumberNormalizer();
        }
        return IntegerNumberNormalizer._instance;
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
        for (int i = 0; i < length; ++i) {
            if (Character.isDigit(text.charAt(position)) || text.charAt(position) == '.') {
                word[pos++] = text.charAt(position++);
            }
            else {
                ++position;
            }
        }
        normalized[0] = new String(word, 0, pos);
        return normalized;
    }
}
