// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

public class NullSettingProvider implements SettingProvider
{
    @Override
    public String getSetting(final String name) {
        return null;
    }
}
