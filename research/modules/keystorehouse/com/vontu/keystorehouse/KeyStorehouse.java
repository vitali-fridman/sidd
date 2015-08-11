// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import java.util.Date;
import java.util.Enumeration;
import com.vontu.util.Base64;
import com.vontu.security.KeyStorehouseError;
import com.vontu.security.KeyStorehouseException;
import edu.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SettingProvider;
import com.vontu.security.keystorecontainer.KeyStoreContainer;
import com.vontu.security.crypto.KeyFactory;
import java.util.Map;

public class KeyStorehouse implements KeyIgnition
{
    public static final String PROPERTIES_FILE = "com.vontu.keystorehouse.KeyStorehouse.properties";
    public static final String RNG_PROVIDER_PROPERTY = "rng_provider";
    private static KeyStorehouse _theInstance;
    private boolean _isIgnited;
    private final Map<String, KeyContainer> _keyCache;
    private final KeyFactory _keyFactory;
    private final KeyContainerConfiguration _keyContainerConfig;
    private final KeyStoreContainer _keyStoreContainer;
    private final KeyIdentity _keyIdentity;
    private final boolean _onMonitor;
    private final String _keyTypePrefix;
    static final String KEY_TYPE_PROPERTY = "keystorehouse_key_type";
    static final String KEY_TYPE_DEFAULT = "SYSTEM";
    static final String KEY_TYPE_EXTERNAL = "EXTERNAL";
    
    public static KeyStorehouse createInstance(final SettingProvider settingProvider, final String keyStoreProviderProperty) throws ConfigurationException {
        synchronized (KeyStorehouse.class) {
            final String keyType = settingProvider.getSetting("keystorehouse_key_type");
            if (keyType == null || keyType.equals("SYSTEM")) {
                if (KeyStorehouse._theInstance == null) {
                    KeyStorehouse._theInstance = new KeyStorehouse(settingProvider, keyStoreProviderProperty, keyType);
                }
                return KeyStorehouse._theInstance;
            }
            return new KeyStorehouse(settingProvider, keyStoreProviderProperty, keyType);
        }
    }
    
    public static KeyStorehouse createInstance(final SettingProvider settingProvider) throws ConfigurationException {
        return createInstance(settingProvider, "key_store_provider");
    }
    
    public static KeyStorehouse getInstance() {
        synchronized (KeyStorehouse.class) {
            return KeyStorehouse._theInstance;
        }
    }
    
    private KeyStorehouse(final SettingProvider settingProvider, final String keyStoreProviderProperty, final String keyType) throws ConfigurationException {
        this._keyTypePrefix = computeKeyTypePrefix(keyType);
        this._keyCache = (Map<String, KeyContainer>)new ConcurrentReaderHashMap();
        this._keyFactory = new KeyFactory(settingProvider.getSetting("rng_provider"));
        final KeyStoreFactory keyStoreFactory = new KeyStoreFactory(settingProvider);
        this._keyStoreContainer = keyStoreFactory.createKeyStore(keyStoreProviderProperty);
        this._keyContainerConfig = new KeyContainerConfiguration(settingProvider);
        if (this.isMonitorKeystoreProvider(keyStoreProviderProperty)) {
            this._keyIdentity = null;
            this._onMonitor = true;
        }
        else {
            final KeyIdentityFactory keyIdentityFactory = new KeyIdentityFactory(settingProvider);
            this._keyIdentity = keyIdentityFactory.createKeyIdentity();
            this._onMonitor = false;
        }
    }
    
    private boolean isMonitorKeystoreProvider(final String keyStoreProviderProperty) {
        return keyStoreProviderProperty.equals("aggregator_key_store_provider") || keyStoreProviderProperty.equals("filereader_key_store_provider") || keyStoreProviderProperty.equals("requestprocessor_key_store_provider");
    }
    
    public void addNewKey(final String alias) throws KeyStorehouseException, ConfigurationException {
        final byte[] keyBytes = this._keyFactory.newHmacKey();
        this._keyStoreContainer.addKey(alias, keyBytes);
        this.addKeyToCache(new KeyContainer(alias, keyBytes, this._keyContainerConfig));
    }
    
    private void addKeyToCache(final KeyContainer hKey) {
        this._keyCache.put(hKey.getAlias(), hKey);
    }
    
    private void checkIsIgnited() throws KeyStorehouseException {
        if (!this._isIgnited) {
            throw new KeyStorehouseException(KeyStorehouseError.KEYSTORE_NOT_IGNITED);
        }
    }
    
    @Override
    public void forceNotIgnited() {
        synchronized (KeyStorehouse.class) {
            this._isIgnited = false;
        }
    }
    
    public KeyContainer getKeyContainerWithLatestKeyAlias() throws KeyStorehouseException {
        return this.getKeyContainer(this.getLatestKeyAlias());
    }
    
    public KeyContainer getKeyContainer(final String keyAlias) throws KeyStorehouseException {
        synchronized (KeyStorehouse.class) {
            this.checkIsIgnited();
            KeyContainer keyContainer = this._keyCache.get(keyAlias);
            if (keyContainer == null) {
                if (this._keyStoreContainer.getKey(keyAlias) == null) {
                    throw new KeyStorehouseException(KeyStorehouseError.KEYSTORE_UNAVAILABLE);
                }
                keyContainer = new KeyContainer(keyAlias, this._keyStoreContainer.getKey(keyAlias), this._keyContainerConfig);
                this.addKeyToCache(keyContainer);
            }
            return keyContainer;
        }
    }
    
    @Override
    public void igniteKeys(final byte[] ignitionKey) throws KeyStorehouseException {
        synchronized (KeyStorehouse.class) {
            this._keyStoreContainer.ignite(Base64.encodeBytes(ignitionKey));
            this.updateCache();
            this._isIgnited = true;
        }
    }
    
    @Override
    public boolean isIgnited() {
        synchronized (KeyStorehouse.class) {
            return this._isIgnited;
        }
    }
    
    public static boolean isInstantiatedAndIgnited() {
        synchronized (KeyStorehouse.class) {
            final KeyIgnition keyStorehouse = getInstance();
            return keyStorehouse != null && keyStorehouse.isIgnited();
        }
    }
    
    private void updateCache() throws KeyStorehouseException {
        final Enumeration<String> en = (Enumeration<String>)this._keyStoreContainer.aliases();
        if (en == null) {
            return;
        }
        while (en.hasMoreElements()) {
            final String alias = en.nextElement();
            final byte[] hkey = this._keyStoreContainer.getKey(alias);
            this.addKeyToCache(new KeyContainer(alias, hkey, this._keyContainerConfig));
        }
    }
    
    public static void releaseInstance() {
        synchronized (KeyStorehouse.class) {
            if (KeyStorehouse._theInstance != null) {
                KeyStorehouse._theInstance.forceNotIgnited();
                KeyStorehouse._theInstance = null;
            }
        }
    }
    
    public String getLatestKeyAlias() throws KeyStorehouseException {
        synchronized (KeyStorehouse.class) {
            this.checkIsIgnited();
            String alias = null;
            if (this._onMonitor) {
                final Enumeration<String> aliases = (Enumeration<String>)this._keyStoreContainer.aliases();
                if (aliases == null) {
                    throw new KeyStorehouseException(KeyStorehouseError.KEYSTORE_KEY_IDENTITY_ERROR);
                }
                Date latestCreateDate = new Date(0L);
                while (aliases.hasMoreElements()) {
                    final String thisAlias = aliases.nextElement();
                    final Date createDate = this._keyStoreContainer.getCreationDate(thisAlias);
                    if (latestCreateDate.before(createDate) && thisAlias.startsWith(this._keyTypePrefix)) {
                        latestCreateDate = createDate;
                        alias = thisAlias;
                    }
                }
            }
            else {
                try {
                    alias = this._keyIdentity.getLatestAlias();
                    final KeyContainer key = this._keyCache.get(alias);
                    if (key == null) {
                        this.addNewKey(alias);
                    }
                }
                catch (KeyIdentityException e) {
                    throw new KeyStorehouseException(KeyStorehouseError.KEYSTORE_KEY_IDENTITY_ERROR, (Throwable)e);
                }
            }
            return alias;
        }
    }
    
    Date getCreationDate(final String alias) throws KeyStorehouseException {
        synchronized (KeyStorehouse.class) {
            return this._keyStoreContainer.getCreationDate(alias);
        }
    }
    
    static String computeKeyTypePrefix(final String type) {
        if (type == null || "SYSTEM".equals(type)) {
            return "";
        }
        return type + ".";
    }
}
