// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;
import java.io.Serializable;

public final class Configuration implements Serializable
{
    public static final String USE_JAVA_MD5 = "use_java_md5";
    public static final boolean USE_JAVA_MD5_DEFAULT = false;
    public static final String MAX_BIN_MATCH_SIZE = "max_bin_match_size";
    public static final int MAX_BIN_MATCH_SIZE_DEFAULT = 30000000;
    public static final String MIN_NORMALIZED_LENGTH = "min_normalized_size";
    public static final int MIN_NORMALIZED_LENGTH_DEFAULT = 30;
    private static final Logger _logger;
    private static final long serialVersionUID = 7622888039104940686L;
    private final boolean _useJavaMD5;
    private final int _maxBinMatchSize;
    
    public Configuration(final SettingProvider settingProvider) {
        final SettingReader reader = new SettingReader(settingProvider, Configuration._logger);
        this._useJavaMD5 = reader.getBooleanSetting("use_java_md5", false);
        this._maxBinMatchSize = reader.getIntSetting("max_bin_match_size", 30000000);
    }
    
    public boolean isUsingJavaMD5() {
        return this._useJavaMD5;
    }
    
    public int getMaxBinMatchSize() {
        return this._maxBinMatchSize;
    }
    
    static {
        _logger = Logger.getLogger(Configuration.class.getName());
    }
}
