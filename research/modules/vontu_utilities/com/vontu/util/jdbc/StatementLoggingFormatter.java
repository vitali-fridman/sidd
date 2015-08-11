// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.SQLException;
import com.vontu.util.Stopwatch;

public class StatementLoggingFormatter
{
    public static String generateStatementBeginMessage(final String sql, final Object statementId) {
        return generateBeginLogMessage("Statement", sql, statementId);
    }
    
    public static String generateStatementEndMessage(final Stopwatch.Statistics statistics, final Object statementId) {
        return generateEndLogMessage("Statement", statistics, statementId);
    }
    
    public static String generateQueryBeginMessage(final String sql, final Object statementId) {
        return generateBeginLogMessage("Query", sql, statementId);
    }
    
    public static String generateQueryEndMessage(final Stopwatch.Statistics statistics, final Object statementId) {
        return generateEndLogMessage("Query", statistics, statementId);
    }
    
    public static String generateUpdateBeginMessage(final String sql, final Object statementId) {
        return generateBeginLogMessage("Update", sql, statementId);
    }
    
    public static String generateUpdateEndMessage(final Stopwatch.Statistics statistics, final Object statementId) {
        return generateEndLogMessage("Update", statistics, statementId);
    }
    
    public static String generateFailedMessage(final String sql, final SQLException sqlException) {
        String query = "\n" + sql;
        try {
            query = new SQLFormatter(sql).format();
        }
        catch (RuntimeException ex) {}
        return "Query failed with error: " + sqlException.getMessage() + query + ";";
    }
    
    private static String generateBeginLogMessage(final String type, final String sql, final Object id) {
        String query = "\n" + sql;
        try {
            query = new SQLFormatter(sql).format();
        }
        catch (RuntimeException ex) {}
        final String idString = (id == null) ? " " : (" (id=" + id + ")");
        return "-- " + type + idString + query + ";";
    }
    
    private static String generateEndLogMessage(final String type, final Stopwatch.Statistics statistics, final Object id) {
        final String idString = (id == null) ? " " : (" (id=" + id + ")");
        return "-- " + type + " Time" + idString + ":" + statistics.getLastTime() + "ms";
    }
}
