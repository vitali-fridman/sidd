// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.Field;
import com.hunnysoft.jmime.Headers;
import com.hunnysoft.jmime.Message;
import com.hunnysoft.jmime.ByteString;
import java.util.HashSet;

public class UniqueIdEmailHeaders
{
    private static final String[] matchingHeadersList;
    private static HashSet<ByteString> headersToMatch;
    
    private static void populateMatchingHeaders(final String[] headersList) {
        UniqueIdEmailHeaders.headersToMatch = new HashSet<ByteString>();
        for (final String headerName : headersList) {
            UniqueIdEmailHeaders.headersToMatch.add(new ByteString(headerName));
        }
    }
    
    public static Headers extractMatchingMessageHeaders(final Message mimeMessage) {
        final Headers matchedMessageHeaders = new Headers();
        mimeMessage.parse();
        final Headers messageHeaders = mimeMessage.headers();
        for (int numFields = messageHeaders.numFields(), i = 0; i < numFields; ++i) {
            final Field field = messageHeaders.fieldAt(i);
            if (UniqueIdEmailHeaders.headersToMatch.contains(field.getFieldName().toLowerCase())) {
                matchedMessageHeaders.addField(field);
            }
        }
        matchedMessageHeaders.assemble();
        return matchedMessageHeaders;
    }
    
    static {
        populateMatchingHeaders(matchingHeadersList = new String[] { "from", "to", "cc", "subject", "date", "message-id" });
    }
}
