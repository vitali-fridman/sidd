// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class JavaScriptTextEscaper
{
    public static String escape(final String text) {
        if (text == null) {
            return null;
        }
        for (int ln = text.length(), i = 0; i < ln; ++i) {
            char c = text.charAt(i);
            if (c == '\"' || c == '\'' || c == '\\' || c == '>' || c < ' ') {
                final StringBuffer b = new StringBuffer(ln + 4);
                b.append(text.substring(0, i));
                while (true) {
                    if (c == '\"') {
                        b.append("\\\"");
                    }
                    else if (c == '\'') {
                        b.append("\\'");
                    }
                    else if (c == '\\') {
                        b.append("\\\\");
                    }
                    else if (c == '>') {
                        b.append("\\>");
                    }
                    else if (c < ' ') {
                        if (c == '\n') {
                            b.append("\\n");
                        }
                        else if (c == '\r') {
                            b.append("\\r");
                        }
                        else if (c == '\f') {
                            b.append("\\f");
                        }
                        else if (c == '\b') {
                            b.append("\\b");
                        }
                        else if (c == '\t') {
                            b.append("\\t");
                        }
                        else {
                            b.append("\\x");
                            int x = c / '\u0010';
                            b.append((char)((x >= 10) ? (x - 10 + 65) : (x + 48)));
                            x = (c & '\u000f');
                            b.append((char)((x >= 10) ? (x - 10 + 65) : (x + 48)));
                        }
                    }
                    else {
                        b.append(c);
                    }
                    if (++i >= ln) {
                        break;
                    }
                    c = text.charAt(i);
                }
                return b.toString();
            }
        }
        return text;
    }
}
