// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.login;

import java.util.Map;
import com.vontu.util.config.PropertySettingProvider;
import java.util.Properties;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoginConfig
{
    public static final String PROPERTIES = "com.vontu.auth.config.properties.uri";
    public static final String DUMMY_PASSWORD = "dummypassword";
    private String _dummyPassword;
    
    public LoginConfig() throws URISyntaxException, IOException {
        this.initialize(load());
    }
    
    private void initialize(final Properties properties) {
        this._dummyPassword = properties.getProperty("dummypassword");
        if (null != this._dummyPassword && this._dummyPassword.length() > 0) {
            this._dummyPassword = this._dummyPassword.trim();
        }
    }
    
    private static Properties load() throws IOException {
        final Properties load = new Properties();
        load.putAll(new PropertySettingProvider("com.vontu.auth.config.properties.uri").getPropertyMap());
        return load;
    }
    
    public String getDummyPassword() {
        return this._dummyPassword;
    }
}
