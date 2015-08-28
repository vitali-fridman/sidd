// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TokenSetType
{
    public static final int TABULAR_DATA = 0;
    public static final int NUM_TYPES = 1;
    private static final String[] NAMES;
    
    public static String getName(final int type) {
        return TokenSetType.NAMES[type];
    }
    
    static {
        NAMES = new String[] { "TABULAR_DATA" };
    }
}
