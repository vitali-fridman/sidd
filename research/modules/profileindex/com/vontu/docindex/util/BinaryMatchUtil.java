// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.util;

import java.security.MessageDigest;
import com.twmacinta.util.MD5;

public class BinaryMatchUtil
{
    public static byte[] generateHash(final byte[] bytes, final MD5 md5, final int maxSize) {
        final int numBytes = Math.min(bytes.length, maxSize);
        md5.Init();
        md5.Update(bytes, numBytes);
        return md5.Final();
    }
    
    public static byte[] generateHash(final byte[] bytes, final MessageDigest digester, final int maxSize) {
        final int numBytes = Math.min(bytes.length, maxSize);
        digester.update(bytes, 0, numBytes);
        return digester.digest();
    }
}
