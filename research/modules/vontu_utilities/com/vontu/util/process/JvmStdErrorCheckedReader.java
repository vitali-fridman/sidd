// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;

public class JvmStdErrorCheckedReader extends BufferedReader
{
    private static final String MEMORY_ERROR = "could not reserve enough space for object heap";
    private static final String GENERIC_JVM_START_ERROR = "could not create the java virtual machine";
    private boolean _gotInput;
    private boolean _memoryError;
    private boolean _genericJvmStartError;
    
    public JvmStdErrorCheckedReader(final InputStream in) {
        super(new BufferedReader(new InputStreamReader(in)));
    }
    
    @Override
    public String readLine() throws IOException {
        this._gotInput = true;
        final String ln = super.readLine();
        if (ln != null) {
            final String lcLn = ln.toLowerCase();
            if (lcLn.indexOf("could not reserve enough space for object heap") != -1) {
                this._memoryError = true;
            }
            else if (lcLn.indexOf("could not create the java virtual machine") != 1) {
                this._genericJvmStartError = true;
            }
        }
        return ln;
    }
    
    public boolean gotInput() {
        return this._gotInput;
    }
    
    public boolean gotMemoryError() {
        return this._memoryError;
    }
    
    public boolean gotGenericJvmStartError() {
        return this._genericJvmStartError;
    }
}
