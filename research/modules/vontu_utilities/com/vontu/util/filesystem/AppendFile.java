// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.File;

public class AppendFile
{
    public static void main(final String[] args) {
        if (args.length == 2) {
            final File appendTo = new File(args[0]);
            final File appendFrom = new File(args[1]);
            try {
                append(appendTo, appendFrom);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Usage:");
            System.out.println("java com.vontu.util.filesystem.AppendFile [to file path] [from file path]");
        }
    }
    
    private static void append(final File appendTo, final File appendFrom) throws FileNotFoundException, IOException {
        RandomAccessFile to = null;
        InputStream from = null;
        try {
            to = new RandomAccessFile(appendTo, "rw");
            to.seek(to.length());
            from = new BufferedInputStream(new FileInputStream(appendFrom));
            int b;
            while ((b = from.read()) != -1) {
                to.write(b);
            }
        }
        finally {
            if (to != null) {
                try {
                    to.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (from != null) {
                try {
                    from.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
