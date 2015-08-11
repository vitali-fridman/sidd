// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.keystore;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;

public class KeystoreEntry
{
    private final KeyStore keystore;
    private final String password;
    
    public KeystoreEntry(final byte[] keystoreBytes, final String password) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        this(new ByteArrayInputStream(keystoreBytes), password);
    }
    
    public KeystoreEntry(final InputStream keystoreStream, final String password) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        (this.keystore = KeyStore.getInstance("JKS")).load(keystoreStream, password.toCharArray());
        this.password = password;
    }
    
    public static KeystoreEntry getEmptyKeystoreEntry() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        return new KeystoreEntry((InputStream)null, "");
    }
    
    public byte[] getKeystoreBytes() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            this.keystore.store(byteStream, this.password.toCharArray());
            return byteStream.toByteArray();
        }
    }
    
    public KeyStore getKeystore() {
        return this.keystore;
    }
    
    public String getPassword() {
        return this.password;
    }
}
