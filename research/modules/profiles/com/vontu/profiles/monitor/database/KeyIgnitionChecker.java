// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.database;

import com.vontu.logging.SystemEventLogger;
import com.vontu.logging.LocalLogWriter;
import com.vontu.logging.event.system.SystemEventSeverity;
import com.vontu.logging.SystemEventWriter;
import com.vontu.keystorehouse.KeyIgnition;
import com.vontu.logging.LogOnceEvent;
import java.util.logging.Logger;

public class KeyIgnitionChecker
{
    private static final Logger _logger;
    private static final String[] NO_PARAMS;
    private final LogOnceEvent _systemEvent;
    private final KeyIgnition _keyIgnition;
    
    public KeyIgnitionChecker(final SystemEventWriter writer, final KeyIgnition keyIgnition) {
        this(new LogOnceEvent(new SystemEventLogger("com.vontu.profiles.not_ignited", SystemEventSeverity.SEVERE, LocalLogWriter.createAggregated(KeyIgnitionChecker._logger, writer))), keyIgnition);
    }
    
    KeyIgnitionChecker(final LogOnceEvent systemEvent, final KeyIgnition keyIgnition) {
        this._systemEvent = systemEvent;
        this._keyIgnition = keyIgnition;
    }
    
    public boolean isIgnited() {
        if (this._keyIgnition.isIgnited()) {
            this._systemEvent.reset();
            return true;
        }
        this._systemEvent.log(KeyIgnitionChecker.NO_PARAMS);
        return false;
    }
    
    static {
        _logger = Logger.getLogger(KeyIgnitionChecker.class.getName());
        NO_PARAMS = new String[0];
    }
}
