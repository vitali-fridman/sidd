// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

public interface CodePointFilter
{
    boolean accepts(int p0);
    
    CharSequence filterSequence(CharSequence p0);
    
    char getReplacementCharacter();
}
