// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Set;

public class SQLFormatter
{
    private static final Set<String> BEGIN_CLAUSES;
    private static final Set<String> END_CLAUSES;
    private static final Set<String> LOGICAL;
    private static final Set<String> QUANTIFIERS;
    private static final Set<String> DML;
    private static final Set<String> MISC;
    public static final String WHITESPACE = " \n\r\f\t";
    String indentString;
    String initial;
    boolean beginLine;
    boolean afterBeginBeforeEnd;
    boolean afterByOrSetOrFromOrSelect;
    boolean afterValues;
    boolean afterOn;
    boolean afterBetween;
    boolean afterInsert;
    int inFunction;
    int parensSinceSelect;
    private String lineEnd;
    private LinkedList<Integer> parenCounts;
    private LinkedList<Boolean> afterByOrFromOrSelects;
    int indent;
    StringBuffer result;
    StringTokenizer tokens;
    String lastToken;
    String token;
    String lcToken;
    
    public SQLFormatter(final String sql) {
        this.indentString = "    ";
        this.initial = "\n    ";
        this.beginLine = true;
        this.afterBeginBeforeEnd = false;
        this.afterByOrSetOrFromOrSelect = false;
        this.afterValues = false;
        this.afterOn = false;
        this.afterBetween = false;
        this.afterInsert = false;
        this.inFunction = 0;
        this.parensSinceSelect = 0;
        this.lineEnd = "\n";
        this.parenCounts = new LinkedList<Integer>();
        this.afterByOrFromOrSelects = new LinkedList<Boolean>();
        this.indent = 1;
        this.result = new StringBuffer();
        this.setLineEnd();
        this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
    }
    
    public SQLFormatter setInitialString(final String initial) {
        this.setLineEnd();
        this.initial = initial;
        return this;
    }
    
    public SQLFormatter setIndentString(final String indent) {
        this.indentString = indent;
        return this;
    }
    
    private void setLineEnd() {
        this.lineEnd = System.getProperty("line.separator");
        if (this.lineEnd == null) {
            this.lineEnd = "\n";
        }
        this.initial = this.lineEnd + "    ";
    }
    
    public String format() {
        this.result.append(this.initial);
        while (this.tokens.hasMoreTokens()) {
            this.token = this.tokens.nextToken();
            this.lcToken = this.token.toLowerCase();
            if ("'".equals(this.token)) {
                String t;
                do {
                    t = this.tokens.nextToken();
                    this.token += t;
                } while (!"'".equals(t));
            }
            else if ("\"".equals(this.token)) {
                String t;
                do {
                    t = this.tokens.nextToken();
                    this.token += t;
                } while (!"\"".equals(t));
            }
            if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token)) {
                this.commaAfterByOrFromOrSelect();
            }
            else if (this.afterOn && ",".equals(this.token)) {
                this.commaAfterOn();
            }
            else if ("(".equals(this.token)) {
                this.openParen();
            }
            else if (")".equals(this.token)) {
                this.closeParen();
            }
            else if (SQLFormatter.BEGIN_CLAUSES.contains(this.lcToken)) {
                this.beginNewClause();
            }
            else if (SQLFormatter.END_CLAUSES.contains(this.lcToken)) {
                this.endNewClause();
            }
            else if ("select".equals(this.lcToken)) {
                this.select();
            }
            else if (SQLFormatter.DML.contains(this.lcToken)) {
                this.updateOrInsertOrDelete();
            }
            else if ("values".equals(this.lcToken)) {
                this.values();
            }
            else if ("on".equals(this.lcToken)) {
                this.on();
            }
            else if (this.afterBetween && this.lcToken.equals("and")) {
                this.misc();
                this.afterBetween = false;
            }
            else if (SQLFormatter.LOGICAL.contains(this.lcToken)) {
                this.logical();
            }
            else if (isWhitespace(this.token)) {
                this.white();
            }
            else if ("{ts".equals(this.token)) {
                String timestamp = "TO_TIMESTAMP ('";
                while (!"'".equals(this.tokens.nextToken())) {}
                for (String tempToken = this.tokens.nextToken(); !"}".equals(tempToken); tempToken = this.tokens.nextToken()) {
                    timestamp += tempToken;
                }
                timestamp += ", 'YYYY-MM-DD HH24:MI:SS.FF')";
                this.result.append(timestamp);
            }
            else {
                this.misc();
            }
            if (!isWhitespace(this.token)) {
                this.lastToken = this.lcToken;
            }
        }
        return this.result.toString();
    }
    
    private void commaAfterOn() {
        this.out();
        --this.indent;
        this.newline();
        this.afterOn = false;
        this.afterByOrSetOrFromOrSelect = true;
    }
    
    private void commaAfterByOrFromOrSelect() {
        this.out();
        this.newline();
    }
    
    private void logical() {
        if ("end".equals(this.lcToken)) {
            --this.indent;
        }
        this.newline();
        this.out();
        this.beginLine = false;
    }
    
    private void on() {
        ++this.indent;
        this.afterOn = true;
        this.newline();
        this.out();
        this.beginLine = false;
    }
    
    private void misc() {
        this.out();
        if ("between".equals(this.lcToken)) {
            this.afterBetween = true;
        }
        if (this.afterInsert) {
            this.newline();
            this.afterInsert = false;
        }
        else {
            this.beginLine = false;
            if ("case".equals(this.lcToken)) {
                ++this.indent;
            }
        }
    }
    
    private void white() {
        if (!this.beginLine) {
            this.result.append(" ");
        }
    }
    
    private void updateOrInsertOrDelete() {
        this.out();
        ++this.indent;
        this.beginLine = false;
        if ("update".equals(this.lcToken)) {
            this.newline();
        }
        if ("insert".equals(this.lcToken)) {
            this.afterInsert = true;
        }
    }
    
    private void select() {
        this.out();
        ++this.indent;
        this.newline();
        this.parenCounts.addLast(new Integer(this.parensSinceSelect));
        this.afterByOrFromOrSelects.addLast(new Boolean(this.afterByOrSetOrFromOrSelect));
        this.parensSinceSelect = 0;
        this.afterByOrSetOrFromOrSelect = true;
    }
    
    private void out() {
        this.result.append(this.token);
    }
    
    private void endNewClause() {
        if (!this.afterBeginBeforeEnd) {
            --this.indent;
            if (this.afterOn) {
                --this.indent;
                this.afterOn = false;
            }
            this.newline();
        }
        this.out();
        if (!"union".equals(this.lcToken)) {
            ++this.indent;
        }
        this.newline();
        this.afterBeginBeforeEnd = false;
        this.afterByOrSetOrFromOrSelect = ("by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken));
    }
    
    private void beginNewClause() {
        if (!this.afterBeginBeforeEnd) {
            if (this.afterOn) {
                --this.indent;
                this.afterOn = false;
            }
            --this.indent;
            this.newline();
        }
        this.out();
        this.beginLine = false;
        this.afterBeginBeforeEnd = true;
    }
    
    private void values() {
        --this.indent;
        this.newline();
        this.out();
        ++this.indent;
        this.newline();
        this.afterValues = true;
    }
    
    private void closeParen() {
        --this.parensSinceSelect;
        if (this.parensSinceSelect < 0) {
            --this.indent;
            this.parensSinceSelect = this.parenCounts.removeLast();
            this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast();
        }
        if (this.inFunction > 0) {
            --this.inFunction;
            this.out();
        }
        else {
            if (!this.afterByOrSetOrFromOrSelect) {
                --this.indent;
                this.newline();
            }
            this.out();
        }
        this.beginLine = false;
    }
    
    private void openParen() {
        if (isFunctionName(this.lastToken) || this.inFunction > 0) {
            ++this.inFunction;
        }
        this.beginLine = false;
        if (this.inFunction > 0) {
            this.out();
        }
        else {
            this.out();
            if (!this.afterByOrSetOrFromOrSelect) {
                ++this.indent;
                this.newline();
                this.beginLine = true;
            }
        }
        ++this.parensSinceSelect;
    }
    
    private static boolean isFunctionName(final String tok) {
        final char begin = tok.charAt(0);
        final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '\"' == begin;
        return isIdentifier && !SQLFormatter.LOGICAL.contains(tok) && !SQLFormatter.END_CLAUSES.contains(tok) && !SQLFormatter.QUANTIFIERS.contains(tok) && !SQLFormatter.DML.contains(tok) && !SQLFormatter.MISC.contains(tok);
    }
    
    private static boolean isWhitespace(final String token) {
        return " \n\r\f\t".indexOf(token) >= 0;
    }
    
    private void newline() {
        this.result.append(this.lineEnd);
        for (int i = 0; i < this.indent; ++i) {
            this.result.append(this.indentString);
        }
        this.beginLine = true;
    }
    
    static {
        BEGIN_CLAUSES = new HashSet<String>();
        END_CLAUSES = new HashSet<String>();
        LOGICAL = new HashSet<String>();
        QUANTIFIERS = new HashSet<String>();
        DML = new HashSet<String>();
        MISC = new HashSet<String>();
        SQLFormatter.BEGIN_CLAUSES.add("left");
        SQLFormatter.BEGIN_CLAUSES.add("right");
        SQLFormatter.BEGIN_CLAUSES.add("inner");
        SQLFormatter.BEGIN_CLAUSES.add("outer");
        SQLFormatter.BEGIN_CLAUSES.add("group");
        SQLFormatter.BEGIN_CLAUSES.add("order");
        SQLFormatter.END_CLAUSES.add("where");
        SQLFormatter.END_CLAUSES.add("set");
        SQLFormatter.END_CLAUSES.add("having");
        SQLFormatter.END_CLAUSES.add("join");
        SQLFormatter.END_CLAUSES.add("from");
        SQLFormatter.END_CLAUSES.add("by");
        SQLFormatter.END_CLAUSES.add("join");
        SQLFormatter.END_CLAUSES.add("into");
        SQLFormatter.END_CLAUSES.add("union");
        SQLFormatter.LOGICAL.add("and");
        SQLFormatter.LOGICAL.add("or");
        SQLFormatter.LOGICAL.add("when");
        SQLFormatter.LOGICAL.add("else");
        SQLFormatter.LOGICAL.add("end");
        SQLFormatter.QUANTIFIERS.add("in");
        SQLFormatter.QUANTIFIERS.add("all");
        SQLFormatter.QUANTIFIERS.add("exists");
        SQLFormatter.QUANTIFIERS.add("some");
        SQLFormatter.QUANTIFIERS.add("any");
        SQLFormatter.DML.add("insert");
        SQLFormatter.DML.add("update");
        SQLFormatter.DML.add("delete");
        SQLFormatter.MISC.add("select");
        SQLFormatter.MISC.add("on");
    }
}
