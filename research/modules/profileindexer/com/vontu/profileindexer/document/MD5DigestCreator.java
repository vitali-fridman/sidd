// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import com.twmacinta.util.MD5;

public class MD5DigestCreator implements MessageDigestCreator
{
    private MD5 md5;
    
    public MD5DigestCreator() {
        this.md5 = new MD5();
    }
    
    @Override
    public byte[] createMD5Digest(final byte[] content) {
        this.md5.Init();
        this.md5.Update(content);
        return this.md5.Final();
    }
}
