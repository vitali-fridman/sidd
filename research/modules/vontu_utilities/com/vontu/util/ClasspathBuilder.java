// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Iterator;
import java.io.File;
import java.util.ArrayList;

public class ClasspathBuilder
{
    private ArrayList<File> _paths;
    
    public ClasspathBuilder() {
        this._paths = new ArrayList<File>();
    }
    
    public void addClassesDirectory(final File classesDirectory) {
        this.addPath(classesDirectory);
    }
    
    public void addJarDirectory(final File jarDirectory) {
        if (jarDirectory.isDirectory()) {
            final File[] files = jarDirectory.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    this.addJarDirectory(files[i]);
                }
                else {
                    final String lowerCaseName = files[i].getName().toLowerCase();
                    if (lowerCaseName.endsWith(".zip") || lowerCaseName.endsWith(".jar")) {
                        this.addJar(files[i]);
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException(jarDirectory.getAbsolutePath() + " is not a directory");
    }
    
    public void addJar(final File jar) {
        this.addPath(jar);
    }
    
    private void addPath(final File path) {
        if (path.exists()) {
            this._paths.add(path);
            return;
        }
        throw new IllegalArgumentException(path.getAbsolutePath() + " does not exist");
    }
    
    public String createClasspath() {
        final StringBuffer classpath = new StringBuffer();
        int pathCount = 0;
        final Iterator<File> iter = this._paths.iterator();
        while (iter.hasNext()) {
            if (pathCount > 0) {
                classpath.append(File.pathSeparatorChar);
            }
            final File path = iter.next();
            classpath.append(path.getAbsolutePath());
            ++pathCount;
        }
        return classpath.toString();
    }
}
