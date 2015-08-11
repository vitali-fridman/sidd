// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.OutputStream;
import java.io.InputStream;
import javax.activation.DataSource;

public class InputStreamDataSource implements DataSource
{
    public static final String DEFAULT_NAME = "InputStreamDataSource";
    private String type;
    private String name;
    private InputStream source;
    
    public InputStreamDataSource(final InputStream stream, final String type) {
        this.name = "InputStreamDataSource";
        this.source = stream;
        this.type = type;
    }
    
    @Override
    public InputStream getInputStream() {
        return this.source;
    }
    
    @Override
    public String getContentType() {
        return this.type;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("InputStreamDataSource does not support an OutputStream");
    }
}
