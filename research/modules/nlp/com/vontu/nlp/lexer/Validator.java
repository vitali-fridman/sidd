// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public interface Validator
{
    boolean assertTokenValidity(CharSequence p0, int p1, int p2);
    
    boolean assertTokenValidity(String p0);
}
