// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public interface Normalizer
{
    String[] normalize(CharSequence p0, int p1, int p2);
    
    String[] normalize(String p0);
}
