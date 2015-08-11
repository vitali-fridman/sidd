// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import com.vontu.util.unicode.CharacterEncodingManager;
import com.vontu.util.unicode.UnicodeNormalizerRegistry;
import com.vontu.util.unicode.CharacterEncoding;
import java.util.logging.Logger;

public class ByteContent
{
    private static final Logger _logger;
    private byte[] _originalContent;
    private CharacterEncoding _encoding;
    private CharSequence _decodedContent;
    private boolean _freeMemoryAfterDecoding;
    
    public ByteContent(final byte[] originalContent) {
        this._encoding = CharacterEncoding.UNKNOWN;
        this._decodedContent = null;
        this._freeMemoryAfterDecoding = true;
        this._originalContent = originalContent;
    }
    
    public ByteContent(final byte[] originalContent, final CharacterEncoding encoding) {
        this(originalContent);
        this.setEncoding(encoding);
    }
    
    public ByteContent(final byte[] originalContent, final CharacterEncoding encoding, final boolean freeMemoryAfterDecoding) {
        this(originalContent, encoding);
        this._freeMemoryAfterDecoding = freeMemoryAfterDecoding;
    }
    
    public ByteContent() {
        this(new byte[0]);
    }
    
    public synchronized void setEncoding(final CharacterEncoding encoding) {
        this._encoding = ((encoding == null) ? CharacterEncoding.UNKNOWN : encoding);
        if (this._decodedContent != null) {
            ByteContent._logger.warning("Setting encoding after decoding content.");
        }
        this._decodedContent = null;
    }
    
    public CharacterEncoding getEncoding() {
        return this._encoding;
    }
    
    public synchronized byte[] getByteContent() {
        if (this._decodedContent != null && this._freeMemoryAfterDecoding) {
            throw new IllegalStateException("Cannot get original content after decoding.");
        }
        return this._originalContent;
    }
    
    public synchronized boolean isByteContentSet() {
        return this._originalContent != null && this._originalContent.length > 0;
    }
    
    public synchronized CharSequence getDecodedContent() {
        if (this._decodedContent == null && this._originalContent != null) {
            this._decodedContent = this.decodeContent();
            if (this._decodedContent != null) {
                if (this._freeMemoryAfterDecoding) {
                    this._originalContent = null;
                }
                this._decodedContent = UnicodeNormalizerRegistry.getNormalizer().normalize(this._decodedContent);
            }
        }
        return this._decodedContent;
    }
    
    private CharSequence decodeContent() {
        final ByteArrayToCharSequenceConverter converter = new ByteArrayToCharSequenceConverter(CharacterEncodingManager.getInstance());
        final ByteArrayToCharSequenceConverter.Result converterResult = converter.convert(this._originalContent, this._encoding);
        this.setEncoding(converterResult.getEncoding());
        return converterResult.getChars();
    }
    
    static {
        _logger = Logger.getLogger(ByteContent.class.getName());
    }
}
