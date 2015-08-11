// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.syslog;

public class SyslogProtocolDefinitions
{
    public static final int LOG_EMERG = 0;
    public static final int LOG_ALERT = 1;
    public static final int LOG_CRIT = 2;
    public static final int LOG_ERR = 3;
    public static final int LOG_WARNING = 4;
    public static final int LOG_NOTICE = 5;
    public static final int LOG_INFO = 6;
    public static final int LOG_DEBUG = 7;
    public static final int LOG_ALL = 8;
    public static final int[] ALL_LEVELS;
    public static final int KERNEL = 0;
    public static final int USER = 1;
    public static final int MAIL_SYSTEM = 2;
    public static final int SYSTEM_DAEMONS = 3;
    public static final int SECURITY_AUTHORIZATION = 4;
    public static final int SYSLOG = 5;
    public static final int PRINTER = 6;
    public static final int NNTP = 7;
    public static final int UUCP = 8;
    public static final int CLOCK = 9;
    public static final int SECURITY_AUTH2 = 10;
    public static final int FTP = 11;
    public static final int NTP = 12;
    public static final int AUDIT = 13;
    public static final int ALERT = 14;
    public static final int CLOCK2 = 15;
    public static final int LOCAL0 = 16;
    public static final int LOCAL1 = 17;
    public static final int LOCAL2 = 18;
    public static final int LOCAL3 = 19;
    public static final int LOCAL4 = 20;
    public static final int LOCAL5 = 21;
    public static final int LOCAL6 = 22;
    public static final int LOCAL7 = 23;
    public static final int NUMBER_FACILITIES = 24;
    public static final int[] ALL_FACILITIES;
    public static final int DEFAULT_PORT = 514;
    public static final int MAX_MESSAGE_SIZE = 1460;
    
    static {
        ALL_LEVELS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        ALL_FACILITIES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
    }
}
