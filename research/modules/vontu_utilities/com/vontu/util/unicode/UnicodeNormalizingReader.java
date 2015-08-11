// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.IOException;
import java.nio.CharBuffer;
import java.io.Reader;

public class UnicodeNormalizingReader extends Reader
{
    private final Reader _originalReader;
    private CharBuffer _normalizedBuffer;
    private final UnicodeNormalizer _normalizer;
    
    public UnicodeNormalizingReader(final Reader reader) {
        super(reader);
        this._originalReader = reader;
        this._normalizer = UnicodeNormalizerRegistry.getNormalizer();
    }
    
    @Override
    public int read(final char[] cbuf, final int offset, final int length) throws IOException {
        if (this._normalizedBuffer != null && this._normalizedBuffer.hasRemaining()) {
            int charsRead;
            if (length < this._normalizedBuffer.remaining()) {
                charsRead = length;
            }
            else {
                charsRead = this._normalizedBuffer.remaining();
            }
            this._normalizedBuffer.get(cbuf, offset, charsRead);
            return charsRead;
        }
        int charsRead = this._originalReader.read(cbuf, offset, length);
        if (charsRead != -1) {
            final CharSequence normalized = this._normalizer.normalize(CharBuffer.wrap(cbuf, offset, charsRead), false);
            this._normalizedBuffer = CharBuffer.wrap(normalized);
            if (normalized.length() < length) {
                charsRead = normalized.length();
            }
            else {
                charsRead = length;
            }
            this._normalizedBuffer.get(cbuf, offset, charsRead);
        }
        return charsRead;
    }
    
    @Override
    public void close() throws IOException {
        this._originalReader.close();
    }
}
