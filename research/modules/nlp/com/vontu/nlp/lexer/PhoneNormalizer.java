// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class PhoneNormalizer extends AbstractNormalizer
{
    private static Normalizer _instance;
    
    public static Normalizer getInstance() {
        if (PhoneNormalizer._instance == null) {
            PhoneNormalizer._instance = new PhoneNormalizer();
        }
        return PhoneNormalizer._instance;
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, final int length) {
        final String phone = AbstractNormalizer.removeNonDigits(text, position, length);
        String[] normalized = null;
        switch (phone.length()) {
            case 0: {
                normalized = new String[0];
                break;
            }
            case 7: {
                normalized = new String[] { phone };
                break;
            }
            case 10: {
                normalized = new String[] { "1" + phone, phone.substring(3), phone.substring(0, 3) };
                break;
            }
            case 11: {
                normalized = new String[] { phone, phone.substring(4), phone.substring(1, 4) };
                break;
            }
            default: {
                normalized = new String[0];
                break;
            }
        }
        return normalized;
    }
}
