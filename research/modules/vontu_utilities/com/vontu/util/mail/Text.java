// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.ByteString;

public class Text
{
    private ByteString mText;
    private String mEncoding;
    private String mLanguage;
    
    public Text(final ByteString text, final String encoding) {
        this.mText = text;
        this.mEncoding = encoding;
        this.mLanguage = "";
    }
    
    public Text(final ByteString text, final String encoding, final String language) {
        this.mText = text;
        this.mEncoding = encoding;
        this.mLanguage = language;
    }
    
    public ByteString getText() {
        return this.mText;
    }
    
    public String getCharset() {
        return this.mEncoding;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
    public String getLanguage() {
        return this.mLanguage;
    }
}
