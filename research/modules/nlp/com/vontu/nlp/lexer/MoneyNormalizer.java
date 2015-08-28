// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class MoneyNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (MoneyNormalizer._instance == null) {
            MoneyNormalizer._instance = new MoneyNormalizer();
        }
        return MoneyNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, int position, final int length) {
        final String[] normalized = { null };
        final char[] word = new char[length];
        int pos = 0;
        for (int i = 0; i < length; ++i) {
            if (Character.isDigit(text.charAt(position)) || text.charAt(position) == '$' || text.charAt(position) == '-' || text.charAt(position) == '.') {
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
