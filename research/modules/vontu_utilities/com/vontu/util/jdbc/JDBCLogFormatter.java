// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.logging.Logger;
import java.util.HashSet;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.FieldPosition;
import java.util.logging.LogRecord;
import java.util.Set;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class JDBCLogFormatter extends SimpleFormatter
{
    Date date;
    private static final String format = "{0,date} {0,time}";
    private MessageFormat formatter;
    private Object[] args;
    private String lineSeparator;
    private static final Set<String> _loggerClassNames;
    
    public JDBCLogFormatter() {
        this.date = new Date();
        this.args = new Object[1];
        this.lineSeparator = System.getProperty("line.separator");
    }
    
    @Override
    public synchronized String format(final LogRecord record) {
        final StringBuffer sb = new StringBuffer("-- ");
        this.date.setTime(record.getMillis());
        this.args[0] = this.date;
        final StringBuffer text = new StringBuffer();
        if (this.formatter == null) {
            this.formatter = new MessageFormat("{0,date} {0,time}");
        }
        this.formatter.format(this.args, text, null);
        sb.append(text);
        this.appendCaller(sb, record);
        this.appendSessionId(sb, record);
        sb.append(this.lineSeparator);
        sb.append("-- ");
        final String message = this.formatMessage(record);
        sb.append(record.getLevel().getName());
        sb.append(": ");
        sb.append(message);
        sb.append(this.lineSeparator);
        if (record.getThrown() != null) {
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (Exception ex) {}
        }
        return sb.toString();
    }
    
    private void appendCaller(final StringBuffer sb, final LogRecord record) {
        this.inferCaller(record);
        sb.append(" ");
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        }
        else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append(" ");
            sb.append(record.getSourceMethodName());
        }
    }
    
    private void appendSessionId(final StringBuffer sb, final LogRecord record) {
        for (final Object param : record.getParameters()) {
            if (param instanceof JDBCSessionId) {
                final JDBCSessionId jdbcSid = (JDBCSessionId)param;
                final String sessionId = jdbcSid.getSessionId();
                sb.append(" session=");
                sb.append(sessionId);
            }
        }
    }
    
    private void inferCaller(final LogRecord record) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final StackTraceElement frame = this.findLastFrameBeforeLogging(stack);
        if (frame != null) {
            record.setSourceClassName(frame.getClassName());
            record.setSourceMethodName(frame.getMethodName());
        }
    }
    
    private StackTraceElement findLastFrameBeforeLogging(final StackTraceElement[] stack) {
        for (int stackIndex = this.findLastLoggerFrame(stack); stackIndex < stack.length; ++stackIndex) {
            final StackTraceElement frame = stack[stackIndex];
            final String cname = frame.getClassName();
            if (!JDBCLogFormatter._loggerClassNames.contains(cname)) {
                return frame;
            }
        }
        return null;
    }
    
    private int findLastLoggerFrame(final StackTraceElement[] stack) {
        for (int stackIndex = 0; stackIndex < stack.length; ++stackIndex) {
            final StackTraceElement frame = stack[stackIndex];
            final String className = frame.getClassName();
            if (JDBCLogFormatter._loggerClassNames.contains(className)) {
                return stackIndex;
            }
        }
        return stack.length;
    }
    
    static {
        (_loggerClassNames = new HashSet<String>()).add(Logger.class.getName());
        JDBCLogFormatter._loggerClassNames.add(JDBCLogger.class.getName());
    }
}
