// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.util.logging.Level;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.io.FilterInputStream;

public class InputStreamRequiringCleanup extends FilterInputStream
{
    private final Runnable runOnClose;
    private boolean alreadyCleanedUp;
    private static final Logger logger;
    
    public InputStreamRequiringCleanup(final InputStream backingStream, final Runnable runOnClose) {
        super(backingStream);
        this.alreadyCleanedUp = false;
        this.runOnClose = runOnClose;
    }
    
    @Override
    public int read() throws IOException {
        try {
            return super.read();
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        try {
            return super.read(b);
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        try {
            return super.read(b, off, len);
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        try {
            return super.skip(n);
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public int available() throws IOException {
        try {
            return super.available();
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            super.close();
            this.cleanUp(null);
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    @Override
    public void reset() throws IOException {
        try {
            super.reset();
        }
        catch (IOException e) {
            this.cleanUp(e);
            throw e;
        }
    }
    
    private void cleanUp(final IOException originalCause) {
        try {
            if (!this.alreadyCleanedUp) {
                this.runOnClose.run();
                this.alreadyCleanedUp = true;
            }
        }
        catch (RuntimeException e) {
            InputStreamRequiringCleanup.logger.log(Level.WARNING, "Exception during cleanup", e);
        }
        catch (Error e2) {
            if (originalCause != null) {
                InputStreamRequiringCleanup.logger.log(Level.WARNING, "Overshadowing exception", originalCause);
            }
            throw e2;
        }
    }
    
    static {
        logger = Logger.getLogger(InputStreamRequiringCleanup.class.getName());
    }
}
