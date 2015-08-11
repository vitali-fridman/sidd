// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.properties;

import java.security.InvalidAlgorithmParameterException;
import java.io.InputStream;
import javax.crypto.CipherInputStream;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.io.OutputStream;
import javax.crypto.CipherOutputStream;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.File;

public class EncryptedProperties
{
    private static final byte[] _testData;
    protected final File propertiesFile;
    protected final Properties properties;
    
    private static byte[] testMe() {
        final byte[] testResult = new byte[EncryptedProperties._testData.length - 13];
        for (int index = 3; index < EncryptedProperties._testData.length - 10; ++index) {
            testResult[index - 3] = (byte)(EncryptedProperties._testData[index] + EncryptedProperties._testData[index - 1]);
        }
        return testResult;
    }
    
    public EncryptedProperties(final File propertiesFile, final Properties properties) {
        this.propertiesFile = propertiesFile;
        this.properties = properties;
    }
    
    public EncryptedProperties(final File propertiesFile) throws IOException {
        this(propertiesFile, true);
    }
    
    public EncryptedProperties(final File propertiesFile, final boolean encrypted) throws IOException {
        if (encrypted) {
            this.properties = loadProperties(propertiesFile);
        }
        else {
            this.properties = loadUnencryptedProperties(propertiesFile);
        }
        this.propertiesFile = propertiesFile;
    }
    
    public void writePropertiesFile() throws IOException {
        final FileOutputStream propertiesStream = new FileOutputStream(this.propertiesFile);
        CipherOutputStream cipheredPropertiesStream = null;
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            cipher.init(1, new SecretKeySpec(testMe(), "AES"));
            final byte[] iv = cipher.getIV();
            propertiesStream.write(iv);
            cipheredPropertiesStream = new CipherOutputStream(propertiesStream, cipher);
            this.properties.store(cipheredPropertiesStream, null);
        }
        catch (NoSuchPaddingException e) {
            throw new IOException("Password Encryption Error: " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e2) {
            throw new IOException("Password Encryption Error: " + e2.getMessage());
        }
        catch (InvalidKeyException e3) {
            throw new IOException("Password Encryption Error: " + e3.getMessage());
        }
        finally {
            if (cipheredPropertiesStream != null) {
                cipheredPropertiesStream.close();
            }
            else {
                propertiesStream.close();
            }
        }
    }
    
    private static Properties loadProperties(final File propertiesFile) throws IOException {
        final Properties properties = new Properties();
        if (propertiesFile == null || !propertiesFile.exists()) {
            throw new IOException("Password properties file \"" + propertiesFile.getAbsoluteFile() + "\" not found.");
        }
        final FileInputStream propertiesStream = new FileInputStream(propertiesFile);
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            final byte[] iv = new byte[16];
            propertiesStream.read(iv);
            cipher.init(2, new SecretKeySpec(testMe(), "AES"), new IvParameterSpec(iv));
            final CipherInputStream decipheredPropertiesStream = new CipherInputStream(propertiesStream, cipher);
            properties.load(decipheredPropertiesStream);
            return properties;
        }
        catch (NoSuchPaddingException e) {
            throw new IOException("Password Encryption Error: " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e2) {
            throw new IOException("Password Encryption Error: " + e2.getMessage());
        }
        catch (InvalidKeyException e3) {
            throw new IOException("Password Encryption Error: " + e3.getMessage());
        }
        catch (InvalidAlgorithmParameterException e4) {
            throw new IOException("Password Encryption Error: " + e4.getMessage());
        }
        finally {
            propertiesStream.close();
        }
    }
    
    private static Properties loadUnencryptedProperties(final File propertiesFile) throws IOException {
        final Properties properties = new Properties();
        if (propertiesFile != null && propertiesFile.exists()) {
            final FileInputStream propertiesStream = new FileInputStream(propertiesFile);
            try {
                properties.load(propertiesStream);
            }
            finally {
                propertiesStream.close();
            }
        }
        return properties;
    }
    
    public boolean isFileValid() {
        return this.propertiesFile != null && this.propertiesFile.exists();
    }
    
    public void verifyFileIsWriteable() throws IOException {
        if (!this.propertiesFile.exists()) {
            throw new IOException("Properties file \"" + this.propertiesFile.getAbsoluteFile() + "\" does not exist.");
        }
        if (!this.propertiesFile.canWrite()) {
            throw new IOException("Properties file \"" + this.propertiesFile.getAbsoluteFile() + "\" is not writeable.");
        }
    }
    
    public String getProperty(final String key) {
        return this.properties.getProperty(key);
    }
    
    public void setProperty(final String key, final String value) throws IOException {
        this.properties.setProperty(key, value);
        this.writePropertiesFile();
    }
    
    static {
        _testData = new byte[] { 56, 2, 3, -78, -27, 121, 46, -79, 46, -107, -37, 32, -59, 104, -93, -89, -30, 52, 39, 3, -24, -16, 26, -94, -11, 93, 117, -2, 120 };
    }
}
