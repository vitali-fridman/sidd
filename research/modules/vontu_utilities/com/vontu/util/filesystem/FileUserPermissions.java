// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.IOException;
import java.util.logging.Level;
import java.io.FileWriter;
import java.io.File;
import java.util.logging.Logger;

public class FileUserPermissions
{
    private static final Logger logger;
    
    public static boolean canWriteToFile(final File file) throws IllegalArgumentException {
        try {
            if (file == null || !file.exists() || file.isDirectory()) {
                throw new IllegalArgumentException("Valid file expected");
            }
            if (!file.canWrite()) {
                return false;
            }
            FileWriter fstream = null;
            try {
                fstream = new FileWriter(file, true);
            }
            finally {
                if (fstream != null) {
                    fstream.close();
                }
            }
        }
        catch (IOException e) {
            FileUserPermissions.logger.log(Level.FINE, e.getMessage());
            return false;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(FileUserPermissions.class.getName());
    }
}
