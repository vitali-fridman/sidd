// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.jar.Manifest;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Collections;
import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.List;
import java.util.jar.Attributes;
import java.util.logging.Logger;

public class JarManager
{
    private static final Logger _logger;
    private static final Attributes.Name SPECIFICATION_TITLE;
    private final List<JarFile> _jars;
    private final List<URL> _urls;
    private final ClassLoader _parentClassLoader;
    
    public JarManager(final ClassLoader parentClassLoader, final File pluginsFolder, final List<String> acceptableJars) {
        this._parentClassLoader = parentClassLoader;
        if (acceptableJars.isEmpty()) {
            this._jars = Collections.emptyList();
            this._urls = Collections.emptyList();
        }
        else {
            final PluginJarFilter jarFilter = new PluginJarFilter(acceptableJars);
            final File[] files = pluginsFolder.listFiles(jarFilter);
            this._jars = new ArrayList<JarFile>();
            this._urls = new ArrayList<URL>();
            for (final File file : files) {
                try {
                    this._jars.add(new JarFile(file));
                    this._urls.add(file.toURI().toURL());
                }
                catch (IOException e) {
                    JarManager._logger.log(Level.WARNING, '\"' + file.getName() + "\" can't be loaded.", e);
                }
            }
            Collections.sort(this._jars, new PluginComparator(acceptableJars));
        }
    }
    
    public List<JarFile> getJars() {
        return this._jars;
    }
    
    public URL[] getUrls() {
        return this._urls.toArray(new URL[this._urls.size()]);
    }
    
    public ClassLoader getClassLoader() {
        return new URLClassLoader(this.getUrls(), this._parentClassLoader);
    }
    
    public ClassLoader getClassLoader(final JarFile jar) {
        final int index = this._jars.indexOf(jar);
        return new URLClassLoader(new URL[] { this._urls.get(index) }, this._parentClassLoader);
    }
    
    static {
        _logger = Logger.getLogger(JarManager.class.getName());
        SPECIFICATION_TITLE = new Attributes.Name("Specification-Title");
    }
    
    private static final class PluginJarFilter implements FilenameFilter
    {
        private final Set<String> _jarSpecifications;
        
        public PluginJarFilter(final Collection<String> acceptableJars) {
            this._jarSpecifications = new HashSet<String>(acceptableJars.size());
            for (final String anAcceptableJar : acceptableJars) {
                this._jarSpecifications.add(anAcceptableJar.trim());
            }
        }
        
        @Override
        public boolean accept(final File dir, final String name) {
            if (name.endsWith(".jar")) {
                final File file = new File(dir, name);
                try {
                    final JarFile jar = new JarFile(file);
                    if (!this._jarSpecifications.contains(name)) {
                        final Manifest manifest = jar.getManifest();
                        if (manifest == null) {
                            final String logMessage = "Cannot accept this jar file because it has no manifest: {0}";
                            JarManager._logger.log(Level.FINEST, logMessage, file);
                            return false;
                        }
                        final String specificationTitle = manifest.getMainAttributes().getValue(JarManager.SPECIFICATION_TITLE);
                        if (!this._jarSpecifications.contains(specificationTitle)) {
                            final String logMessage2 = "Cannot accept this jar file because its specification title is not in the jar specifications list: {0}";
                            JarManager._logger.log(Level.FINEST, logMessage2, specificationTitle);
                            return false;
                        }
                    }
                    return true;
                }
                catch (IOException e) {
                    JarManager._logger.log(Level.WARNING, '\"' + name + "\" can't be loaded.", e);
                }
            }
            return false;
        }
    }
    
    private static final class PluginComparator implements Comparator<JarFile>
    {
        private final List<String> pluginJars;
        
        public PluginComparator(final List<String> pluginJars) {
            this.pluginJars = pluginJars;
        }
        
        private static String getTitle(final JarFile jar) throws IOException {
            final Manifest manifest = jar.getManifest();
            final String title = (manifest == null) ? null : manifest.getMainAttributes().getValue(JarManager.SPECIFICATION_TITLE);
            return (title == null) ? jar.getName() : title;
        }
        
        @Override
        public int compare(final JarFile o1, final JarFile o2) {
            try {
                return this.pluginJars.indexOf(getTitle(o1)) - this.pluginJars.indexOf(getTitle(o2));
            }
            catch (IOException e) {
                return 0;
            }
        }
    }
}
