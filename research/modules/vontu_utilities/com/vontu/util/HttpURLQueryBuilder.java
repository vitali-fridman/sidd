// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class HttpURLQueryBuilder
{
    private static final char QUESTION_MARK_CHAR = '?';
    private static final char AMPERSAND_CHAR = '&';
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private StringBuilder query;
    private Character separator;
    
    public HttpURLQueryBuilder(final String query) {
        (this.query = new StringBuilder()).append(query);
        this.separator = this.determineSeparator(query);
    }
    
    Character determineSeparator(final String query) {
        if (this.endsWithAmperSand(query) || this.endsWithQuestionMark(query)) {
            return null;
        }
        if (this.hasAmperSand(query) || this.hasQuestionMark(query)) {
            return '&';
        }
        return '?';
    }
    
    private boolean hasQuestionMark(final String query) {
        return query.indexOf(63) != -1;
    }
    
    private boolean hasAmperSand(final String query) {
        return query.indexOf(38) != -1;
    }
    
    private boolean endsWithQuestionMark(final String query) {
        return query.endsWith("?");
    }
    
    private boolean endsWithAmperSand(final String query) {
        return query.endsWith("&");
    }
    
    public void appendParameter(final String parameter, final String value) {
        if (this.separator != null) {
            this.query.append(this.separator);
        }
        this.query.append(parameter).append('=').append(value);
        this.separator = '&';
    }
    
    @Override
    public String toString() {
        return this.query.toString();
    }
}
