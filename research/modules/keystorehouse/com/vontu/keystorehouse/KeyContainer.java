// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import com.vontu.security.KeyStorehouseException;
import com.vontu.security.KeyStorehouseError;
import com.vontu.security.crypto.VontuCipherInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.vontu.security.crypto.VontuCipherOutputStream;
import java.io.OutputStream;
import com.vontu.security.crypto.CryptoException;
import com.vontu.util.config.ConfigurationException;
import com.vontu.security.crypto.Digester;
import com.vontu.security.crypto.AesCBCCipher;
import com.vontu.security.crypto.AesCipher;

public final class KeyContainer
{
    public static final int DIGEST_BYTE_LENGTH = 20;
    private final String _alias;
    private final AesCipher _cipher;
    private final AesCBCCipher _cbcCipher;
    private final Digester _digester;
    private final byte[] _keyBytes;
    
    public KeyContainer(final String alias, final byte[] keyBytes, final KeyContainerConfiguration configuration) throws ConfigurationException {
        this(alias, keyBytes, configuration.createCipher(), configuration.createCBCCipher(), configuration.createDigester(keyBytes));
    }
    
    public KeyContainer(final String alias, final byte[] keyBytes, final AesCipher cipher, final AesCBCCipher cbcCipher, final Digester digester) {
        this._alias = alias;
        this._cipher = cipher;
        this._cbcCipher = cbcCipher;
        this._digester = digester;
        System.arraycopy(keyBytes, 0, this._keyBytes = new byte[keyBytes.length], 0, this._keyBytes.length);
    }
    
    public byte[] computeDigest(final String toBeSigned) throws CryptoException {
        return this._digester.computeDigest(toBeSigned);
    }
    
    public byte[] decrypt(final byte[] encryptedBytes) throws CryptoException {
        return this._cipher.decrypt(this.getCipherKey(), encryptedBytes);
    }
    
    public byte[] cbcDecrypt(final byte[] encryptedBytes) throws CryptoException {
        return this._cbcCipher.decrypt(this.getCipherKey(), encryptedBytes);
    }
    
    public byte[] cbcEncrypt(final byte[] clearBytes) throws CryptoException {
        return this._cbcCipher.encrypt(this.getCipherKey(), clearBytes);
    }
    
    public byte[] encrypt(final byte[] clearTextBytes) throws CryptoException {
        return this._cipher.encrypt(this.getCipherKey(), this.padForEncryption(clearTextBytes));
    }
    
    public VontuCipherOutputStream encryptOutputStream(final OutputStream out) throws CryptoException, IOException {
        return this._cbcCipher.encryptOutputStream(this.getCipherKey(), out);
    }
    
    public VontuCipherInputStream encryptInputStream(final InputStream rawStream) throws CryptoException {
        return this._cbcCipher.encryptInputStream(this.getCipherKey(), rawStream);
    }
    
    public InputStream decryptInputStream(final InputStream inputStream) throws CryptoException, IOException {
        return this._cbcCipher.decryptInputStream(this.getCipherKey(), inputStream);
    }
    
    @Override
    protected void finalize() throws Throwable {
        for (int i = 0; i < this._keyBytes.length; ++i) {
            final byte[] keyBytes = this._keyBytes;
            final int n = i;
            keyBytes[n] ^= (byte)255;
            this._keyBytes[i] = -1;
            this._keyBytes[i] = 0;
        }
        super.finalize();
    }
    
    public String getAlias() {
        return this._alias;
    }
    
    private byte[] getCipherKey() {
        if (this._keyBytes.length > this._cipher.getBlockSize()) {
            final byte[] keyBytes = new byte[this._cipher.getBlockSize()];
            System.arraycopy(this._keyBytes, 0, keyBytes, 0, keyBytes.length);
            return keyBytes;
        }
        return this._keyBytes;
    }
    
    byte[] getHmacKey() throws KeyStorehouseException {
        if (!this._alias.toLowerCase().startsWith("EXTERNAL".toLowerCase() + ".")) {
            throw new KeyStorehouseException(KeyStorehouseError.KEYSTORE_KEY_GET_ERROR, (Object)this._alias);
        }
        final byte[] keyBytes = new byte[this._keyBytes.length];
        System.arraycopy(this._keyBytes, 0, keyBytes, 0, this._keyBytes.length);
        return keyBytes;
    }
    
    private byte[] padForEncryption(final byte[] clearTextBytes) {
        if (clearTextBytes.length % this._cipher.getBlockSize() == 0) {
            return clearTextBytes;
        }
        return this._cipher.padData(clearTextBytes);
    }
}
