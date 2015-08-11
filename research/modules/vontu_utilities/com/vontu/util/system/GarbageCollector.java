// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.system;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GarbageCollector
{
    private static final Logger _logger;
    
    public void runGc() {
        if (GarbageCollector._logger.isLoggable(Level.FINER)) {
            GarbageCollector._logger.finer("Running Garbage Collector.");
        }
        System.gc();
        System.gc();
    }
    
    static {
        _logger = Logger.getLogger(GarbageCollector.class.getName());
    }
}
