// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.net.UnknownHostException;

public class ValidateIpAddress
{
    public static void checkValidIPv4Address(final String address) throws UnknownHostException {
        final String[] strTok = address.split("\\.");
        final boolean has4Tokens = strTok.length == 4;
        boolean allTokensDigit = true;
        boolean isAll4TokensValid = true;
        for (int i = 0; i < strTok.length; ++i) {
            try {
                final int number = Integer.parseInt(strTok[i]);
                if (has4Tokens && (number < 0 || number > 255)) {
                    isAll4TokensValid = false;
                }
            }
            catch (NumberFormatException e) {
                allTokensDigit = false;
            }
        }
        if (allTokensDigit && (!has4Tokens || (has4Tokens && !isAll4TokensValid))) {
            throw new UnknownHostException("isValidIPv4Address fails for address: " + address);
        }
    }
}
