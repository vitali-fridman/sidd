// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TAXIDNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (TAXIDNormalizer._instance == null) {
            TAXIDNormalizer._instance = new TAXIDNormalizer();
        }
        return TAXIDNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String[] normalized = { AbstractNormalizer.removeNonDigits(text, position, length) };
        return normalized;
    }
}
