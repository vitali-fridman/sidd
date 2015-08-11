// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import com.vontu.util.config.ConfigurationException;
import com.vontu.util.ProtectException;

public abstract class NativeProcessControl
{
    private static String getOs() {
        return System.getProperty("os.name");
    }
    
    public abstract int getCurrentProcessId();
    
    public abstract boolean kill(final int p0) throws ProtectException;
    
    public abstract int getParentProcessId(final int p0) throws ProtectException;
    
    public abstract int[] getProcessIds(final String p0) throws ProtectException;
    
    public static NativeProcessControl newInstance() throws ConfigurationException {
        if ("Linux".equals(getOs())) {
            return new UnixProcessControl();
        }
        if (getOs() != null && getOs().indexOf("Windows") == 0) {
            return new Win32ProcessControl();
        }
        throw new ConfigurationException(getOs() + " operating system isn't supported.");
    }
    
    public static void main(final String[] args) {
        if (args.length == 2 && "-k".equals(args[0])) {
            final int processId = Integer.parseInt(args[1]);
            try {
                if (newInstance().kill(processId)) {
                    System.out.println("Process " + processId + " is killed.");
                }
                else {
                    System.out.println("Process " + processId + " doesn't exist.");
                }
            }
            catch (ProtectException e) {
                System.out.println("Failed to kill process " + processId + ". " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
