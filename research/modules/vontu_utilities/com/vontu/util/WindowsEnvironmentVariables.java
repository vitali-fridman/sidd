// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.io.IOException;
import java.util.Map;

public class WindowsEnvironmentVariables
{
    private Map _environmentVariables;
    
    public WindowsEnvironmentVariables() throws IOException {
        this.initialize();
    }
    
    public Set getNames() {
        return this._environmentVariables.keySet();
    }
    
    public String getValue(final String name) {
        return (String) this._environmentVariables.get(name.toUpperCase());
    }
    
    private void initialize() throws IOException {
        this._environmentVariables = new HashMap();
        final Process cmd = Runtime.getRuntime().exec("cmd /c set");
        final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        String line;
        while ((line = commandOutput.readLine()) != null) {
            final int nameEndIndex = line.indexOf(61);
            if (nameEndIndex != -1) {
                this._environmentVariables.put(line.substring(0, nameEndIndex).toUpperCase(), line.substring(nameEndIndex + 1));
            }
        }
    }
}
