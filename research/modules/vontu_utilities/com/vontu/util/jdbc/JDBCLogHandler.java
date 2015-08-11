// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;

public class JDBCLogHandler extends FileHandler
{
    public JDBCLogHandler() throws IOException, SecurityException {
        this.setFormatter(new JDBCLogFormatter());
    }
}
