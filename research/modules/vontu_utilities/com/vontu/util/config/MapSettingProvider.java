// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.Map;

public final class MapSettingProvider implements SettingProvider
{
    private final Map _settings;
    
    public MapSettingProvider(final Map settings) {
        this._settings = settings;
    }
    
    @Override
    public String getSetting(final String name) {
        return (String) this._settings.get(name);
    }
}
