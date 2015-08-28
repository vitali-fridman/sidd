// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class CCNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (CCNormalizer._instance == null) {
            CCNormalizer._instance = new CCNormalizer();
        }
        return CCNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String[] normalized = { AbstractNormalizer.removeNonDigits(text, position, length) };
        return normalized;
    }
}
