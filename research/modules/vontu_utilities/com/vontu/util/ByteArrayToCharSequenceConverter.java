// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import com.vontu.util.unicode.icu.BestGuessResult;
import com.vontu.util.unicode.CharacterConversionException;
import com.vontu.util.unicode.UnsupportedEncodingException;
import com.vontu.util.unicode.CharacterEncoding;
import com.vontu.util.unicode.CharacterEncodingManager;
import java.util.logging.Logger;

public class ByteArrayToCharSequenceConverter
{
    private static final Logger logger;
    private final CharacterEncodingManager encodingManager;
    
    public ByteArrayToCharSequenceConverter(final CharacterEncodingManager encodingManager) {
        this.encodingManager = encodingManager;
    }
    
    public Result convert(final byte[] bytes, final CharacterEncoding encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("The encoding argument cannot be null; use CharacterEncoding.UNKNOWN instead");
        }
        if (encoding != CharacterEncoding.UNKNOWN) {
            try {
                return new Result(encoding.convert(bytes), encoding);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                ByteArrayToCharSequenceConverter.logger.fine("Unsupported Encoding specified, falling back to best-guess");
            }
            catch (CharacterConversionException characterConversionException) {
                ByteArrayToCharSequenceConverter.logger.fine("Decoding with supplied encoding failed, falling back to best-guess");
            }
        }
        final BestGuessResult result = this.encodingManager.guessEncodingAndDecode(bytes);
        return new Result(result.getDecodedContent(), result.getEncoding());
    }
    
    static {
        logger = Logger.getLogger(ByteArrayToCharSequenceConverter.class.getName());
    }
    
    public class Result
    {
        private final CharSequence chars;
        private final CharacterEncoding encoding;
        
        private Result(final CharSequence chars, final CharacterEncoding encoding) {
            this.chars = chars;
            this.encoding = encoding;
        }
        
        public CharSequence getChars() {
            return this.chars;
        }
        
        public CharacterEncoding getEncoding() {
            return this.encoding;
        }
    }
}
