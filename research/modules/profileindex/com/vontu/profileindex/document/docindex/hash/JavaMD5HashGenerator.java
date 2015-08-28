// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.hash;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class JavaMD5HashGenerator implements HashGenerator
{
    private final int maximumContentSize;
    private final MessageDigest digester;
    
    public JavaMD5HashGenerator(final int maximumContentSize) throws NoSuchAlgorithmException {
        this.maximumContentSize = maximumContentSize;
        this.digester = MessageDigest.getInstance("MD5");
    }
    
    @Override
    public byte[] generate(final byte[] bytes) {
        final int numBytesForHashing = Math.min(bytes.length, this.maximumContentSize);
        this.digester.update(bytes, 0, numBytesForHashing);
        return this.digester.digest();
    }
}
