// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import com.vontu.util.ProtectException;
import java.io.File;

public final class Win32ProcessControl extends NativeProcessControl
{
    Win32ProcessControl() {
        final String libPath = System.getProperty("vontu.native.lib.dir");
        if (libPath != null) {
            System.load(new File(libPath, "Win32ProcessControl.dll").getAbsolutePath());
        }
        else {
            System.loadLibrary("Win32ProcessControl");
        }
    }
    
    @Override
    public native int getCurrentProcessId();
    
    @Override
    public native boolean kill(final int p0) throws ProtectException;
    
    @Override
    public native int getParentProcessId(final int p0) throws ProtectException;
    
    @Override
    public native int[] getProcessIds(final String p0) throws ProtectException;
}
