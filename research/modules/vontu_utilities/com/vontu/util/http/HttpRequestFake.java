// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.http;

import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.DispatcherType;
import javax.servlet.AsyncContext;
import javax.servlet.RequestDispatcher;
import java.util.Locale;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.Collections;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Vector;
import java.security.Principal;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class HttpRequestFake implements HttpServletRequest
{
    private Map<String, List<String>> _parameters;
    private Map<String, Object> _attributes;
    private HttpSession _session;
    private Cookie[] _cookies;
    
    public HttpRequestFake() {
        this._parameters = new HashMap<String, List<String>>();
        this._attributes = new HashMap<String, Object>();
        this._session = (HttpSession)new HttpSessionFake();
    }
    
    public void addParameter(final String name, final String value) {
        List<String> values = this._parameters.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this._parameters.put(name, values);
        }
        values.add(value);
    }
    
    public String getAuthType() {
        return null;
    }
    
    public Cookie[] getCookies() {
        return this._cookies;
    }
    
    public void setCookies(final Cookie[] cookies) {
        this._cookies = cookies;
    }
    
    public long getDateHeader(final String string) {
        return 0L;
    }
    
    public String getHeader(final String string) {
        return null;
    }
    
    public Enumeration getHeaders(final String string) {
        return null;
    }
    
    public Enumeration getHeaderNames() {
        return null;
    }
    
    public int getIntHeader(final String string) {
        return 0;
    }
    
    public String getMethod() {
        return null;
    }
    
    public String getPathInfo() {
        return null;
    }
    
    public String getPathTranslated() {
        return null;
    }
    
    public String getContextPath() {
        return null;
    }
    
    public String getQueryString() {
        return null;
    }
    
    public String getRemoteUser() {
        return null;
    }
    
    public boolean isUserInRole(final String string) {
        return false;
    }
    
    public Principal getUserPrincipal() {
        return null;
    }
    
    public String getRequestedSessionId() {
        return null;
    }
    
    public String getRequestURI() {
        return null;
    }
    
    public StringBuffer getRequestURL() {
        return null;
    }
    
    public String getServletPath() {
        return null;
    }
    
    public HttpSession getSession(final boolean b) {
        return this.getSession();
    }
    
    public HttpSession getSession() {
        return this._session;
    }
    
    public boolean isRequestedSessionIdValid() {
        return false;
    }
    
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }
    
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }
    
    public Object getAttribute(final String string) {
        return this._attributes.get(string);
    }
    
    public Enumeration getAttributeNames() {
        return new Vector(this._attributes.keySet()).elements();
    }
    
    public String getCharacterEncoding() {
        return null;
    }
    
    public void setCharacterEncoding(final String string) throws UnsupportedEncodingException {
    }
    
    public int getContentLength() {
        return 0;
    }
    
    public String getContentType() {
        return null;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }
    
    public String getParameter(final String string) {
        if (this._parameters.containsKey(string)) {
            return this._parameters.get(string).get(0);
        }
        return null;
    }
    
    public Enumeration getParameterNames() {
        final Vector names = new Vector(this._parameters.keySet());
        return names.elements();
    }
    
    public String[] getParameterValues(final String string) {
        return this._parameters.get(string).toArray(new String[0]);
    }
    
    public Map getParameterMap() {
        final Map<String, String[]> map = new HashMap<String, String[]>();
        for (final Map.Entry<String, List<String>> entry : this._parameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return Collections.unmodifiableMap((Map<?, ?>)map);
    }
    
    public String getProtocol() {
        return null;
    }
    
    public String getScheme() {
        return null;
    }
    
    public String getServerName() {
        return null;
    }
    
    public int getServerPort() {
        return 0;
    }
    
    public BufferedReader getReader() throws IOException {
        return null;
    }
    
    public String getRemoteAddr() {
        return null;
    }
    
    public String getRemoteHost() {
        return null;
    }
    
    public void setAttribute(final String string, final Object object) {
        this._attributes.put(string, object);
    }
    
    public void removeAttribute(final String string) {
    }
    
    public Locale getLocale() {
        return Locale.getDefault();
    }
    
    public Enumeration getLocales() {
        return null;
    }
    
    public boolean isSecure() {
        return false;
    }
    
    public RequestDispatcher getRequestDispatcher(final String string) {
        return null;
    }
    
    public String getRealPath(final String string) {
        return null;
    }
    
    public int getRemotePort() {
        return 0;
    }
    
    public String getLocalName() {
        return null;
    }
    
    public String getLocalAddr() {
        return null;
    }
    
    public int getLocalPort() {
        return 0;
    }
    
    public AsyncContext getAsyncContext() {
        return null;
    }
    
    public DispatcherType getDispatcherType() {
        return null;
    }
    
    public ServletContext getServletContext() {
        return null;
    }
    
    public boolean isAsyncStarted() {
        return false;
    }
    
    public boolean isAsyncSupported() {
        return false;
    }
    
    public AsyncContext startAsync() {
        return null;
    }
    
    public AsyncContext startAsync(final ServletRequest arg0, final ServletResponse arg1) {
        return null;
    }
    
    public boolean authenticate(final HttpServletResponse arg0) throws IOException, ServletException {
        return false;
    }
    
    public Part getPart(final String arg0) throws IOException, IllegalStateException, ServletException {
        return null;
    }
    
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        return null;
    }
    
    public void login(final String arg0, final String arg1) throws ServletException {
    }
    
    public void logout() throws ServletException {
    }
}
