// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.parse;

import java.util.regex.Pattern;

public class CSVLineTokenizer
{
    private Pattern pattern;
    private static String METACHARACTERS;
    private char fieldDelimiter;
    
    public CSVLineTokenizer(final char fieldDelimiter) {
        this.generatePattern(this.fieldDelimiter = fieldDelimiter);
    }
    
    public char getFieldDelimiter() {
        return this.fieldDelimiter;
    }
    
    public String[] tokenizeString(final String line) {
        final String[] tokens = this.pattern.split(line);
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = this.formatCSVToken(tokens[i]);
        }
        return tokens;
    }
    
    private void generatePattern(final char fieldDelimiter) {
        final String escapedDelimiter = this.buildDelimiter(fieldDelimiter);
        this.pattern = Pattern.compile(escapedDelimiter);
    }
    
    private String buildDelimiter(final char fieldDelimiter) {
        final StringBuilder regexBuilder = new StringBuilder();
        if (isMetacharacterDelimiter(fieldDelimiter)) {
            regexBuilder.append('\\');
        }
        regexBuilder.append(fieldDelimiter);
        regexBuilder.append("(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        return regexBuilder.toString();
    }
    
    private String formatCSVToken(final String text) {
        final String trimmedAndUnquoted = text.trim().replaceAll("^\"|\"$", "").trim();
        return trimmedAndUnquoted.replaceAll("\"\"", "\"");
    }
    
    static boolean isMetacharacterDelimiter(final char delimiter) {
        return CSVLineTokenizer.METACHARACTERS.indexOf(delimiter) != -1;
    }
    
    static {
        CSVLineTokenizer.METACHARACTERS = "<([{\\^-=$!|]})?*+.>";
    }
}
