// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.http;

import javax.servlet.http.HttpSessionContext;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

public class HttpSessionFake implements HttpSession
{
    private Map<String, Object> _attributes;
    
    public HttpSessionFake() {
        this._attributes = new HashMap<String, Object>();
    }
    
    public Object getAttribute(final String arg0) {
        return this._attributes.get(arg0);
    }
    
    public Enumeration getAttributeNames() {
        return new Vector(this._attributes.keySet()).elements();
    }
    
    public long getCreationTime() {
        return 0L;
    }
    
    public String getId() {
        return null;
    }
    
    public long getLastAccessedTime() {
        return 0L;
    }
    
    public int getMaxInactiveInterval() {
        return 0;
    }
    
    public ServletContext getServletContext() {
        return null;
    }
    
    public HttpSessionContext getSessionContext() {
        return null;
    }
    
    public Object getValue(final String arg0) {
        return null;
    }
    
    public String[] getValueNames() {
        return null;
    }
    
    public void invalidate() {
    }
    
    public boolean isNew() {
        return false;
    }
    
    public void putValue(final String arg0, final Object arg1) {
    }
    
    public void removeAttribute(final String arg0) {
        this._attributes.remove(arg0);
    }
    
    public void removeValue(final String arg0) {
    }
    
    public void setAttribute(final String arg0, final Object arg1) {
        this._attributes.put(arg0, arg1);
    }
    
    public void setMaxInactiveInterval(final int arg0) {
    }
}
