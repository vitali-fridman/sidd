// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import java.util.HashMap;
import java.io.File;
import java.security.Provider;
import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import com.vontu.security.KeyStorehouseError;
import com.vontu.util.config.ConfigurationException;
import com.vontu.security.keystorecontainer.FileKeyStore;
import com.vontu.security.keystorecontainer.FilelessKeyStore;
import java.security.Security;
import com.vontu.security.keystorecontainer.KeyStoreContainer;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SettingReader;
import java.security.KeyStore;
import java.util.Map;
import java.util.logging.Logger;

public class KeyStoreFactory
{
    public static final String KEY_STORE_FILE_PROPERTY = "key_store_file";
    public static final String KEY_STORE_FILE_DEFAULT;
    public static final String KEY_STORE_PROVIDER_PROPERTY = "key_store_provider";
    public static final String FILEREADER_KEYSTORE_PROVIDER_PROPERTY = "filereader_key_store_provider";
    public static final String AGGREGATOR_KEYSTORE_PROVIDER_PROPERTY = "aggregator_key_store_provider";
    public static final String REQUESTPROCESSOR_KEYSTORE_PROVIDER_PROPERTY = "requestprocessor_key_store_provider";
    private static final String VONTU_KEYSTORE_PROVIDER = "VontuJCEKS";
    private static final String VONTU_KEYSTORE_PROVIDER_CLASS = "com.vontu.jceks.VontuJCEKS";
    private static final String KEY_STORE_PROVIDER_DEFAULT = "VontuJCEKS";
    private static final String FILEREADER_KEYSTORE_PROVIDER = "FileReaderJCEKS";
    private static final String FILEREADER_KEYSTORE_PROVIDER_CLASS = "com.vontu.messaging.FileReaderJCEKS";
    private static final String AGGREGATOR_KEYSTORE_PROVIDER = "AggregatorJCEKS";
    private static final String AGGREGATOR_KEYSTORE_PROVIDER_CLASS = "com.vontu.aggregator.keyprovider.AggregatorJCEKS";
    private static final String REQUESTPROCESSOR_KEYSTORE_PROVIDER = "RequestProcessorJCEKS";
    private static final String REQUESTPROCESSOR_KEYSTORE_PROVIDER_CLASS = "com.vontu.mta.keyprovider.RequestProcessorJCEKS";
    private static final Logger _logger;
    private static final Map<String, KeyStore> _providerClasses;
    private final SettingReader _settingReader;
    
    public KeyStoreFactory(final SettingProvider settingProvider) {
        this._settingReader = new SettingReader(settingProvider, KeyStoreFactory._logger);
    }
    
    KeyStoreContainer createKeyStore(final String keyStoreProviderProperty) throws ConfigurationException {
        final String providerName = this._settingReader.getSetting(keyStoreProviderProperty, "VontuJCEKS");
        final String keyType = this._settingReader.settingExists("keystorehouse_key_type") ? this._settingReader.getSetting("keystorehouse_key_type") : "SYSTEM";
        final String keyTypePrefix = KeyStorehouse.computeKeyTypePrefix(keyType);
        if ("VontuJCEKS".equals(providerName)) {
            if (!KeyStoreFactory._providerClasses.containsKey(providerName)) {
                Security.addProvider(createProvider("com.vontu.jceks.VontuJCEKS"));
                KeyStoreFactory._providerClasses.put(providerName, createKeyStoreImpl(providerName));
            }
            return (KeyStoreContainer)new FilelessKeyStore((KeyStore)KeyStoreFactory._providerClasses.get(providerName), keyTypePrefix);
        }
        if ("FileReaderJCEKS".equals(providerName)) {
            if (!KeyStoreFactory._providerClasses.containsKey(providerName)) {
                Security.addProvider(createProvider("com.vontu.messaging.FileReaderJCEKS"));
                KeyStoreFactory._providerClasses.put(providerName, createKeyStoreImpl(providerName));
            }
            return (KeyStoreContainer)new FilelessKeyStore((KeyStore)KeyStoreFactory._providerClasses.get(providerName), keyTypePrefix);
        }
        if ("AggregatorJCEKS".equals(providerName)) {
            if (!KeyStoreFactory._providerClasses.containsKey(providerName)) {
                Security.addProvider(createProvider("com.vontu.aggregator.keyprovider.AggregatorJCEKS"));
                KeyStoreFactory._providerClasses.put(providerName, createKeyStoreImpl(providerName));
            }
            return (KeyStoreContainer)new FilelessKeyStore((KeyStore)KeyStoreFactory._providerClasses.get(providerName), keyTypePrefix);
        }
        if ("RequestProcessorJCEKS".equals(providerName)) {
            if (!KeyStoreFactory._providerClasses.containsKey(providerName)) {
                Security.addProvider(createProvider("com.vontu.mta.keyprovider.RequestProcessorJCEKS"));
                KeyStoreFactory._providerClasses.put(providerName, createKeyStoreImpl(providerName));
            }
            return (KeyStoreContainer)new FilelessKeyStore((KeyStore)KeyStoreFactory._providerClasses.get(providerName), keyTypePrefix);
        }
        return (KeyStoreContainer)new FileKeyStore(createKeyStoreImpl(providerName), this._settingReader.getSetting("key_store_file", KeyStoreFactory.KEY_STORE_FILE_DEFAULT));
    }
    
    private static KeyStore createKeyStoreImpl(final String providerName) {
        try {
            if (providerName == null) {
                return KeyStore.getInstance("JCEKS");
            }
            return KeyStore.getInstance("JCEKS", providerName);
        }
        catch (KeyStoreException e) {
            throw new ConfigurationException(KeyStorehouseError.KEYSTORE_UNAVAILABLE.getDescription(), (Throwable)e);
        }
        catch (NoSuchProviderException e2) {
            throw new ConfigurationException(KeyStorehouseError.KEYSTORE_UNKNOWN_PROVIDER.getDescription(new Object[] { providerName }), (Throwable)e2);
        }
    }
    
    private static Provider createProvider(final String className) throws ConfigurationException {
        try {
            return (Provider)Class.forName(className).newInstance();
        }
        catch (Exception e) {
            throw new ConfigurationException(KeyStorehouseError.KEYSTORE_PROVIDER_ERROR.getDescription(new Object[] { className }), (Throwable)e);
        }
    }
    
    static {
        KEY_STORE_FILE_DEFAULT = new File(System.getProperty("user.home"), ".keystore").getAbsolutePath();
        _logger = Logger.getLogger(KeyStoreFactory.class.getName());
        _providerClasses = new HashMap<String, KeyStore>();
    }
}
