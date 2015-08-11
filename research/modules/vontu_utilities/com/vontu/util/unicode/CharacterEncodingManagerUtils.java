// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.util.Properties;
import com.vontu.util.config.SettingProvider;

public class CharacterEncodingManagerUtils
{
    public static final String DETECTION_ICU_ENABLED = "Detection.EncodingGuessingEnabled";
    public static final String DETECTION_ICU_MIN_CONFIDENCE = "Detection.EncodingGuessingMinimumConfidence";
    public static final String DETECTION_ICU_DEFAULT = "Detection.EncodingGuessingDefaultEncoding";
    
    public static Properties mapDetectionSettings(final SettingProvider settings) {
        final Properties props = new Properties();
        mapPropertyIfSet(settings, "Detection.EncodingGuessingEnabled", props, "EncodingGuessingEnabled");
        mapPropertyIfSet(settings, "Detection.EncodingGuessingMinimumConfidence", props, "MinimumConfidence");
        mapPropertyIfSet(settings, "Detection.EncodingGuessingDefaultEncoding", props, "DefaultEncoding");
        return props;
    }
    
    private static void mapPropertyIfSet(final SettingProvider settings, final String settingKey, final Properties properties, final String propertiesKey) {
        if (settings.getSetting(settingKey) != null) {
            properties.setProperty(propertiesKey, settings.getSetting(settingKey));
        }
    }
}
