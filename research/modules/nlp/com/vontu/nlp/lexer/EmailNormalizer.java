// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class EmailNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (EmailNormalizer._instance == null) {
            EmailNormalizer._instance = new EmailNormalizer();
        }
        return EmailNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String[] normalized = { null };
        final StringBuffer normalizedWord = new StringBuffer(length);
        for (int i = position; i < position + length; ++i) {
            final char ch = Character.toLowerCase(text.charAt(i));
            normalizedWord.append(ch);
        }
        normalized[0] = normalizedWord.toString();
        return normalized;
    }
    
    public static void main(final String[] args) {
        final Normalizer normalizer = getInstance();
        System.out.println(normalizer.normalize("ERIC@VONTU.com")[0]);
        System.out.println(normalizer.normalize("eric's.email@Whatever.fr")[0]);
    }
}
