// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.HashMap;
import com.vontu.util.config.SystemSettingProvider;
import java.util.StringTokenizer;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.IOException;
import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SystemProperties;
import java.util.Collection;
import java.util.Map;
import com.vontu.util.config.SettingReader;
import java.util.logging.Logger;

public final class PluginLoader
{
    private static final Logger _logger;
    private static final SettingReader _settings;
    private static Map<Class<?>, Collection<String>> _jarCatalog;
    
    public static <T> void clearPluginsProperties(final PluginDescriptor<T> descriptor) {
        try {
            SystemProperties.clear(PluginLoader._settings.getSetting(descriptor.getPluginProperties()));
        }
        catch (IOException e) {
            throw new ConfigurationException("Failed to clear plug-in configuration properties.", e);
        }
        catch (ConfigurationException cexp) {
            PluginLoader._logger.log(Level.INFO, "Plugin parameters not loaded");
        }
    }
    
    public static <T> Collection<Class<T>> loadPlugins(final PluginDescriptor<T> descriptor) throws ConfigurationException {
        final List<String> pluginJarDependencies = new ArrayList<String>();
        return loadPlugins(descriptor, pluginJarDependencies, true);
    }
    
    public static <T> Collection<Class<T>> loadPlugins(final PluginDescriptor<T> descriptor, final List<String> pluginJarDependencies, final boolean useSharedClassloader) throws ConfigurationException {
        final String pluginDirectory = descriptor.getPluginDirectory();
        final File pluginsFolder = PluginLoader._settings.getFolderSetting(pluginDirectory);
        final String logMessage = "Loading plugins.  Plugin directory setting: {0}, Plugins folder: {1}";
        PluginLoader._logger.log(Level.FINE, logMessage, new Object[] { pluginDirectory, pluginsFolder });
        final PluginPropertiesLoader propertyLoader = new PluginPropertiesLoader();
        propertyLoader.loadPluginProperties(descriptor);
        final String plugins = PluginLoader._settings.getSetting(descriptor.getName() + ".plugins", "");
        List<String> pluginsList;
        if (!plugins.isEmpty()) {
            pluginsList = Arrays.asList(plugins.split(","));
        }
        else {
            pluginsList = Collections.emptyList();
        }
        final JarManager manager = new JarManager(descriptor.getClassLoader(), pluginsFolder, pluginsList);
        ClassLoader loader = null;
        if (useSharedClassloader) {
            loader = manager.getClassLoader();
        }
        PluginLoader._jarCatalog.clear();
        final List<Class<T>> pluginClasses = new ArrayList<Class<T>>();
        for (final JarFile jar : manager.getJars()) {
            try {
                if (!useSharedClassloader) {
                    loader = manager.getClassLoader(jar);
                }
                boolean isPluginJar = false;
                final Enumeration<JarEntry> entryIterator = jar.entries();
                while (entryIterator.hasMoreElements()) {
                    final JarEntry entry = entryIterator.nextElement();
                    final String entryName = entry.getName();
                    if (!entry.isDirectory() && entryName.endsWith(".class")) {
                        final String className = entryName.substring(0, entryName.lastIndexOf(".class")).replace('/', '.');
                        if (!addIfClassIsAPlugin(className, pluginClasses, descriptor, loader, jar)) {
                            continue;
                        }
                        isPluginJar = true;
                    }
                }
                if (!isPluginJar) {
                    final String jarName = new File(jar.getName()).getName();
                    pluginJarDependencies.add(jarName);
                }
            }
            finally {
                try {
                    jar.close();
                }
                catch (IOException e) {
                    PluginLoader._logger.log(Level.WARNING, "Failed to close JAR \"" + jar.getName() + "\".", e);
                }
            }
        }
        return pluginClasses;
    }
    
    private static <T> boolean addIfClassIsAPlugin(final String className, final List<Class<T>> pluginClasses, final PluginDescriptor<T> descriptor, final ClassLoader loader, final JarFile jar) {
        try {
            final Class<T> cls = (Class<T>)loader.loadClass(className);
            if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
                return false;
            }
            if (descriptor.isAssignableFrom(cls)) {
                pluginClasses.add(cls);
                addJarToCatalog(cls, jar.getName());
                return true;
            }
        }
        catch (ClassNotFoundException e) {
            PluginLoader._logger.log(Level.WARNING, "Unable to load class " + className + '.', e);
        }
        catch (NoClassDefFoundError e2) {
            PluginLoader._logger.warning("Unable to locate dependency of class " + className + ": " + e2.getMessage() + '.');
        }
        return false;
    }
    
    private static <T> void addJarToCatalog(final Class<T> pluginClass, final String jarName) {
        Collection<String> currentJars = PluginLoader._jarCatalog.get(pluginClass);
        if (currentJars == null) {
            currentJars = new ArrayList<String>();
            PluginLoader._jarCatalog.put(pluginClass, currentJars);
        }
        currentJars.add(jarName);
    }
    
    public static List<String> getPluginChain() {
        final String pluginsList = System.getProperty("com.vontu.plugins.execution.chain");
        if (pluginsList == null || pluginsList.length() == 0) {
            return Collections.emptyList();
        }
        final List<String> list = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(pluginsList, ",");
        while (st.hasMoreElements()) {
            final String entry = st.nextToken();
            list.add(entry.trim());
        }
        return list;
    }
    
    public static <T> Collection<String> getJarPaths(final Class<T> pluginClass) {
        final Collection<String> paths = PluginLoader._jarCatalog.get(pluginClass);
        if (paths == null) {
            return (Collection<String>)Collections.<String>emptyList();
        }
        return paths;
    }
    
    static {
        _logger = Logger.getLogger(PluginLoader.class.getName());
        _settings = SystemSettingProvider.getReader(PluginLoader._logger);
        PluginLoader._jarCatalog = new HashMap<Class<?>, Collection<String>>();
    }
}
