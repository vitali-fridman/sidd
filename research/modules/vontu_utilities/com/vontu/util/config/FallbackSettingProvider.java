// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

public class FallbackSettingProvider implements SettingProvider
{
    private final SettingProvider _primaryProvider;
    private final SettingProvider _backupProvider;
    
    public FallbackSettingProvider(final SettingProvider primaryProvider, final SettingProvider backupProvider) {
        this._primaryProvider = primaryProvider;
        this._backupProvider = backupProvider;
    }
    
    @Override
    public String getSetting(final String name) {
        return this.fallback(name, this._primaryProvider.getSetting(name));
    }
    
    private String fallback(final String name, final String primaryValue) {
        return this.primarySettingExists(primaryValue) ? primaryValue : this._backupProvider.getSetting(name);
    }
    
    protected boolean primarySettingExists(final String primaryValue) {
        return primaryValue != null;
    }
}
