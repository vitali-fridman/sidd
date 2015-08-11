// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.db;

import java.util.logging.Level;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import org.apache.commons.dbutils.DbUtils;
import com.vontu.util.ProtectError;
import com.vontu.util.jdbc.ProtectConnectionPool;
import com.vontu.util.Stopwatch;
import java.util.logging.Logger;

public class RefreshOracleDBStats
{
    private static final Logger _logger;
    private static final String STATEMENT = "{call refreshcbostats ()}";
    
    public static synchronized void refresh() throws DBUtilException {
        RefreshOracleDBStats._logger.fine("Starting refresh statistics");
        final Stopwatch stopwatch = new Stopwatch("Refresh statistics stopwatch");
        stopwatch.start();
        Connection conn = null;
        CallableStatement cs = null;
        try {
            conn = ProtectConnectionPool.getConnection();
            cs = conn.prepareCall("{call refreshcbostats ()}");
            cs.executeUpdate();
        }
        catch (Throwable t) {
            throw new DBUtilException(ProtectError.JDBC_ERROR, t);
        }
        finally {
            RefreshOracleDBStats._logger.fine("Refresh statistics completed in " + stopwatch.stop().getLastTimeFormatted());
            DbUtils.closeQuietly((Statement)cs);
            DbUtils.closeQuietly(conn);
        }
    }
    
    public static void asynchRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RefreshOracleDBStats.refresh();
                }
                catch (DBUtilException e) {
                    RefreshOracleDBStats._logger.log(Level.SEVERE, e.getMessage());
                }
            }
        }).start();
    }
    
    static {
        _logger = Logger.getLogger(RefreshOracleDBStats.class.getName());
    }
}
