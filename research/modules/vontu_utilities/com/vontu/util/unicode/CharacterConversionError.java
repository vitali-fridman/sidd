// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.util.HashMap;
import java.util.Map;
import com.vontu.util.ParameterizedError;

public class CharacterConversionError extends ParameterizedError
{
    private static final int BEGIN = 14700;
    private static final Map<Integer, String> _templateMessages;
    public static final CharacterConversionError UNSUPPORTED_CHARSET;
    public static final CharacterConversionError DISABLED_CHARSET;
    public static final CharacterConversionError DISABLED_ENCODING;
    public static final CharacterConversionError DISABLED_DECODING;
    
    protected CharacterConversionError(final int value, final String templateMessage) throws IllegalArgumentException {
        super(value);
        CharacterConversionError._templateMessages.put(value, templateMessage);
    }
    
    @Override
    protected String getMessageTemplate() {
        return CharacterConversionError._templateMessages.get(this.getValue());
    }
    
    static {
        _templateMessages = new HashMap<Integer, String>();
        UNSUPPORTED_CHARSET = new CharacterConversionError(14700, "Unsupported character set {0}.");
        DISABLED_CHARSET = new CharacterConversionError(14701, "Disabled character set {0}.");
        DISABLED_ENCODING = new CharacterConversionError(14702, "Encoding to character set {0} is disabled.");
        DISABLED_DECODING = new CharacterConversionError(14703, "Decoding of character set {0} is disabled.");
    }
}
