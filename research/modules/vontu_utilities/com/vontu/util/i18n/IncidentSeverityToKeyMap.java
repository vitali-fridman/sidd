// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.i18n;

import java.util.HashMap;
import java.util.Map;

public class IncidentSeverityToKeyMap
{
    private static Map<String, String> severityToKey;
    
    public static String getI18NKey(final String severity) {
        return IncidentSeverityToKeyMap.severityToKey.get(severity);
    }
    
    static {
        (IncidentSeverityToKeyMap.severityToKey = new HashMap<String, String>()).put("HIGH", "incident.severity.High");
        IncidentSeverityToKeyMap.severityToKey.put("MEDIUM", "incident.severity.Medium");
        IncidentSeverityToKeyMap.severityToKey.put("LOW", "incident.severity.Low");
        IncidentSeverityToKeyMap.severityToKey.put("INFO", "incident.severity.Info");
    }
}
