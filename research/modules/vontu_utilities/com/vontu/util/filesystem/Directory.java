// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class Directory
{
    private File directory;
    
    public Directory(final File directory) {
        this.directory = directory;
    }
    
    public Directory(final String directoryPath) {
        this(new File(directoryPath));
    }
    
    @Override
    public String toString() {
        return this.directory.toString();
    }
    
    public boolean moveDirectory(final File targetLocation) throws IOException {
        this.deepCopy(targetLocation);
        return this.delete();
    }
    
    public void deepCopy(final File targetLocation) throws IOException {
        if (this.directory.isDirectory()) {
            if (targetLocation.exists()) {
                throw new IOException("Can not copy directory to " + targetLocation.getAbsolutePath() + ": location already exists");
            }
            targetLocation.mkdir();
            final String[] children = this.directory.list();
            for (int i = 0; i < children.length; ++i) {
                final File sourceChild = new File(this.directory, children[i]);
                final File targetChild = new File(targetLocation, children[i]);
                new Directory(sourceChild).deepCopy(targetChild);
            }
        }
        else {
            copyFile(this.directory, targetLocation);
        }
    }
    
    private static void copyFile(final File sourceLocation, final File targetLocation) throws FileNotFoundException, IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(sourceLocation);
            out = new FileOutputStream(targetLocation);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
    
    public boolean delete() {
        if (this.directory == null || !this.directory.isDirectory()) {
            return false;
        }
        final File[] entries = this.directory.listFiles();
        for (int numEntries = entries.length, i = 0; i < numEntries; ++i) {
            if (entries[i].isDirectory()) {
                new Directory(entries[i]).delete();
            }
            else {
                entries[i].delete();
            }
        }
        return this.directory.delete();
    }
    
    public boolean deleteSubdirectory() {
        if (this.directory == null || !this.directory.isDirectory()) {
            return false;
        }
        final File[] entries = this.directory.listFiles();
        for (int numEntries = entries.length, i = 0; i < numEntries; ++i) {
            if (entries[i].isDirectory()) {
                new Directory(entries[i]).delete();
            }
            else {
                entries[i].delete();
            }
        }
        return true;
    }
    
    public void copyDirectoryShallow(final File destinationDirectory, final Logger logger) {
        final File[] copyFiles = this.directory.listFiles();
        for (int i = 0; i < copyFiles.length; ++i) {
            if (copyFiles[i].isFile()) {
                final File outFile = new File(destinationDirectory, copyFiles[i].getName());
                try {
                    final FileInputStream inReader = new FileInputStream(copyFiles[i]);
                    final FileOutputStream outWriter = new FileOutputStream(outFile);
                    final byte[] buffer = new byte[10000];
                    int bytesRead;
                    while ((bytesRead = inReader.read(buffer)) != -1) {
                        outWriter.write(buffer, 0, bytesRead);
                    }
                    inReader.close();
                    outWriter.close();
                }
                catch (Exception exception) {
                    logger.log(Level.WARNING, "Directory copy failed. Source: " + this.directory.toString() + " Destination: " + destinationDirectory.toString(), exception);
                }
            }
        }
    }
    
    public void deleteFiles(final Logger logger) {
        try {
            final File[] delFiles = this.directory.listFiles();
            if (delFiles == null) {
                return;
            }
            for (int i = 0; i < delFiles.length; ++i) {
                if (delFiles[i].isFile()) {
                    delFiles[i].delete();
                }
            }
        }
        catch (Exception exception) {
            logger.log(Level.WARNING, "Directory delete failed. Directory: " + this.directory, exception);
        }
    }
    
    public File[] listFiles() throws IOException {
        return this.listFiles(null);
    }
    
    public File[] listFiles(final FilenameFilter filter) throws IOException {
        assertDirectoryCanBeRead(this.directory);
        final File[] matchingFiles = (filter == null) ? this.directory.listFiles() : this.directory.listFiles(filter);
        if (matchingFiles == null) {
            final String message = "Could not list the files in the directory: " + this.directory.getPath() + " . " + "Either this path does not denote a directory or an " + "I/O error occurred while trying to list the files " + "in the directory.";
            throw new IOException(message);
        }
        return matchingFiles;
    }
    
    private static void assertDirectoryCanBeRead(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = "Could not list the files in " + directory + " because it does not exist.";
            throw new IOException(message);
        }
        if (!directory.isDirectory()) {
            final String message = "Could not list the files in " + directory + " because it is not a directory.";
            throw new IOException(message);
        }
        if (!directory.canRead()) {
            final String message = "Could not list the files in " + directory + " because it cannot be read.";
            throw new IOException(message);
        }
    }
    
    public void ensureExists() throws IOException {
        if (!this.directory.isDirectory() && !this.directory.mkdirs()) {
            throw new IOException("Could not create folder: " + this.directory);
        }
    }
    
    public List<String> listAllFiles() throws IOException {
        final File[] files = this.getFiles();
        final List<String> result = new ArrayList<String>();
        for (final File file : files) {
            if (file.isFile()) {
                result.add(file.getAbsolutePath());
            }
            else {
                result.addAll(new Directory(file).listAllFiles());
            }
        }
        return result;
    }
    
    public List<String> listTopLevelFilesAndDirectories() throws IOException {
        final File[] files = this.getFiles();
        final List<String> result = new ArrayList<String>();
        for (final File file : files) {
            result.add(file.getAbsolutePath());
        }
        return result;
    }
    
    private File[] getFiles() throws IOException {
        if (!this.directory.exists() || !this.directory.isDirectory()) {
            throw new IOException("Invalid directory:" + this.directory);
        }
        final File[] files = this.directory.listFiles();
        return files;
    }
    
    public File appendToDirectory(final String childDir) {
        return new File(this.directory, childDir);
    }
    
    public void deleteFiles(final FilenameFilter filter, final Logger logger) throws IOException {
        for (final File fileToDelete : this.listFiles(filter)) {
            if (!fileToDelete.delete()) {
                logger.warning("Unable to delete file: " + fileToDelete.getAbsolutePath());
            }
            else {
                logger.finer("File deleted: " + fileToDelete.getAbsolutePath());
            }
        }
    }
    
    public File toFile() {
        return this.directory;
    }
}
