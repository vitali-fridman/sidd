// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.http;

import java.util.Collection;
import java.util.Locale;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpResponseFake implements HttpServletResponse
{
    public void addCookie(final Cookie cookie) {
    }
    
    public boolean containsHeader(final String string) {
        return false;
    }
    
    public String encodeURL(final String string) {
        return null;
    }
    
    public String encodeRedirectURL(final String string) {
        return null;
    }
    
    public String encodeUrl(final String string) {
        return null;
    }
    
    public String encodeRedirectUrl(final String string) {
        return null;
    }
    
    public void sendError(final int i, final String string) throws IOException {
    }
    
    public void sendError(final int i) throws IOException {
    }
    
    public void sendRedirect(final String string) throws IOException {
    }
    
    public void setDateHeader(final String string, final long l) {
    }
    
    public void addDateHeader(final String string, final long l) {
    }
    
    public void setHeader(final String string, final String string1) {
    }
    
    public void addHeader(final String string, final String string1) {
    }
    
    public void setIntHeader(final String string, final int i) {
    }
    
    public void addIntHeader(final String string, final int i) {
    }
    
    public void setStatus(final int i) {
    }
    
    public void setStatus(final int i, final String string) {
    }
    
    public String getCharacterEncoding() {
        return null;
    }
    
    public String getContentType() {
        return null;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new StringWriter());
    }
    
    public void setCharacterEncoding(final String string) {
    }
    
    public void setContentLength(final int i) {
    }
    
    public void setContentType(final String string) {
    }
    
    public void setBufferSize(final int i) {
    }
    
    public int getBufferSize() {
        return 0;
    }
    
    public void flushBuffer() throws IOException {
    }
    
    public void resetBuffer() {
    }
    
    public boolean isCommitted() {
        return false;
    }
    
    public void reset() {
    }
    
    public void setLocale(final Locale locale) {
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public String getHeader(final String arg0) {
        return null;
    }
    
    public Collection<String> getHeaderNames() {
        return null;
    }
    
    public Collection<String> getHeaders(final String arg0) {
        return null;
    }
    
    public int getStatus() {
        return 0;
    }
}
