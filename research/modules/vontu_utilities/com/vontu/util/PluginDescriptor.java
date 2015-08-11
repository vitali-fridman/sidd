// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class PluginDescriptor<T>
{
    private final Class<T> _pluginInterface;
    private final String _pluginDirectory;
    private final String _pluginProperties;
    
    public PluginDescriptor(final Class<T> pluginInterface, final String pluginDirectory, final String pluginProperties) {
        this._pluginDirectory = pluginDirectory;
        this._pluginInterface = pluginInterface;
        this._pluginProperties = pluginProperties;
    }
    
    public String getName() {
        return this._pluginInterface.getName();
    }
    
    public ClassLoader getClassLoader() {
        return this._pluginInterface.getClassLoader();
    }
    
    boolean isAssignableFrom(final Class<T> cls) {
        return this._pluginInterface.isAssignableFrom(cls);
    }
    
    public String getPluginDirectory() {
        return this._pluginDirectory;
    }
    
    public String getPluginProperties() {
        return this._pluginProperties;
    }
}
