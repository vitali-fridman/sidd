// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.validate;

import java.util.regex.Pattern;

public final class CommonValidator
{
    private static String ipv4Regx;
    private static String ipv6Regx;
    private static Pattern ipv4Pattern;
    private static Pattern ipv6Pattern;
    
    public static boolean isValidInetAddressFormat(final String ipAddress) {
        return StringValidator.isNotEmptyString(ipAddress) && (CommonValidator.ipv4Pattern.matcher(ipAddress).matches() || CommonValidator.ipv6Pattern.matcher(ipAddress).matches());
    }
    
    static {
        CommonValidator.ipv4Regx = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        CommonValidator.ipv6Regx = "((([0-9a-f]{1,4}+:){7}+[0-9a-f]{1,4}+)|(:(:[0-9a-f]{1,4}+){1,6}+)|(([0-9a-f]{1,4}+:){1,6}+:)|(::)|(([0-9a-f]{1,4}+:)(:[0-9a-f]{1,4}+){1,5}+)|(([0-9a-f]{1,4}+:){1,2}+(:[0-9a-f]{1,4}+){1,4}+)|(([0-9a-f]{1,4}+:){1,3}+(:[0-9a-f]{1,4}+){1,3}+)|(([0-9a-f]{1,4}+:){1,4}+(:[0-9a-f]{1,4}+){1,2}+)|(([0-9a-f]{1,4}+:){1,5}+(:[0-9a-f]{1,4}+))|(((([0-9a-f]{1,4}+:)?([0-9a-f]{1,4}+:)?([0-9a-f]{1,4}+:)?([0-9a-f]{1,4}+:)?)|:)(:(([0-9]{1,3}+\\.){3}+[0-9]{1,3}+)))|(:(:[0-9a-f]{1,4}+)*:([0-9]{1,3}+\\.){3}+[0-9]{1,3}+))(/[0-9]+)?";
        CommonValidator.ipv4Pattern = Pattern.compile(CommonValidator.ipv4Regx);
        CommonValidator.ipv6Pattern = Pattern.compile(CommonValidator.ipv6Regx, 2);
    }
}
