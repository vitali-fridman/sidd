// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;

public class SimpleKeyIdentity implements KeyIdentity
{
    public static final String FIXED_ALIAS_PROPERTY = "fixed_alias";
    private static final Logger _logger;
    private final String _fixedAlias;
    
    public SimpleKeyIdentity(final SettingProvider settingProvider) throws ConfigurationException {
        this(new SettingReader(settingProvider, SimpleKeyIdentity._logger));
    }
    
    private SimpleKeyIdentity(final SettingReader settingReader) throws ConfigurationException {
        this._fixedAlias = settingReader.getSetting("fixed_alias");
    }
    
    @Override
    public String getLatestAlias() {
        return this._fixedAlias;
    }
    
    static {
        _logger = Logger.getLogger(SimpleKeyIdentity.class.getName());
    }
}
