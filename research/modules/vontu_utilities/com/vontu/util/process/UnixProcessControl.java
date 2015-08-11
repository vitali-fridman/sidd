// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import cern.colt.list.IntArrayList;
import java.io.IOException;
import com.vontu.util.ProtectError;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.vontu.util.ProtectException;
import com.vontu.util.software.Version;
import java.io.File;
import com.vontu.util.software.Software;

public final class UnixProcessControl extends NativeProcessControl
{
    UnixProcessControl() {
        final String libPath = System.getProperty("vontu.native.lib.dir");
        final Version version = new Software().version();
        final StringBuilder libNameBuilder = new StringBuilder();
        libNameBuilder.append("libUnixProcessControl-");
        libNameBuilder.append(version.getMajorVersion());
        libNameBuilder.append('.');
        libNameBuilder.append(version.getMinorVersion());
        libNameBuilder.append(".0.so");
        if (libPath != null) {
            System.load(new File(libPath, libNameBuilder.toString()).getAbsolutePath());
        }
        else {
            System.loadLibrary(libNameBuilder.toString());
        }
    }
    
    @Override
    public native int getCurrentProcessId();
    
    @Override
    public native boolean kill(final int p0) throws ProtectException;
    
    @Override
    public int getParentProcessId(final int processId) throws ProtectException {
        final GetSingleProcessId getParentId = new GetSingleProcessId();
        getProcessIds("ps -o ppid= -p " + processId, getParentId);
        return getParentId.getProcessId();
    }
    
    @Override
    public int[] getProcessIds(final String processName) throws ProtectException {
        final GetMultipleProcessIds getIds = new GetMultipleProcessIds();
        getProcessIds("ps -o pid= -C " + processName, getIds);
        return getIds.getProcessIds();
    }
    
    private static void getProcessIds(final String command, final ProcessIdAction processIdAction) throws ProtectException {
        try {
            final Process psProcess = Runtime.getRuntime().exec(command);
            psProcess.waitFor();
            final BufferedReader psOutputReader = new BufferedReader(new InputStreamReader(psProcess.getInputStream()));
            try {
                for (String line = psOutputReader.readLine(); line != null; line = psOutputReader.readLine()) {
                    processIdAction.execute(Integer.parseInt(line.trim()));
                }
            }
            finally {
                psOutputReader.close();
            }
        }
        catch (NumberFormatException e) {
            throw new ProtectException(ProtectError.UNEXPECTED_ERROR, e, "Invalid process ID format.");
        }
        catch (IOException e2) {
            throw new ProtectException(ProtectError.UNEXPECTED_ERROR, e2, "Failed to get process ID(s) from OS.");
        }
        catch (InterruptedException e3) {
            throw new ProtectException(ProtectError.THREAD_INTERRUPTED, e3, "The current thread was interrupted while getting the process list.");
        }
    }
    
    private final class GetMultipleProcessIds implements ProcessIdAction
    {
        private final IntArrayList _processIds;
        
        private GetMultipleProcessIds() {
            this._processIds = new IntArrayList();
        }
        
        @Override
        public void execute(final int processId) {
            this._processIds.add(processId);
        }
        
        public int[] getProcessIds() {
            this._processIds.trimToSize();
            return this._processIds.elements();
        }
    }
    
    private final class GetSingleProcessId implements ProcessIdAction
    {
        private int _processId;
        
        private GetSingleProcessId() {
            this._processId = -1;
        }
        
        @Override
        public void execute(final int processId) {
            this._processId = processId;
        }
        
        public int getProcessId() {
            return this._processId;
        }
    }
    
    private interface ProcessIdAction
    {
        void execute(int p0);
    }
}
