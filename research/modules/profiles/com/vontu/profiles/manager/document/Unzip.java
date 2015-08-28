// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;

public class Unzip
{
    private final String _extractDirectory;
    private final String _zipFile;
    private static final String DOT_DOT_REGEX = "\\.\\.";
    
    public Unzip(final String extractDirectory, final String zipFile) {
        this._extractDirectory = extractDirectory;
        this._zipFile = zipFile;
    }
    
    public void unzip() throws IOException {
        final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(this._zipFile));
        FileOutputStream outputStream = null;
        try {
            final byte[] buffer = new byte[1024];
            ZipEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String fileName = entry.getName();
                if (fileName.indexOf("..") >= 0) {
                    fileName = fileName.replaceAll("\\.\\.", "_");
                }
                final File entryFile = new File(this._extractDirectory, fileName);
                final File parentDir = entryFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                outputStream = new FileOutputStream(entryFile);
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
            }
            inputStream.close();
        }
        finally {
            inputStream.close();
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
