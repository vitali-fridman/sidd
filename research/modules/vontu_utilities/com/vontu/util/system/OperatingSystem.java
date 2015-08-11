// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.system;

public final class OperatingSystem
{
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}
