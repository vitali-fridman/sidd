// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.hash;

import java.security.NoSuchAlgorithmException;
import com.vontu.profileindex.document.docindex.Configuration;

public class HashGeneratorFactory
{
    private Configuration configuration;
    
    public HashGeneratorFactory(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public HashGenerator newGenerator() {
        if (!this.configuration.isUsingJavaMD5()) {
            return new FastMD5Generator(this.configuration.getMaxBinMatchSize());
        }
        try {
            return new JavaMD5HashGenerator(this.configuration.getMaxBinMatchSize());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to create MD5 Digest", e);
        }
    }
}
