// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteConverter
{
    CharSequence convert(byte[] p0) throws CharacterConversionException;
    
    CharSequence convert(byte[] p0, int p1, int p2) throws CharacterConversionException;
    
    byte[] convert(CharSequence p0) throws CharacterConversionException;
    
    void convert(CharSequence p0, OutputStream p1) throws IOException;
    
    void initialize(String p0, boolean p1, boolean p2);
    
    boolean canEncode();
    
    boolean canDecode();
}
