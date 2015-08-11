// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.FileNotFoundException;
import java.util.zip.ZipException;
import java.io.InputStream;
import java.util.Enumeration;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;
import java.io.File;

public class ZipUtil
{
    public static void addFilesToZip(final File directory, final String prefix, final ZipOutputStream zos) throws IOException {
        final String prefix2 = (prefix == null || prefix.equals("")) ? "" : (prefix + File.separator);
        final String newPrefix = prefix2 + directory.getName();
        final File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                final File file = files[i];
                if (file.isDirectory()) {
                    addFilesToZip(file, newPrefix, zos);
                }
                else {
                    addFileToZip(file, newPrefix, zos);
                }
            }
        }
    }
    
    public static void addFileToZip(final File file, final String prefix, final ZipOutputStream zos) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        final String prefix2 = (prefix == null || prefix.equals("")) ? "" : (prefix + File.separator);
        final String entryName = prefix2 + file.getName();
        final ZipEntry ze = new ZipEntry(entryName);
        ze.setTime(file.lastModified());
        zos.putNextEntry(ze);
        try {
            final byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }
        finally {
            fis.close();
        }
        zos.flush();
        zos.closeEntry();
    }
    
    public static void unzip(final File zip, final File outputDirectory) throws ZipException, IOException, FileNotFoundException {
        final ZipFile zf = new ZipFile(zip);
        try {
            final Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                final File f = new File(outputDirectory, entry.getName());
                if (entry.isDirectory()) {
                    f.mkdirs();
                }
                else {
                    f.getParentFile().mkdirs();
                    final FileOutputStream fos = new FileOutputStream(f);
                    try {
                        final InputStream is = zf.getInputStream(entry);
                        final byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                        fos.flush();
                    }
                    finally {
                        fos.close();
                    }
                    if (entry.getTime() == -1L || !f.exists()) {
                        continue;
                    }
                    f.setLastModified(entry.getTime());
                }
            }
        }
        finally {
            zf.close();
        }
    }
}
