// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class ParentProcessMonitor extends Thread
{
    public static final String HEARTBEAT_MESSAGE = "heartbeat\n";
    protected static final Logger _logger;
    
    public ParentProcessMonitor() {
        super("Parent Process Monitor");
        this.setDaemon(true);
    }
    
    protected abstract void parentDisappeared();
    
    @Override
    public void run() {
        try {
            boolean heartbeatReceived = true;
            while (heartbeatReceived) {
                final byte[] buffer = new byte["heartbeat\n".length()];
                Thread.sleep(1100L);
                int inByte = System.in.read(buffer);
                heartbeatReceived = wasHeartbeatReceived(buffer, inByte);
                while (0 != System.in.available()) {
                    inByte = System.in.read(buffer);
                }
            }
        }
        catch (IOException ignore) {
            ParentProcessMonitor._logger.fine("IOException reading stdin.");
        }
        catch (InterruptedException e) {
            ParentProcessMonitor._logger.fine("InterruptedException reading stdin.");
        }
        this.parentDisappeared();
    }
    
    private static boolean wasHeartbeatReceived(final byte[] buffer, final int inByte) {
        if (inByte == -1) {
            ParentProcessMonitor._logger.fine("End of stdin detected.");
            return false;
        }
        if ("heartbeat\n".equals(new String(buffer))) {
            ParentProcessMonitor._logger.finest("Received heartbeat");
            return true;
        }
        ParentProcessMonitor._logger.fine("Read " + new String(buffer) + " from stdin.");
        return false;
    }
    
    static {
        _logger = Logger.getLogger(ParentProcessMonitor.class.getName());
    }
}
