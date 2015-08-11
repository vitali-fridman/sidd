// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.i18n;

import com.vontu.util.StringUtil;
import java.util.Locale;

public class LocaleParsing
{
    public static Locale localeFromString(final String loc) {
        if (StringUtil.isEmptyTrimmed(loc)) {
            return null;
        }
        final String[] parts = loc.split("_");
        switch (parts.length) {
            case 1: {
                return new Locale(parts[0]);
            }
            case 2: {
                return new Locale(parts[0], parts[1]);
            }
            default: {
                return new Locale(parts[0], parts[1], parts[2]);
            }
        }
    }
}
