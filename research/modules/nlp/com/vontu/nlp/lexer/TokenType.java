// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TokenType
{
    public static final int WORD = 0;
    public static final int PHONE = 1;
    public static final int EMAIL = 2;
    public static final int TAXID = 3;
    public static final int CCN = 4;
    public static final int DATE = 5;
    public static final int NUMBER = 6;
    public static final int PERCENT = 7;
    public static final int IP_ADDRESS = 8;
    public static final int POSTAL_CODE = 9;
    public static final int URL = 10;
    public static final int PUNCTUATION = 11;
    public static final int SEPARATOR = 12;
    public static final int NFA_PATTERN = 13;
    public static final int DECIMAL_NUMBER = 14;
    public static final int INTEGER_NUMBER = 15;
    public static final int NUM_TYPES = 16;
    private static String[] NAMES;
    private static char[] CODES;
    
    public static String getName(final int type) {
        return TokenType.NAMES[type];
    }
    
    public static char getCode(final int type) {
        return TokenType.CODES[type];
    }
    
    public static Normalizer getNormalizer(final int type) {
        switch (type) {
            case 3: {
                return TAXIDNormalizer.getInstance();
            }
            case 4: {
                return CCNormalizer.getInstance();
            }
            case 1: {
                return PhoneNormalizer.getInstance();
            }
            case 0: {
                return WordNormalizer.getInstance();
            }
            case 2: {
                return EmailNormalizer.getInstance();
            }
            case 9: {
                return PostalCodeNormalizer.getInstance();
            }
            case 6:
            case 14: {
                return NumberNormalizer.getInstance();
            }
            case 15: {
                return IntegerNumberNormalizer.getInstance();
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean hasNormalizer(final int type) {
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 9:
            case 14:
            case 15: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static Validator getValidator(final int type) {
        switch (type) {
            case 4: {
                return CCValidator.getInstance();
            }
            case 3: {
                return TaxIDValidator.getInstance();
            }
            case 8: {
                return IPAddressValidator.getInstance();
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean hasValidator(final int type) {
        switch (type) {
            case 3:
            case 4:
            case 8: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isNumericType(final int type) {
        switch (type) {
            case 6:
            case 14:
            case 15: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        TokenType.NAMES = new String[] { "WORD", "PHONE", "EMAIL", "TAXID", "CCN", "DATE", "NUMBER", "PERCENT", "IP_ADDRESS", "POSTAL_CODE", "URL", "PUNCTUATION", "SEPARATOR", "NFA_PATTERN", "DECIMAL_INTEGER", "INTEGER_NUMBER" };
        TokenType.CODES = new char[] { 'w', 'p', 'e', 't', 'c', 'd', 'n', 'r', 'i', 'o', 'u', 'q', 's', 'f', 'n', 'n', 'z' };
    }
}
