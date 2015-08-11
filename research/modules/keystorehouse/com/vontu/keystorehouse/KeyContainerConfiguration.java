// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import com.vontu.security.crypto.FastHash;
import com.vontu.util.config.SettingReader;
import com.vontu.security.crypto.HmacDigester;
import com.vontu.security.crypto.Digester;
import com.vontu.security.crypto.AesCBCCipher;
import com.vontu.util.config.ConfigurationException;
import com.vontu.security.crypto.AesCipher;
import com.vontu.util.config.SettingProvider;

public class KeyContainerConfiguration
{
    public static final String CIPHER_PROVIDER_PROPERTY = "cipher_provider";
    public static final String MAC_PROVIDER_PROPERTY = "mac_provider";
    public static final String USE_FAST_HASH_PROPERTY = "use_fast_hash";
    public static final boolean USE_FAST_HASH_DEFAULT = false;
    private final SettingProvider _settings;
    
    public KeyContainerConfiguration(final SettingProvider settings) {
        this._settings = settings;
    }
    
    public AesCipher createCipher() throws ConfigurationException {
        if (this._settings == null) {
            return new AesCipher((String)null);
        }
        return new AesCipher(this._settings.getSetting("cipher_provider"));
    }
    
    public AesCBCCipher createCBCCipher() throws ConfigurationException {
        if (this._settings == null) {
            return new AesCBCCipher((String)null);
        }
        return new AesCBCCipher(this._settings.getSetting("cipher_provider"));
    }
    
    public AesCBCCipher createCBCCipher(final AesCBCCipher.Padding padding) throws ConfigurationException {
        if (this._settings == null) {
            return new AesCBCCipher((String)null, padding);
        }
        return new AesCBCCipher(this._settings.getSetting("cipher_provider"), padding);
    }
    
    public Digester createDigester(final byte[] keyBytes) {
        if (this._settings == null) {
            return (Digester)new HmacDigester((String)null, keyBytes);
        }
        final SettingReader settingReader = new SettingReader(this._settings);
        if (settingReader.getBooleanSetting("use_fast_hash", false)) {
            return (Digester)new FastHash();
        }
        return (Digester)new HmacDigester(this._settings.getSetting("mac_provider"), keyBytes);
    }
}
