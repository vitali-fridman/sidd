// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.bytestring;

import com.hunnysoft.jmime.ByteString;

public class ByteStringUtilities
{
    public static ByteString duplicate(final ByteString in) {
        final byte[] data = new byte[in.length()];
        System.arraycopy(in.data(), in.offset(), data, 0, in.length());
        return new ByteString(in);
    }
    
    public static int indexOfIgnoreCase(final ByteString content, final ByteString search) {
        return indexOfIgnoreCase(content.data(), search.data());
    }
    
    public static int indexOfIgnoreCase(final byte[] content, final byte[] search) {
        int index = -1;
        final int len = search.length;
        for (int limit = content.length - len + 1, i = 0; i < limit; ++i) {
            final int n = ByteString.strncasecmp(content, i, len, search, 0, len, len);
            if (n == 0) {
                index = i;
                break;
            }
        }
        return index;
    }
}
