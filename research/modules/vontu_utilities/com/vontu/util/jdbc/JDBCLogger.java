// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class JDBCLogger
{
    private static final Logger _log;
    private static volatile Logger _jdbcLogger;
    private JDBCSessionId _jdbcSid;
    
    public JDBCLogger(final JDBCSessionId jdbcSid) {
        this.setJDBCSessionId(jdbcSid);
    }
    
    public static void reinitializeLogger() throws IOException {
        final Handler handler = new JDBCLogHandler();
        final Logger logger = Logger.getLogger("com.vontu.jdbc");
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(handler.getLevel());
        JDBCLogger._jdbcLogger = logger;
    }
    
    public void setJDBCSessionId(final JDBCSessionId jdbcSid) {
        this._jdbcSid = jdbcSid;
    }
    
    public boolean isLoggable(final Level level) {
        return JDBCLogger._jdbcLogger.isLoggable(level);
    }
    
    public void log(final Level level, final String msg) {
        JDBCLogger._jdbcLogger.log(level, msg, this._jdbcSid);
    }
    
    public void fine(final String msg) {
        this.log(Level.FINE, msg);
    }
    
    public static boolean isEnabled() {
        return JDBCLogger._jdbcLogger.isLoggable(Level.FINE);
    }
    
    public static void enable() {
        setLoggerLevel(Level.FINE);
    }
    
    public static void disable() {
        setLoggerLevel(Level.INFO);
    }
    
    private static void setLoggerLevel(final Level level) {
        JDBCLogger._jdbcLogger.setLevel(level);
        final Handler[] handlers = JDBCLogger._jdbcLogger.getHandlers();
        for (int cntr = 0; cntr < handlers.length; ++cntr) {
            if (handlers[cntr] instanceof JDBCLogHandler) {
                handlers[cntr].setLevel(level);
            }
        }
    }
    
    public static Logger getLogger() {
        return JDBCLogger._jdbcLogger;
    }
    
    static {
        _log = Logger.getLogger(JDBCLogger.class.getName());
        try {
            reinitializeLogger();
        }
        catch (Exception e) {
            JDBCLogger._log.log(Level.SEVERE, "Failed to initialize JDBC Logging", e);
            throw new RuntimeException(e);
        }
    }
}
