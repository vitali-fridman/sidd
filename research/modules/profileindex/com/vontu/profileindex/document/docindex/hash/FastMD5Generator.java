// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.hash;

import com.twmacinta.util.MD5;

public class FastMD5Generator implements HashGenerator
{
    private final int maximumContentSize;
    private final MD5 md5;
    
    public FastMD5Generator(final int maximumContentSize) {
        this.maximumContentSize = maximumContentSize;
        this.md5 = new MD5();
    }
    
    @Override
    public byte[] generate(final byte[] bytes) {
        final int numBytesForHashing = Math.min(bytes.length, this.maximumContentSize);
        this.md5.Init();
        this.md5.Update(bytes, numBytesForHashing);
        return this.md5.Final();
    }
}
