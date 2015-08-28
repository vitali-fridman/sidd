// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;
import java.io.Serializable;

public final class Configuration implements Serializable
{
    public static final String MAX_MATCH_COUNT = "maximum_number_of_matches_to_return";
    public static final int MAX_MATCH_COUNT_DEFAULT = 1000;
    public static final String ENABLE_PROXIMITY_LOGIC = "enable_proximity_logic";
    public static final boolean ENABLE_PROXIMITY_LOGIC_DEFAULT = true;
    public static final String SIMPLE_TEXT_PROXIMITY_RADIUS = "simple_text_proximity_radius";
    public static final int SIMPLE_TEXT_PROXIMITY_RADIUS_DEFAULT = 35;
    public static final String VERIFY_JOHN_JOHN_CASES = "verify_john_john_cases";
    public static final boolean VERIFY_JOHN_JOHN_CASES_DEFAULT = true;
    public static final String MATCH_COUNT_VARIANT = "match_count_variant";
    public static final int MATCH_COUNT_ROWS = 1;
    public static final int MATCH_COUNT_UNIQUE_SETS = 2;
    private static final int MATCH_COUNT_SUPERSETS = 3;
    public static final int MATCH_COUNT_VARIANT_DEFAULT = 3;
    private static final Logger _logger;
    private static final long serialVersionUID = 8622888039104940686L;
    private final int _maxMatchCount;
    private final int _simpleTextProximityRadius;
    private final boolean _isProximityLogicEnabled;
    private final boolean _shouldVerifyJohnJohn;
    private final int _matchCountVariant;
    
    public Configuration(final SettingProvider settingProvider) {
        final SettingReader reader = new SettingReader(settingProvider, Configuration._logger);
        this._maxMatchCount = reader.getIntSetting("maximum_number_of_matches_to_return", 1000);
        this._simpleTextProximityRadius = reader.getIntSetting("simple_text_proximity_radius", 35);
        this._isProximityLogicEnabled = reader.getBooleanSetting("enable_proximity_logic", true);
        this._shouldVerifyJohnJohn = reader.getBooleanSetting("verify_john_john_cases", true);
        this._matchCountVariant = validateMatchCountVariant(reader.getIntSetting("match_count_variant", 3));
    }
    
    int getMaxMatchCount() {
        return this._maxMatchCount;
    }
    
    int getSimpleTextProximityRadius() {
        return this._simpleTextProximityRadius;
    }
    
    boolean isProximityLogicEnabled() {
        return this._isProximityLogicEnabled;
    }
    
    boolean shouldVerifyJohnJohn() {
        return this._shouldVerifyJohnJohn;
    }
    
    int getMatchCountVariant() {
        return this._matchCountVariant;
    }
    
    private static int validateMatchCountVariant(final int matchCountVaraint) {
        if (matchCountVaraint < 1 || matchCountVaraint > 3) {
            Configuration._logger.warning("match_count_variant setting value is invalid. The default value of 3 will be used.");
            return 3;
        }
        return matchCountVaraint;
    }
    
    static {
        _logger = Logger.getLogger(Configuration.class.getName());
    }
}
