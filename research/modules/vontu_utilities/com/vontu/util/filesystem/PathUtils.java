// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import org.apache.commons.lang.StringUtils;

public class PathUtils
{
    private static final char PATH_SEPARATOR_CHAR = '/';
    public static final char UNC_PATH_SEPARATOR_CHAR = '\\';
    public static final String UNC_PATH_PREFIX = "\\\\";
    public static final String PATH_SEPARATOR = "/";
    
    public static String normalizePath(final String path) {
        String normalizedPath = path.replace('\\', '/');
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }
    
    public static String getFirstElementInPath(final String path) {
        final String normalized = normalizePath(path);
        final String normalizedNoLeadingSlashes = removeLeadingSlashes(normalized);
        return normalizedNoLeadingSlashes.split("/", 2)[0];
    }
    
    public static String removeLeadingSlashes(final String path) {
        int index;
        for (index = 0; index < path.length() && path.charAt(index) == '/'; ++index) {}
        return path.substring(index);
    }
    
    public static String removeTrailingSlashes(final String path) {
        int index;
        for (index = path.length() - 1; index >= 0 && path.charAt(index) == '/'; --index) {}
        return path.substring(0, index + 1);
    }
    
    public static String getDirectoryPath(String filePath) {
        filePath = normalizePath(filePath);
        final int lastSeparatorIndex = filePath.lastIndexOf("/");
        return (lastSeparatorIndex > 0) ? filePath.substring(0, lastSeparatorIndex) : null;
    }
    
    public static int getPathSeparatorCount(final String path) {
        return StringUtils.countMatches(normalizePath(path), "/");
    }
}
