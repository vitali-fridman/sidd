// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.HashMap;
import java.util.Map;

public class NameTranslator implements SettingProvider
{
    private final SettingProvider _settingProvider;
    private final Map<String, String> _namesMap;
    
    public NameTranslator(final SettingProvider settingProvider, final Map<String, String> namesMap) {
        this._settingProvider = settingProvider;
        this._namesMap = new HashMap<String, String>(namesMap);
    }
    
    @Override
    public String getSetting(final String name) {
        return this._settingProvider.getSetting(this.translateSettingName(name));
    }
    
    private String translateSettingName(final String settingName) {
        if (this._namesMap.containsKey(settingName)) {
            return this._namesMap.get(settingName);
        }
        return settingName;
    }
}
