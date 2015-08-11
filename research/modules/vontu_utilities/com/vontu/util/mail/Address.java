// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

public final class Address
{
    private String mInetName;
    private String mDisplayName;
    private String mCharset;
    
    public Address() {
        this.mInetName = "";
        this.mDisplayName = "";
        this.mCharset = "";
    }
    
    public Address(final String inetName, final String displayName, final String charset) {
        this.mInetName = inetName;
        this.mDisplayName = displayName;
        this.mCharset = charset;
    }
    
    public String getInetName() {
        return this.mInetName;
    }
    
    public String getDisplayName() {
        return this.mDisplayName;
    }
    
    public String getCharset() {
        return this.mCharset;
    }
}
