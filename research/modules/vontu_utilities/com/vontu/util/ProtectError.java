// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ProtectError implements Serializable
{
    private static final Map _errorMap;
    public static final int GENERAL_BEGIN = 0;
    public static final int GENERAL_END = 99;
    public static final int MANAGER_BEGIN = 100;
    public static final int MANAGER_END = 999;
    public static final int PROFILES_BEGIN = 1000;
    public static final int INDEXER_BEGIN = 1300;
    public static final int INDEX_BEGIN = 1400;
    public static final int DETECTION_BEGIN = 2000;
    public static final int DETECTION_END = 2999;
    public static final int LEXER_BEGIN = 3000;
    public static final int LEXER_END = 3999;
    public static final int MONITOR_BEGIN = 4000;
    public static final int CONTROLLER_BEGIN = 4200;
    public static final int MONITOR_END = 4999;
    public static final int SHEDULER_BEGIN = 5000;
    public static final int SHEDULER_END = 5999;
    public static final int AUTH_BEGIN = 6000;
    public static final int CRYPTO_BEGIN = 6100;
    public static final int KEYSTORE_BEGIN = 6200;
    public static final int IGNITION_BEGIN = 6300;
    public static final int PASSWORD_BEGIN = 6400;
    public static final int UTILITY_BEGIN = 7000;
    public static final int UTILITY_FILESYSTEM_BEGIN = 7100;
    public static final int UTILITY_WEBDAV_BEGIN = 7200;
    public static final int UTILITY_END = 7999;
    public static final int MESSAGING_BEGIN = 8000;
    public static final int MESSAGING_END = 8999;
    public static final int CRACKING_BEGIN = 9000;
    public static final int CRACKING_END = 9999;
    public static final int PCAP_BEGIN = 11000;
    public static final int PCAP_END = 11999;
    public static final int BYPASS_BEGIN = 12000;
    public static final int BYPASS_END = 12999;
    public static final int MESSAGE_BEGIN = 13000;
    public static final int MESSAGE_END = 13001;
    public static final int PROTOCOL_BEGIN = 14000;
    public static final int PROTOCOL_END = 14001;
    public static final int CLASSIFICATION_BEGIN = 14002;
    public static final int CLASSIFICATION_END = 14099;
    public static final int ICAP_BEGIN = 14100;
    public static final int ICAP_END = 14199;
    public static final int INLINE_SMTP_BEGIN = 14200;
    public static final int INLINE_SMTP_END = 14299;
    public static final int MANAGEMENT_BEGIN = 14300;
    public static final int MANAGEMENT_END = 14399;
    public static final int SCANMANAGER_BEGIN = 14400;
    public static final int SCANMANAGER_END = 14499;
    public static final int PROTECT_REMEDIATION_BEGIN = 14500;
    public static final int PROTECT_REMEDIATION_END = 14599;
    public static final int FILESCAN_BEGIN = 14600;
    public static final int FILESCAN_END = 14699;
    public static final int CHARACTER_CONVERSION_BEGIN = 14700;
    public static final int CHARACTER_CONVERSION_END = 14799;
    public static final int INDUCTOR_BEGIN = 14800;
    public static final int INDUCTOR_END = 14899;
    private final String _description;
    private final int _value;
    public static final ProtectError UNEXPECTED_ERROR;
    public static final ProtectError SUCCESS;
    public static final ProtectError DATA_ACCESS;
    public static final ProtectError JDBC_ERROR;
    public static final ProtectError CONFIG_ERROR;
    public static final ProtectError THREAD_INTERRUPTED;
    public static final ProtectError OUT_OF_MEMORY;
    public static final ProtectError RMI_ERROR;
    public static final ProtectError NO_OPEN_TRANSACTION_EXPECTED;
    public static final ProtectError OPEN_TRANSACTION_EXPECTED;
    public static final ProtectError COMMUNICATION_ERROR;
    
    protected ProtectError(final int value, final String description) throws IllegalArgumentException {
        this._description = description;
        this._value = value;
        final Integer key = new Integer(value);
        if (ProtectError._errorMap.containsKey(key)) {
            throw new IllegalArgumentException("Error inserting new error message mapping (" + value + " <==> " + description + "). Mapping already used for message: " + ProtectError._errorMap.get(key));
        }
        ProtectError._errorMap.put(key, this);
    }
    
    public String getDescription() {
        return this._description;
    }
    
    public int getValue() {
        return this._value;
    }
    
    public Object readResolve() {
        return ProtectError._errorMap.get(new Integer(this._value));
    }
    
    @Override
    public String toString() {
        return "Protect Error " + this._value + ": " + this._description + '.';
    }
    
    static {
        _errorMap = new HashMap();
        UNEXPECTED_ERROR = new ProtectError(-1, "Unexpected error");
        SUCCESS = new ProtectError(0, "Operation successful");
        DATA_ACCESS = new ProtectError(1, "Data access error");
        JDBC_ERROR = new ProtectError(3, "Database error");
        CONFIG_ERROR = new ProtectError(4, "Configuration error");
        THREAD_INTERRUPTED = new ProtectError(5, "Operation interrupted unexpectedly");
        OUT_OF_MEMORY = new ProtectError(6, "Out of memory");
        RMI_ERROR = new ProtectError(7, "RMI error");
        NO_OPEN_TRANSACTION_EXPECTED = new ProtectError(8, "No open transaction expected");
        OPEN_TRANSACTION_EXPECTED = new ProtectError(9, "Open transaction expected");
        COMMUNICATION_ERROR = new ProtectError(10, "Unexpected communication error");
    }
}
