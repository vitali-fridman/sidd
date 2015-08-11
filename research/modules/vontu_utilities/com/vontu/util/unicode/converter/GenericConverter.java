// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.io.IOException;
import java.io.Writer;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import com.vontu.util.ProtectError;
import com.vontu.util.ProtectRuntimeException;
import java.io.OutputStream;
import java.nio.charset.CharacterCodingException;
import com.vontu.util.unicode.CharacterConversionError;
import java.nio.ByteBuffer;
import com.vontu.util.unicode.CharacterConversionException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import com.vontu.util.unicode.ByteConverter;

public class GenericConverter implements ByteConverter
{
    protected static final int ENCODER_CHUNK_SIZE = 8192;
    private static final Logger _logger;
    private ThreadLocal<Codec> _localCodec;
    private String _charsetName;
    private Charset _charset;
    private boolean _enableEncoding;
    private boolean _enableDecoding;
    
    public GenericConverter() {
        this._localCodec = new ThreadLocal<Codec>() {
            @Override
            protected Codec initialValue() {
                return new Codec();
            }
        };
        this._charset = null;
    }
    
    @Override
    public void initialize(final String characterSet, final boolean enableEncoding, final boolean enableDecoding) {
        this._charsetName = characterSet;
        this._enableEncoding = enableEncoding;
        this._enableDecoding = enableDecoding;
        try {
            this._charset = Charset.forName(characterSet);
        }
        catch (UnsupportedCharsetException e) {
            GenericConverter._logger.warning("Requested charset is not supported: " + characterSet);
        }
    }
    
    @Override
    public CharSequence convert(final byte[] bytes) throws CharacterConversionException {
        return this.convert(bytes, 0, bytes.length);
    }
    
    @Override
    public CharSequence convert(final byte[] bytes, final int offset, final int length) throws CharacterConversionException {
        final ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        if (this._charset == null) {
            throw new CharacterConversionException(CharacterConversionError.DISABLED_CHARSET.getDescription(this._charsetName));
        }
        if (this.getDecoder() == null) {
            throw new CharacterConversionException(CharacterConversionError.DISABLED_DECODING.getDescription(this._charsetName));
        }
        try {
            return this.getDecoder().decode(bb);
        }
        catch (CharacterCodingException e) {
            throw new CharacterConversionException(e);
        }
    }
    
    @Override
    public void convert(final CharSequence chars, final OutputStream out) throws IOException {
        if (this._charset == null) {
            throw new ProtectRuntimeException((ProtectError)CharacterConversionError.DISABLED_CHARSET, this._charsetName);
        }
        if (this.getEncoder() == null) {
            throw new ProtectRuntimeException((ProtectError)CharacterConversionError.DISABLED_ENCODING, this._charsetName);
        }
        try {
            final Writer writer = new OutputStreamWriter(out, this._charsetName);
            for (int i = 0; i < chars.length(); i += 8192) {
                writer.write(chars.subSequence(i, i + Math.min(8192, chars.length() - i)).toString());
            }
            writer.flush();
        }
        catch (UnsupportedEncodingException e) {
            throw new ProtectRuntimeException((ProtectError)CharacterConversionError.UNSUPPORTED_CHARSET, e, this._charsetName);
        }
    }
    
    @Override
    public byte[] convert(final CharSequence chars) throws CharacterConversionException {
        if (this._charset == null) {
            throw new CharacterConversionException(CharacterConversionError.DISABLED_CHARSET.getDescription(this._charsetName));
        }
        if (this.getEncoder() == null) {
            throw new CharacterConversionException(CharacterConversionError.DISABLED_ENCODING.getDescription(this._charsetName));
        }
        try {
            return chars.toString().getBytes(this._charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw new CharacterConversionException(e);
        }
    }
    
    @Override
    public boolean canEncode() {
        return this.getEncoder() != null;
    }
    
    @Override
    public boolean canDecode() {
        return this.getDecoder() != null;
    }
    
    private CharsetDecoder getDecoder() {
        return this._localCodec.get().getDecoder();
    }
    
    private CharsetEncoder getEncoder() {
        return this._localCodec.get().getEncoder();
    }
    
    @Override
    public String toString() {
        return this._charsetName;
    }
    
    static {
        _logger = Logger.getLogger(GenericConverter.class.getName());
    }
    
    private class Codec
    {
        private final CharsetEncoder _encoder;
        private final CharsetDecoder _decoder;
        
        public Codec() {
            this._encoder = ((GenericConverter.this._charset != null && GenericConverter.this._enableEncoding) ? this.initEncoder(GenericConverter.this._charset) : null);
            this._decoder = ((GenericConverter.this._charset != null && GenericConverter.this._enableDecoding) ? this.initDecoder(GenericConverter.this._charset) : null);
        }
        
        private CharsetEncoder initEncoder(final Charset charset) {
            try {
                final CharsetEncoder encoder = charset.newEncoder();
                encoder.onMalformedInput(CodingErrorAction.IGNORE);
                encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
                return encoder;
            }
            catch (UnsupportedOperationException e) {}
            catch (IllegalArgumentException ex) {}
            return null;
        }
        
        private CharsetDecoder initDecoder(final Charset charset) {
            try {
                final CharsetDecoder decoder = charset.newDecoder();
                decoder.onMalformedInput(CodingErrorAction.IGNORE);
                decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
                return decoder;
            }
            catch (UnsupportedOperationException e) {}
            catch (IllegalArgumentException ex) {}
            return null;
        }
        
        CharsetEncoder getEncoder() {
            return this._encoder;
        }
        
        CharsetDecoder getDecoder() {
            return this._decoder;
        }
    }
}
