// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.File;

public class PathResolver
{
    private static final String USER_DIR = "user.dir";
    private final File root;
    
    public PathResolver() {
        this(System.getProperty("user.dir"));
    }
    
    public PathResolver(final String root) {
        this(new File(root));
    }
    
    public PathResolver(final File root) {
        this.root = root;
    }
    
    public File resolve(final String path) {
        final File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(this.root, path);
    }
}
