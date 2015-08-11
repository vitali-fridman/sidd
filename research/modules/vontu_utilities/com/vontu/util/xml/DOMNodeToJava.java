// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public abstract class DOMNodeToJava<T>
{
    public abstract T convert(final Node p0) throws DOMNodeToJavaException;
    
    protected static String getAttribute(final Node node, final String attributeName) {
        if (node.getNodeType() != 1) {
            throw new IllegalArgumentException("Node is not an element node: " + node.getNodeType());
        }
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);
        if (attributeNode != null) {
            return attributeNode.getNodeValue();
        }
        return null;
    }
    
    protected static String getPCDATA(final Node node) {
        if (node.getNodeType() != 1) {
            throw new IllegalArgumentException("Node is not an element node: " + node.getNodeType());
        }
        if (node.getChildNodes().getLength() > 0) {
            final NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                if (childNodes.item(i).getNodeType() == 3) {
                    return childNodes.item(i).getNodeValue();
                }
            }
            return null;
        }
        return null;
    }
    
    protected static Node getChildNode(final Node node, final String nodeName) {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(nodeName)) {
                return childNode;
            }
        }
        return null;
    }
}
