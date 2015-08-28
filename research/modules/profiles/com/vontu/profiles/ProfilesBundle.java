// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProfilesBundle
{
    public static ResourceBundle getBundle() throws MissingResourceException {
        return ResourceBundle.getBundle("com.vontu.profiles.bundle", Locale.US);
    }
    
    public static String getMessage(final String key) {
        return getBundle().getString(key);
    }
    
    public static String getMessage(final String key, final Object... params) {
        return MessageFormat.format(getBundle().getString(key), params);
    }
    
    public static String getI18NMessage(final String key, final Object... params) {
        final StringBuffer strBuf = new StringBuffer();
        strBuf.append("EVENT_PARAMETER_I18NKEY");
        strBuf.append("##");
        strBuf.append(key);
        strBuf.append("##");
        for (final Object argument : params) {
            strBuf.append(argument);
            strBuf.append("##");
        }
        strBuf.deleteCharAt(strBuf.length() - 1);
        strBuf.deleteCharAt(strBuf.length() - 1);
        return strBuf.toString();
    }
}
