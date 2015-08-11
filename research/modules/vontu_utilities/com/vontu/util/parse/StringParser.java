// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.parse;

import com.vontu.util.validate.StringValidator;
import java.util.ArrayList;
import java.util.List;

public class StringParser
{
    public static List<String> parseDelimittedStringAsList(final String parameter, final String delimitter) {
        final String[] parameterValues = parameter.split(delimitter);
        final List<String> parameterList = new ArrayList<String>();
        for (int i = 0; i < parameterValues.length; ++i) {
            final String value = parameterValues[i].trim();
            if (!"".equals(value)) {
                parameterList.add(parameterValues[i]);
            }
        }
        return parameterList;
    }
    
    public static int firstIndexOfChar(final String string, final Character character) {
        final List<Character> list = new ArrayList<Character>();
        list.add(character);
        return firstIndexOfChar(string, list, 0);
    }
    
    public static int firstIndexOfChar(final String string, final Character character, final int startIndex) {
        final List<Character> list = new ArrayList<Character>();
        list.add(character);
        return firstIndexOfChar(string, list, startIndex);
    }
    
    public static int firstIndexOfChar(final String string, final List<Character> charList) {
        return firstIndexOfChar(string, charList, 0);
    }
    
    public static int firstIndexOfChar(final String string, final List<Character> charList, final int startIndex) {
        int matchAt = -1;
        if (StringValidator.isEmptyString(string) || null == charList) {
            return -1;
        }
        for (int index = 0; index < charList.size(); ++index) {
            final int curMatch = string.indexOf(charList.get(index), startIndex);
            if (curMatch >= 0) {
                if (matchAt == -1) {
                    matchAt = curMatch;
                }
                else {
                    matchAt = Math.min(matchAt, curMatch);
                }
            }
        }
        return matchAt;
    }
}
