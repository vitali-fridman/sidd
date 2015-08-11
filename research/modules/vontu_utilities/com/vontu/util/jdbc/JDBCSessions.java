// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

public class JDBCSessions
{
    private static final InheritableThreadLocal<JDBCSessionId> _perThreadJdbcSids;
    
    public static JDBCSessionId getJDBCSessionId() {
        final JDBCSessionId jdbcSessionId = JDBCSessions._perThreadJdbcSids.get();
        if (jdbcSessionId == null) {
            return startNewNamedSession(null);
        }
        return jdbcSessionId;
    }
    
    public static void setJDBCSessionId(final JDBCSessionId jdbcSid) {
        JDBCSessions._perThreadJdbcSids.set(jdbcSid);
    }
    
    public static void removeJDBCSessionId() {
        JDBCSessions._perThreadJdbcSids.remove();
    }
    
    public static JDBCSessionId startNewNamedSession(final String sessionName) {
        final UniqueJDBCSessionId jdbcSid = new UniqueJDBCSessionId(sessionName);
        setJDBCSessionId(jdbcSid);
        return jdbcSid;
    }
    
    static {
        _perThreadJdbcSids = new InheritableThreadLocal<JDBCSessionId>();
    }
}
