// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.HashMap;
import java.util.Map;

public class SimpleSettingProvider implements SettingProvider
{
    private final Map<String, String> _settings;
    
    public SimpleSettingProvider() {
        this._settings = new HashMap<String, String>();
    }
    
    @Override
    public String getSetting(final String name) {
        return this._settings.get(name);
    }
    
    public void setSetting(final String name, final String value) {
        this._settings.put(name, value);
    }
}
