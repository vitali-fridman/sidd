// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.logging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Writer;

public class LoggingWriter extends Writer
{
    private StringBuffer buf;
    private final Logger logger;
    private final Level level;
    
    public LoggingWriter(final Logger logger, final Level level) {
        this.buf = new StringBuffer();
        this.logger = logger;
        this.level = level;
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
    }
    
    @Override
    public void flush() throws IOException {
        this.logger.log(this.level, this.buf.toString());
        this.buf = new StringBuffer();
    }
    
    @Override
    public void write(final char[] chars, final int offset, final int numChars) throws IOException {
        for (int i = offset; i < offset + numChars; ++i) {
            this.buf.append(chars[i]);
        }
    }
}
