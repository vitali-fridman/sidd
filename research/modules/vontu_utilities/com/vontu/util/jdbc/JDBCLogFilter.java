// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.logging.LogRecord;
import java.util.logging.Filter;

public class JDBCLogFilter implements Filter
{
    @Override
    public boolean isLoggable(final LogRecord record) {
        return !record.getLoggerName().startsWith("com.vontu.util.jdbc");
    }
}
