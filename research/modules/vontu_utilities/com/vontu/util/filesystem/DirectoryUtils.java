// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.List;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.logging.Logger;
import java.io.File;

public class DirectoryUtils
{
    public static void copyDirectory(final File destinationDirectory, final File sourceDirectory, final Logger logger) {
        new Directory(sourceDirectory).copyDirectoryShallow(destinationDirectory, logger);
    }
    
    public static void deleteDirectoryFiles(final String directory, final Logger logger) {
        new Directory(directory).deleteFiles(logger);
    }
    
    public static void deleteDirectoryFiles(final File directory, final Logger logger) {
        new Directory(directory).deleteFiles(logger);
    }
    
    public static boolean deleteDirectory(final File dir) {
        return new Directory(dir).delete();
    }
    
    public static boolean deleteSubdirectory(final File dir) {
        return new Directory(dir).deleteSubdirectory();
    }
    
    public static File[] listFiles(final File directory) throws IOException {
        return listFiles(directory, null);
    }
    
    public static File[] listFiles(final File directory, final FilenameFilter filter) throws IOException {
        return new Directory(directory).listFiles(filter);
    }
    
    public static void ensureFolderExists(final File folder) throws IOException {
        new Directory(folder).ensureExists();
    }
    
    public static List<String> listAllFiles(final String directoryName) throws IOException {
        return new Directory(directoryName).listAllFiles();
    }
    
    public static List<String> listTopLevelFilesAndDirectories(final String directoryName) throws IOException {
        return new Directory(directoryName).listTopLevelFilesAndDirectories();
    }
    
    public static String appendToDirectoryName(final String topDir, final String childDir) {
        return new Directory(topDir).appendToDirectory(childDir).toString();
    }
}
