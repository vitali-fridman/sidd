// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.util.HashMap;
import java.util.Map;
import com.vontu.util.config.SettingProvider;

public class DefaultSettings implements SettingProvider
{
    private final Map<String, String> _settings;
    public static final String UNICODE_NORMALIZATION_ENABLED_KEY = "UnicodeNormalizer.Enabled";
    private static final String UNICODE_NORMALIZATION_ENABLED_VALUE = "on";
    public static final String NEWLINE_ELIMINATION_ENABLED_KEY = "UnicodeNormalizer.NewlineEliminationEnabled";
    private static final String NEWLINE_ELIMINATION_ENABLED_VALUE = "on";
    public static final String ASIAN_CHAR_RANGES_KEY = "UnicodeNormalizer.AsianCharRanges";
    private static final String ASIAN_CHAR_RANGES_VALUE = "E000-F8FF,FF10-FF19,FF21-FF3A,FF41-FF5A,1100-1159,115F-11A2,11A8-11F9,3131-318E,3200-321E,3260-327E,AC00-D7A3,FFA0-FFBE,FFC2-FFC7,FFCA-FFCF,FFD2-FFD7,FFDA-FFDC,3041-3096,3099-309E,30A1-30FA,30FC-30FF,31F0-31FF,32D0-32FE,3300-3357,FF66-FF6F,FF70-FF9D,0E01-0E5B,3105-312D,31A0-31B7,2E80-2E99,2E9B-2EF3,2F00-2FD5,3005-3005,3007-3007,3021-3029,3038-303B,3400-4DB5,4E00-9FC3,F900-FA2D,FA30-FA6A,FA70-FAD9,20000-2A6D6,2F800-2FA1D,F0000-10FFFF";
    
    public DefaultSettings() {
        (this._settings = new HashMap<String, String>()).put("UnicodeNormalizer.Enabled", "on");
        this._settings.put("UnicodeNormalizer.NewlineEliminationEnabled", "on");
        this._settings.put("UnicodeNormalizer.AsianCharRanges", "E000-F8FF,FF10-FF19,FF21-FF3A,FF41-FF5A,1100-1159,115F-11A2,11A8-11F9,3131-318E,3200-321E,3260-327E,AC00-D7A3,FFA0-FFBE,FFC2-FFC7,FFCA-FFCF,FFD2-FFD7,FFDA-FFDC,3041-3096,3099-309E,30A1-30FA,30FC-30FF,31F0-31FF,32D0-32FE,3300-3357,FF66-FF6F,FF70-FF9D,0E01-0E5B,3105-312D,31A0-31B7,2E80-2E99,2E9B-2EF3,2F00-2FD5,3005-3005,3007-3007,3021-3029,3038-303B,3400-4DB5,4E00-9FC3,F900-FA2D,FA30-FA6A,FA70-FAD9,20000-2A6D6,2F800-2FA1D,F0000-10FFFF");
    }
    
    @Override
    public String getSetting(final String settingName) {
        return this._settings.get(settingName);
    }
}
