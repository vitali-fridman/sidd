// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class XHTMLNamespaceContext implements NamespaceContext
{
    private static final String XHTML_NAMESPACE = "xhtml";
    private static final String XHTML_URI = "http://www.w3.org/1999/xhtml";
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        if (prefix.equals("") || prefix.equals("xhtml")) {
            return "http://www.w3.org/1999/xhtml";
        }
        return null;
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return "";
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        final ArrayList list = new ArrayList();
        list.add(this.getPrefix(namespaceURI));
        if (namespaceURI.equals("http://www.w3.org/1999/xhtml")) {
            list.add("xhtml");
        }
        return list.iterator();
    }
}
