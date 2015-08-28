// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class PostalCodeNormalizer extends AbstractNormalizer
{
    public static final int ZIP5LENGTH = 5;
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (PostalCodeNormalizer._instance == null) {
            PostalCodeNormalizer._instance = new PostalCodeNormalizer();
        }
        return PostalCodeNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String[] normalized = { null };
        final String justDigits = AbstractNormalizer.removeNonDigits(text, position, length);
        if (justDigits.length() == 3 && Character.isLetter(text.charAt(position))) {
            normalized[0] = text.subSequence(position, position + 3).toString();
            normalized[0] = normalized[0].concat(text.subSequence(position + length - 3, position + length).toString());
            return normalized;
        }
        normalized[0] = text.subSequence(position, position + 5).toString();
        return normalized;
    }
}
