// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueJDBCSessionId implements JDBCSessionId
{
    private static final AtomicLong uniqueId;
    private final String _sessionId;
    
    public UniqueJDBCSessionId() {
        this(null);
    }
    
    public UniqueJDBCSessionId(String sessionName) {
        if (sessionName == null) {
            sessionName = "";
        }
        final long uniqueSessionNumber = UniqueJDBCSessionId.uniqueId.getAndIncrement();
        this._sessionId = sessionName + Long.toString(uniqueSessionNumber);
    }
    
    @Override
    public String getSessionId() {
        return this._sessionId;
    }
    
    @Override
    public String toString() {
        return "UniqueJDBCSessionId:[sid=" + this.getSessionId() + "]";
    }
    
    static {
        uniqueId = new AtomicLong(1L);
    }
}
