// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;

public class OutputRedirector extends Thread
{
    private final BufferedReader _reader;
    private volatile boolean _isStopped;
    private final String _prefix;
    private final OutputStream _output;
    
    public OutputRedirector(final InputStream is, final OutputStream redirect) {
        this(is, null, redirect);
    }
    
    public OutputRedirector(final InputStream is, final String type) {
        this(is, type, null);
    }
    
    public OutputRedirector(final InputStream is, final String type, final OutputStream redirect) {
        this(new BufferedReader(new InputStreamReader(is)), type, redirect);
    }
    
    public OutputRedirector(final BufferedReader reader, final String type, final OutputStream redirect) {
        this._isStopped = false;
        this.setDaemon(true);
        this._reader = reader;
        this._prefix = type;
        this._output = redirect;
    }
    
    @Override
    public void run() {
        try {
            final PrintWriter pw = (this._output == null) ? null : new PrintWriter(this._output);
            for (String line = this._reader.readLine(); line != null; line = this._reader.readLine()) {
                if (pw != null) {
                    pw.println(line);
                }
                if (this._prefix != null) {
                    System.out.println(this._prefix + "> " + line);
                }
            }
            if (pw != null) {
                pw.flush();
            }
        }
        catch (IOException e) {
            if (!this._isStopped) {
                Logger.global.log(Level.SEVERE, "Failed to read process output.", e);
            }
        }
    }
    
    public void detach() {
        this._isStopped = true;
    }
}
