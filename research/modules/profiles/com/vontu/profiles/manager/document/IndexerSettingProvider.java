// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.util.config.SettingProvider;

public class IndexerSettingProvider implements SettingProvider
{
    SettingProvider _configSettings;
    SettingProvider _whiteListSettings;
    
    public IndexerSettingProvider(final SettingProvider settingProvider) {
        this._configSettings = settingProvider;
    }
    
    public void insertSettingProvider(final SettingProvider sp) {
        this._whiteListSettings = sp;
    }
    
    public String getSetting(final String name) {
        if (name.equals("common_whitelist_file")) {
            return this._whiteListSettings.getSetting(name);
        }
        if (name.equals("docsource_whitelist_file")) {
            return this._whiteListSettings.getSetting(name);
        }
        return this._configSettings.getSetting(name);
    }
}
