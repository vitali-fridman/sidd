// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class CharacterEncoding
{
    public static final CharacterEncoding UNKNOWN;
    private final String _charSet;
    private final Language _language;
    private ByteConverter _converter;
    private final Collection<String> _charAliases;
    private final String _prefix;
    
    CharacterEncoding(final String characterSet, final String[] aliases, final Language lang, final ByteConverter converter, final String prefix, final boolean canEncode, final boolean canDecode) {
        this._charSet = characterSet.toUpperCase();
        this._converter = converter;
        this._language = lang;
        this._charAliases = new ArrayList<String>();
        this._prefix = prefix;
        if (aliases != null) {
            for (final String alias : aliases) {
                this._charAliases.add(alias.toLowerCase());
            }
        }
        if (this._converter != null) {
            this._converter.initialize(characterSet, canEncode, canDecode);
        }
    }
    
    public String getCharacterSet() {
        return this._charSet;
    }
    
    public Collection<String> getCharacterSetAliases() {
        return new ArrayList<String>(this._charAliases);
    }
    
    public Language getLanguage() {
        return this._language;
    }
    
    public String getLanguageString() {
        return this._language.toString();
    }
    
    String getPrefix() {
        return this._prefix;
    }
    
    ByteConverter getConverter() {
        return this._converter;
    }
    
    void setConverter(final ByteConverter newConverter) {
        if (this._converter != null) {
            throw new IllegalStateException("Cannot add byte converter when already one exists: " + this.toString());
        }
        this._converter = newConverter;
    }
    
    public CharSequence convert(final byte[] bytes) throws CharacterConversionException, UnsupportedEncodingException {
        if (this._converter == null) {
            throw new UnsupportedEncodingException("No converter specified for character encoding: " + this);
        }
        return this._converter.convert(bytes);
    }
    
    public CharSequence convert(final byte[] bytes, final int offset, final int length) throws CharacterConversionException, UnsupportedEncodingException {
        if (this._converter == null) {
            throw new UnsupportedEncodingException("No converter specified for character encoding: " + this);
        }
        return this._converter.convert(bytes, offset, length);
    }
    
    public void convert(final CharSequence chars, final OutputStream bytes) throws IOException, UnsupportedEncodingException {
        if (this._converter == null) {
            throw new UnsupportedEncodingException("No converter specified for character encoding: " + this);
        }
        this._converter.convert(chars, bytes);
    }
    
    public byte[] convert(final CharSequence chars) throws CharacterConversionException, UnsupportedEncodingException {
        if (this._converter == null) {
            throw new UnsupportedEncodingException("No converter specified for character encoding: " + this);
        }
        return this._converter.convert(chars);
    }
    
    @Override
    public String toString() {
        return this._charSet + " (" + this._language + "," + this._prefix + ")";
    }
    
    static {
        UNKNOWN = new CharacterEncoding("unknown", null, Language.UNKNOWN, null, "---", false, false);
    }
}
