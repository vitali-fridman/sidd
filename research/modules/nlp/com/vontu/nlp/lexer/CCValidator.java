// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class CCValidator implements Validator
{
    private static Validator _instance;
    
    public static Validator getInstance() {
        if (CCValidator._instance == null) {
            CCValidator._instance = new CCValidator();
        }
        return CCValidator._instance;
    }
    
    @Override
    public boolean assertTokenValidity(final CharSequence text, final int position, final int length) {
        int sum = 0;
        int digit = 0;
        int addend = 0;
        boolean timesTwo = false;
        for (int i = length - 1; i >= 0; --i) {
            digit = Character.digit(text.charAt(position + i), 10);
            if (digit > -1) {
                if (timesTwo) {
                    addend = digit * 2;
                    if (addend > 9) {
                        addend -= 9;
                    }
                }
                else {
                    addend = digit;
                }
                sum += addend;
                timesTwo = !timesTwo;
            }
        }
        final int modulus = sum % 10;
        return modulus == 0;
    }
    
    @Override
    public boolean assertTokenValidity(final String value) {
        return this.assertTokenValidity(value, 0, value.length());
    }
}
