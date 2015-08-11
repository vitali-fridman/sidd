// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

import java.io.IOException;
import java.io.OutputStream;
import com.vontu.util.unicode.CharacterConversionException;
import com.vontu.util.unicode.ByteConverter;

public class AsciiConverter implements ByteConverter
{
    private static final String ENCODING = "ISO-8859-1";
    private GenericConverter _converter;
    
    @Override
    public CharSequence convert(final byte[] bytes) throws CharacterConversionException {
        return this._converter.convert(bytes);
    }
    
    @Override
    public CharSequence convert(final byte[] bytes, final int offset, final int length) throws CharacterConversionException {
        return this._converter.convert(bytes, offset, length);
    }
    
    @Override
    public byte[] convert(final CharSequence chars) throws CharacterConversionException {
        return this._converter.convert(chars);
    }
    
    @Override
    public void convert(final CharSequence chars, final OutputStream out) throws IOException {
        this._converter.convert(chars, out);
    }
    
    @Override
    public void initialize(final String characterSet, final boolean enableEncoding, final boolean enableDecoding) {
        (this._converter = new GenericConverter()).initialize("ISO-8859-1", enableEncoding, enableDecoding);
    }
    
    @Override
    public boolean canEncode() {
        return this._converter.canEncode();
    }
    
    @Override
    public boolean canDecode() {
        return this._converter.canDecode();
    }
}
