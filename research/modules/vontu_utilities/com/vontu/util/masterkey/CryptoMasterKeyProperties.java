// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.masterkey;

import com.vontu.util.Base64;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import com.vontu.util.properties.EncryptedProperties;

public class CryptoMasterKeyProperties extends EncryptedProperties
{
    public static String FILE_LOCATION_PROPERTY;
    public static final String MASTER_KEY_PROPERTY = "crypto.masterkey";
    
    public CryptoMasterKeyProperties() throws IOException {
        super(new File(getFileName()));
    }
    
    public CryptoMasterKeyProperties(final File propertiesFile) {
        super(propertiesFile, new Properties());
    }
    
    public String getEncodedMasterKey() {
        return this.getProperty("crypto.masterkey");
    }
    
    public byte[] getMasterKey() {
        final String encodedKey = this.getEncodedMasterKey();
        return Base64.decode(encodedKey);
    }
    
    public void setEncodedMasterKey(final String encodedMasterKey) throws IOException {
        this.setProperty("crypto.masterkey", encodedMasterKey);
    }
    
    public void setMasterKey(final byte[] masterKey) throws IOException {
        final String encodedKey = Base64.encodeBytes(masterKey);
        this.setEncodedMasterKey(encodedKey);
    }
    
    public File getFile() {
        return this.propertiesFile;
    }
    
    private static String getFileName() {
        return System.getProperty(CryptoMasterKeyProperties.FILE_LOCATION_PROPERTY);
    }
    
    static {
        CryptoMasterKeyProperties.FILE_LOCATION_PROPERTY = "com.vontu.keyignition.properties";
    }
}
