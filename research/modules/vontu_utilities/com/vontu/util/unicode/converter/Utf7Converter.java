// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

import java.io.IOException;
import com.vontu.util.ProtectError;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.unicode.CharacterConversionError;
import java.io.OutputStream;
import com.vontu.util.CharArrayCharSequence;
import com.vontu.util.unicode.CharacterConversionException;
import com.vontu.util.unicode.ByteConverter;

public class Utf7Converter implements ByteConverter
{
    private boolean _canEncode;
    private boolean _canDecode;
    private final byte[] _encoderArray;
    private final char[] _decodeArray;
    private final boolean[] _validByte;
    private final byte _baseStart = 43;
    private final byte _hyphen = 45;
    private final int[] _decodeShiftMask;
    
    public Utf7Converter() {
        this._canEncode = false;
        this._canDecode = false;
        this._encoderArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
        this._decodeArray = new char[256];
        this._validByte = new boolean[256];
        for (int i = 0; i < this._encoderArray.length; ++i) {
            final byte b = this._encoderArray[i];
            this._validByte[b] = true;
            this._decodeArray[b] = (char)i;
        }
        this._decodeShiftMask = new int[] { 42, 36, 30, 24, 18, 12, 6, 0 };
    }
    
    @Override
    public CharSequence convert(final byte[] bytes) throws CharacterConversionException {
        return this.convert(bytes, 0, bytes.length);
    }
    
    @Override
    public CharSequence convert(final byte[] bytes, final int offset, final int byteLength) throws CharacterConversionException {
        if (!this._canDecode) {
            throw new CharacterConversionException("Decoding charset UTF-7 disabled.");
        }
        final char[] output = new char[byteLength - offset];
        final ByteCharIndex index = new ByteCharIndex(offset);
        while (index.byteIndex < byteLength) {
            if (bytes[index.byteIndex] == 43) {
                if (bytes[index.byteIndex + 1] == 45) {
                    output[index.incrCharIndex()] = '+';
                    index.addByteIndex(2);
                }
                else {
                    index.incrByteIndex();
                    this.decodeModifiedBase64(output, bytes, index, byteLength);
                }
            }
            else {
                if ((bytes[index.byteIndex] & 0x80) != 0x0) {
                    throw new CharacterConversionException("Bad character in UTF-7 encoded string at " + index);
                }
                output[index.incrCharIndex()] = (char)bytes[index.incrByteIndex()];
            }
        }
        final char[] finalOut = new char[index.charIndex];
        System.arraycopy(output, 0, finalOut, 0, index.charIndex);
        return new CharArrayCharSequence(finalOut, 0, index.charIndex);
    }
    
    void decodeModifiedBase64(final char[] output, final byte[] bytes, final ByteCharIndex index) throws CharacterConversionException {
        this.decodeModifiedBase64(output, bytes, index, bytes.length);
    }
    
    void decodeModifiedBase64(final char[] output, final byte[] bytes, final ByteCharIndex index, final int byteLength) throws CharacterConversionException {
        int step = 0;
        long buffer = 0L;
        while (index.byteIndex < byteLength && this._validByte[bytes[index.byteIndex]]) {
            buffer |= this._decodeArray[bytes[index.byteIndex]] << this._decodeShiftMask[step];
            step = (step + 1 & 0x7);
            if (step == 0) {
                output[index.incrCharIndex()] = (char)(buffer >> 32);
                output[index.incrCharIndex()] = (char)(buffer >> 16);
                output[index.incrCharIndex()] = (char)buffer;
                buffer = 0L;
            }
            index.incrByteIndex();
        }
        if (step == 3) {
            output[index.incrCharIndex()] = (char)(buffer >> 32);
        }
        else if (step == 6) {
            output[index.incrCharIndex()] = (char)(buffer >> 32);
            output[index.incrCharIndex()] = (char)(buffer >> 16);
        }
        else if (step != 0) {
            throw new CharacterConversionException("Bad character in UTF-7 encoded string at " + index);
        }
        if (index.byteIndex < byteLength && bytes[index.byteIndex] == 45) {
            index.incrByteIndex();
        }
    }
    
    @Override
    public byte[] convert(final CharSequence chars) throws CharacterConversionException {
        throw new CharacterConversionException("Encoding charset UTF-7 disabled.");
    }
    
    @Override
    public void convert(final CharSequence chars, final OutputStream out) throws IOException {
        throw new ProtectRuntimeException((ProtectError)CharacterConversionError.DISABLED_ENCODING, "UTF-7");
    }
    
    @Override
    public void initialize(final String characterSet, final boolean enableEncoding, final boolean enableDecoding) {
        this._canDecode = enableDecoding;
        this._canEncode = false;
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
