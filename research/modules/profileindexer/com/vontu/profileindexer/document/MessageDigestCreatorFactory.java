// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.security.NoSuchAlgorithmException;

public class MessageDigestCreatorFactory
{
    public static MessageDigestCreator createMessageDigestCreator(final boolean useJavaMD5) {
        if (useJavaMD5) {
            try {
                return new JavaMD5DigestCreator();
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return new MD5DigestCreator();
    }
}
