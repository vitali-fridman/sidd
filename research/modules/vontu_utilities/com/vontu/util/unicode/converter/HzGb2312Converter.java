// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

import java.nio.charset.CodingErrorAction;
import java.io.IOException;
import com.vontu.util.ProtectError;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.unicode.CharacterConversionError;
import java.io.OutputStream;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import com.vontu.util.unicode.CharacterConversionException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import com.vontu.util.unicode.ByteConverter;

public class HzGb2312Converter implements ByteConverter
{
    private boolean _canEncode;
    private boolean _canDecode;
    private Charset _gb2312Charset;
    private CharsetDecoder _gb2312Decoder;
    private static final String GB2312 = "GB2312";
    
    public HzGb2312Converter() {
        this._canEncode = false;
        this._canDecode = false;
        this._gb2312Charset = null;
        this._gb2312Decoder = null;
    }
    
    @Override
    public CharSequence convert(final byte[] bytes) throws CharacterConversionException {
        return this.convert(bytes, 0, bytes.length);
    }
    
    @Override
    public CharSequence convert(final byte[] bytes, final int offset, final int length) throws CharacterConversionException {
        if (!this._canDecode) {
            throw new CharacterConversionException("Decoding charset HZ-GB-2312 disabled.");
        }
        final StringBuilder out = new StringBuilder();
        int byteIndex = offset;
        while (byteIndex < length) {
            final char c = (char)bytes[byteIndex];
            if (c == '~' && byteIndex < length - 1) {
                switch ((char)bytes[byteIndex + 1]) {
                    case '~': {
                        out.append('~');
                        byteIndex += 2;
                        continue;
                    }
                    case '\n': {
                        byteIndex += 2;
                        continue;
                    }
                    case '{': {
                        byteIndex = this.addGb2312(bytes, byteIndex + 2, out);
                        continue;
                    }
                    default: {
                        out.append('~');
                        ++byteIndex;
                        continue;
                    }
                }
            }
            else {
                out.append(c);
                ++byteIndex;
            }
        }
        return out;
    }
    
    private int addGb2312(final byte[] bytes, final int byteIndex, final StringBuilder out) throws CharacterConversionException {
        int endIndex = 0;
        for (int i = byteIndex; i < bytes.length - 1; i += 2) {
            if (bytes[i] == 126 && bytes[i + 1] == 125) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == 0) {
            return byteIndex;
        }
        if (endIndex == byteIndex) {
            return byteIndex + 2;
        }
        final byte[] copy = new byte[endIndex - byteIndex];
        System.arraycopy(bytes, byteIndex, copy, 0, copy.length);
        for (int j = 0; j < copy.length; ++j) {
            final byte[] array = copy;
            final int n = j;
            array[n] |= (byte)128;
        }
        try {
            out.append(this._gb2312Decoder.decode(ByteBuffer.wrap(copy)));
        }
        catch (CharacterCodingException e) {
            throw new CharacterConversionException(e);
        }
        return endIndex + 2;
    }
    
    @Override
    public byte[] convert(final CharSequence chars) throws CharacterConversionException {
        throw new CharacterConversionException("Encoding charset HZ-GB-2312 disabled.");
    }
    
    @Override
    public void convert(final CharSequence chars, final OutputStream out) throws IOException {
        throw new ProtectRuntimeException((ProtectError)CharacterConversionError.DISABLED_ENCODING, "HZ-GB-2312");
    }
    
    @Override
    public void initialize(final String characterSet, final boolean enableEncoding, final boolean enableDecoding) {
        this._canEncode = false;
        if (!Charset.isSupported("GB2312")) {
            this._canDecode = false;
            return;
        }
        try {
            this._gb2312Charset = Charset.forName("GB2312");
            (this._gb2312Decoder = this._gb2312Charset.newDecoder()).onMalformedInput(CodingErrorAction.IGNORE);
        }
        catch (Exception e) {
            this._gb2312Charset = null;
        }
        this._canDecode = true;
    }
    
    @Override
    public boolean canEncode() {
        return this._canEncode;
    }
    
    @Override
    public boolean canDecode() {
        return this._canDecode;
    }
}
