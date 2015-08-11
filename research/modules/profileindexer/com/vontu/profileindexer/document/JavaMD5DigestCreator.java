// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class JavaMD5DigestCreator implements MessageDigestCreator
{
    private MessageDigest messageDigest;
    
    JavaMD5DigestCreator() throws NoSuchAlgorithmException {
        this.messageDigest = MessageDigest.getInstance("MD5");
    }
    
    @Override
    public byte[] createMD5Digest(final byte[] content) {
        return this.messageDigest.digest(content);
    }
}
