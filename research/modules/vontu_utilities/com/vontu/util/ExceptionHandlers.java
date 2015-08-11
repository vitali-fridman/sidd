// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionHandlers
{
    public static void handleError(final OutOfMemoryError e, String logMessage) {
        try {
            try {
                if (logMessage != null) {
                    logMessage += " Shutting down the process.";
                }
                else {
                    logMessage = "Shutting down the process due to out of memory error.";
                }
            }
            finally {
                Logger.global.log(Level.SEVERE, logMessage, new ProtectRuntimeException(ProtectError.OUT_OF_MEMORY, e));
            }
        }
        finally {
            System.exit(-1);
        }
    }
}
