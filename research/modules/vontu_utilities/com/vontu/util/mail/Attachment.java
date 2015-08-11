// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.ByteString;

public class Attachment
{
    private String mType;
    private String mSubtype;
    private String mCharset;
    private String mFileName;
    private String mDescription;
    private ByteString mContent;
    
    public Attachment() {
        this.mType = "";
        this.mSubtype = "";
        this.mCharset = "";
        this.mFileName = "";
        this.mDescription = "";
        this.mContent = ByteString.EMPTY_BYTE_STRING;
    }
    
    public String getType() {
        return this.mType;
    }
    
    public String getSubtype() {
        return this.mSubtype;
    }
    
    public void setMediaType(final String type, final String subtype) {
        this.mType = type;
        this.mSubtype = subtype;
    }
    
    public String getCharset() {
        return this.mCharset;
    }
    
    public void setCharset(final String charset) {
        this.mCharset = charset;
    }
    
    public int getSize() {
        return this.mContent.length();
    }
    
    public String getFileName() {
        return this.mFileName;
    }
    
    public void setFileName(final String name) {
        this.mFileName = name;
    }
    
    public String getDescription() {
        return this.mDescription;
    }
    
    public void setDescription(final String desc) {
        this.mDescription = desc;
    }
    
    public ByteString getContent() {
        return this.mContent;
    }
    
    public void setContent(final ByteString buffer) {
        this.mContent = buffer;
    }
}
