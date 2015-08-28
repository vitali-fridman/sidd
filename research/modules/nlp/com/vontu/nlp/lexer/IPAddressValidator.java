// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class IPAddressValidator implements Validator
{
    private static Validator _instance;
    
    public static Validator getInstance() {
        if (IPAddressValidator._instance == null) {
            IPAddressValidator._instance = new IPAddressValidator();
        }
        return IPAddressValidator._instance;
    }
    
    @Override
    public boolean assertTokenValidity(final CharSequence text, final int position, final int length) {
        try {
            final String ipAddressString = text.subSequence(position, position + length).toString();
            final String[] ipSubParts = ipAddressString.split("\\.");
            if (ipSubParts == null || ipSubParts.length != 4) {
                return false;
            }
            final String[] lastPart = ipSubParts[3].split("\\/");
            if (lastPart.length == 2) {
                ipSubParts[3] = lastPart[0];
                final int netPart = Integer.parseInt(lastPart[1]);
                if (netPart > 32 || netPart < 0) {
                    return false;
                }
            }
            for (int i = 0; i < ipSubParts.length; ++i) {
                final int ipPart = Integer.parseInt(ipSubParts[i]);
                if (ipPart > 255) {
                    return false;
                }
                if (ipPart == 0 && i == 0) {
                    return false;
                }
            }
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean assertTokenValidity(final String value) {
        return this.assertTokenValidity(value, 0, value.length());
    }
    
    public static void main(final String[] args) {
        final Validator validator = getInstance();
        System.out.println("true: " + validator.assertTokenValidity("1.2.3.4"));
        System.out.println("true: " + validator.assertTokenValidity("10.0.0.0"));
        System.out.println("false: " + validator.assertTokenValidity("128.256.3.4"));
        System.out.println("false: " + validator.assertTokenValidity("154354.2.3.4"));
        System.out.println("false: " + validator.assertTokenValidity("0.2.3.4"));
        System.out.println("true: " + validator.assertTokenValidity("18.255.30.41"));
        System.out.println("false: " + validator.assertTokenValidity("1.2.3"));
        System.out.println("true: " + validator.assertTokenValidity("172.24.31.40"));
        System.out.println("false: " + validator.assertTokenValidity("10.2.3.400"));
        System.out.println("true: " + validator.assertTokenValidity("10.0.10.0/24"));
        System.out.println("false: " + validator.assertTokenValidity("10.0.10.0/33"));
        System.out.println("true: " + validator.assertTokenValidity("10.0.10.0/1"));
        System.out.println("true: " + validator.assertTokenValidity("10.0.10.0/0"));
        System.out.println("false: " + validator.assertTokenValidity("10.0.10.0/512"));
    }
}
