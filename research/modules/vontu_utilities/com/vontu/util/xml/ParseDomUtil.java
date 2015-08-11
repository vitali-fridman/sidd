// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class ParseDomUtil
{
    public static String consolidateTextNodes(final Element parentNode) {
        final NodeList children = parentNode.getChildNodes();
        final StringBuilder out = new StringBuilder();
        for (int j = 0; j < children.getLength(); ++j) {
            if (children.item(j).getNodeType() == 3) {
                out.append(children.item(j).getNodeValue());
            }
        }
        return out.toString();
    }
}
