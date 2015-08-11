// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.IOException;

public class UnicodeNormalizationConfigCheck
{
    public static final String ERROR_PREFIX = "Error:";
    
    public static void report(final String str) {
        System.out.println(str);
    }
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            report("Error:1 argument needed: UnicodeNormalizationConfig.file");
            return;
        }
        final String filename = args[0];
        try {
            final UnicodeNormalizationConfigLoader configLoader = new UnicodeNormalizationConfigLoader();
            configLoader.loadFile(filename);
            report("Configuration File is valid");
        }
        catch (IOException ioException) {
            report("Error: file" + filename + " cannot be loaded");
        }
        catch (UnicodeNormalizationConfigLoaderException configLoaderException) {
            report("Error:line Number:" + configLoaderException.getLineNumber() + " " + configLoaderException.getMessage());
        }
    }
}
