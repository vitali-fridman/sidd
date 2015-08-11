// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;

public class DirectoryCopy
{
    private static boolean copyFile(final byte[] bytes, final File dest) {
        boolean success = true;
        try {
            if (bytes == null) {
                success = false;
            }
            else {
                final FileOutputStream fileOut = new FileOutputStream(dest);
                fileOut.write(bytes);
                fileOut.close();
            }
        }
        catch (FileNotFoundException e) {
            success = false;
        }
        catch (IOException e2) {
            success = false;
        }
        return success;
    }
    
    public static byte[] getBytesFromFile(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        final long length = file.length();
        if (length <= 2147483647L) {}
        final byte[] bytes = new byte[(int)length];
        int offset = 0;
        for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {}
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }
    
    public static void main(final String[] args) {
        File[] files = null;
        if (args.length < 2) {
            System.out.println("USAGE: DirectoryCopy <src directory> <dst directory> ... <dst directory>");
        }
        final File src = new File(args[0]);
        final ArrayList dst = new ArrayList();
        for (int i = 1; i < args.length; ++i) {
            dst.add(args[i]);
        }
        dst.trimToSize();
        String[] destinations = new String[dst.size()];
        destinations = (String[]) dst.toArray(destinations);
        try {
            while (true) {
                files = src.listFiles();
                for (int j = 0; j < files.length; ++j) {
                    for (int k = 0; k < destinations.length; ++k) {
                        final File newFile = new File(files[j].getAbsolutePath() + "." + k);
                        newFile.createNewFile();
                        if (!copyFile(getBytesFromFile(files[j]), newFile)) {
                            System.out.println("can't create " + newFile.getAbsolutePath());
                        }
                        if (!newFile.renameTo(new File(destinations[k] + "\\" + files[j].getName()))) {
                            System.out.println("can't move " + files[j].getAbsolutePath() + " to " + newFile.getAbsolutePath());
                        }
                    }
                    files[j].delete();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
