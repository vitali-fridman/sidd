// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.Connection;

public class ProtectConnectionProvider implements ConnectionProvider
{
    @Override
    public Connection getConnection() throws DatabaseRuntimeException {
        return ProtectConnectionPool.getConnection();
    }
}
