// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Iterator;
import java.util.List;

public class TabularTokenSet extends TokenSet
{
    public TabularTokenSet(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public TabularTokenSet() {
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(2048);
        sb.append("\t\tTaublar Data Set contains " + this.size() + " rows:\r\n");
        final Iterator it = this.iterator();
        while (it.hasNext()) {
            sb.append("\t\t\tRow:\r\n");
            final List row = it.next();
            final Iterator iter = row.iterator();
            while (iter.hasNext()) {
                sb.append("\t\t\t\t\t").append(iter.next() + "\r\n");
            }
        }
        return sb.toString();
    }
}
