// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TaxIDValidator implements Validator
{
    private static Validator _instance;
    
    public static Validator getInstance() {
        if (TaxIDValidator._instance == null) {
            TaxIDValidator._instance = new TaxIDValidator();
        }
        return TaxIDValidator._instance;
    }
    
    @Override
    public boolean assertTokenValidity(final CharSequence text, final int position, final int length) {
        try {
            final String areaNumberString = text.subSequence(position, position + 3).toString();
            final int areaNumber = Integer.parseInt(areaNumberString);
            if (areaNumber == 0) {
                return false;
            }
            boolean hasSeparator = false;
            String groupNumberString = null;
            if (Character.isDigit(text.charAt(position + 3))) {
                groupNumberString = text.subSequence(position + 3, position + 5).toString();
            }
            else {
                hasSeparator = true;
                groupNumberString = text.subSequence(position + 4, position + 6).toString();
            }
            final int groupNumber = Integer.parseInt(groupNumberString);
            if (groupNumber < 1) {
                return false;
            }
            final int serialNumberIndex = hasSeparator ? 6 : 5;
            String serialNumberString = null;
            if (Character.isDigit(text.charAt(position + serialNumberIndex))) {
                serialNumberString = text.subSequence(position + serialNumberIndex, position + serialNumberIndex + 4).toString();
            }
            else {
                serialNumberString = text.subSequence(position + serialNumberIndex + 1, position + serialNumberIndex + 5).toString();
            }
            final int serialNumber = Integer.parseInt(serialNumberString);
            return serialNumber > 0;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public boolean assertTokenValidity(final String value) {
        return this.assertTokenValidity(value, 0, value.length());
    }
}
