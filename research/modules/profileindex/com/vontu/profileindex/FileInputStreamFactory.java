// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

public class FileInputStreamFactory implements InputStreamFactory, Serializable
{
    private static final long serialVersionUID = 8574994247697318397L;
    private final String _filePath;
    
    public FileInputStreamFactory(final String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException();
        }
        this._filePath = filePath;
    }
    
    @Override
    public String name() {
        return this._filePath;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this._filePath);
    }
    
    @Override
    public String toString() {
        return this._filePath;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof FileInputStreamFactory && this._filePath.equals(((FileInputStreamFactory)obj)._filePath);
    }
    
    @Override
    public int hashCode() {
        return 19 + this._filePath.hashCode();
    }
}
