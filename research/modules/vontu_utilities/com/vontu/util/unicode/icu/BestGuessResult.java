// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.icu;

import com.vontu.util.unicode.CharacterEncoding;

public class BestGuessResult
{
    private final CharacterEncoding _encoding;
    private final byte[] _originalMessage;
    private final CharSequence _decodedContent;
    private final int _confidence;
    private final boolean _usedDefault;
    
    public BestGuessResult(final CharacterEncoding encoding, final byte[] originalMessage, final CharSequence decodedContent, final int confidence, final boolean usedDefault) {
        this._encoding = encoding;
        this._originalMessage = originalMessage;
        this._decodedContent = decodedContent;
        this._confidence = confidence;
        this._usedDefault = usedDefault;
    }
    
    public CharacterEncoding getEncoding() {
        return this._encoding;
    }
    
    public byte[] getOriginalMessage() {
        return this._originalMessage;
    }
    
    public CharSequence getDecodedContent() {
        return this._decodedContent;
    }
    
    public int getConfidence() {
        return this._confidence;
    }
    
    public boolean isDefaultEncodingUsed() {
        return this._usedDefault;
    }
    
    @Override
    public String toString() {
        return "Msg: " + this._originalMessage.length + "bytes; " + "Decoded: " + this._decodedContent.length() + "chars; " + "Encoding: " + this._encoding.getCharacterSet() + "; " + "Confidence: " + this._confidence + "; " + "UsedDefault: " + this._usedDefault;
    }
}
