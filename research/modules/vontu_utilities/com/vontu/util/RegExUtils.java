// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class RegExUtils
{
    private static final String CASE_INSENSITIVE = "(?i)";
    private static final String OR = "|";
    private static final String BEGIN_NON_CAPTURE_GROUP = "(?:";
    private static final String END_GROUP = ")";
    private static final String ESCAPE = "\\";
    private static final String WHITESPACE_CHAR = "\\s";
    private static final String[] ESCAPE_CHARACTERS;
    private static final String[] CORRESPONDING_ESCAPED_CHAR_FOR_FILE_NAMES;
    private static final String[] STRING_CHARACTERS_TO_ESCAPE_FOR_REGEX;
    
    public static Pattern generateRegex(final String[] fileNames) {
        return Pattern.compile(generateRegexString(fileNames));
    }
    
    public static String generateRegexString(final String[] fileNames) {
        final StringBuffer regex = new StringBuffer("(?i)");
        regex.append("(?:");
        for (int i = 0; i < fileNames.length; ++i) {
            regex.append(getFileNameRegex(fileNames[i]));
            if (i + 1 < fileNames.length) {
                regex.append("|");
            }
        }
        regex.append(")");
        return regex.toString();
    }
    
    public static String stringToRegex(String toEscape) {
        final String[] escapeChars = RegExUtils.STRING_CHARACTERS_TO_ESCAPE_FOR_REGEX;
        for (int i = 0; i < escapeChars.length; ++i) {
            toEscape = toEscape.replaceAll(escapeChars[i], "\\\\" + escapeChars[i]);
        }
        return toEscape;
    }
    
    private static String getFileNameRegex(String fileName) {
        final StringBuffer regex = new StringBuffer("(?:");
        fileName = escapeChars(fileName, RegExUtils.ESCAPE_CHARACTERS, RegExUtils.CORRESPONDING_ESCAPED_CHAR_FOR_FILE_NAMES);
        fileName = fileName.replaceAll("\\s", "\\\\s");
        regex.append(fileName);
        regex.append(")");
        return regex.toString();
    }
    
    private static String escapeChars(String toEscape, final String[] escapeChars, final String[] correspondingChars) {
        for (int i = 0; i < escapeChars.length; ++i) {
            toEscape = toEscape.replaceAll(escapeChars[i], correspondingChars[i]);
        }
        return toEscape;
    }
    
    public static String[] parseOnRegex(final String toParse, final String regex) {
        final String[] parsed = toParse.split(regex);
        final ArrayList parsedList = new ArrayList();
        for (int i = 0; i < parsed.length; ++i) {
            if (parsed[i] != null) {
                parsed[i] = parsed[i].trim();
                if (!"".equals(parsed[i])) {
                    parsedList.add(parsed[i]);
                }
            }
        }
        return (String[]) parsedList.toArray(new String[0]);
    }
    
    static {
        ESCAPE_CHARACTERS = new String[] { "\\\\", "\\[", "\\]", "\\(", "\\)", "\\.", "\\?", "\\+", "\\{", "\\}", "\\*", "\\$", "\\^", "\\:", "\\|", "\\/", "\\\\\\\\" };
        CORRESPONDING_ESCAPED_CHAR_FOR_FILE_NAMES = new String[] { "\\\\\\\\", "\\\\[", "\\\\]", "\\\\(", "\\\\)", "\\\\.", ".", "\\\\+", "\\\\{", "\\\\}", ".*", "\\\\\\$", "\\\\^", "\\\\:", "\\\\|", "(?:\\\\\\\\|/)", "(?:\\\\\\\\|/)" };
        STRING_CHARACTERS_TO_ESCAPE_FOR_REGEX = new String[] { "\\\\", "\\[", "\\]", "\\(", "\\)", "\\.", "\\?", "\\+", "\\{", "\\}", "\\*", "\\$", "\\^", "\\|" };
        final String[] CORRESPONDING_ESCAPED_CHAR_FOR_KEYWORDS = new String[RegExUtils.ESCAPE_CHARACTERS.length];
        for (int i = 0; i < RegExUtils.ESCAPE_CHARACTERS.length; ++i) {
            CORRESPONDING_ESCAPED_CHAR_FOR_KEYWORDS[i] = "\\\\" + RegExUtils.ESCAPE_CHARACTERS[i];
        }
    }
}
