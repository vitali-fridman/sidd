// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.icu;

public interface Guesser
{
    BestGuessResult guessEncoding(byte[] p0);
    
    String getName();
    
    String getBackupEncoding();
}
