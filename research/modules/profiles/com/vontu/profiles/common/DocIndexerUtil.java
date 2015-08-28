// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

public class DocIndexerUtil
{
    public static final String FILE_NAME_EXTENSION = ".rdx";
    public static final String FILE_NAME_PREFIX = "DocSource";
    public static final String ENDPOINT_FILE_NAME_PREFIX = "EndpointDocSource";
    
    public static String getIndexFileName(final int docSourceId, final int indexVersion) {
        return getIndexFileName(String.valueOf(docSourceId), indexVersion);
    }
    
    public static String getIndexFileName(final String profileId, final int indexVersion) {
        return "DocSource." + profileId + '.' + indexVersion + ".rdx";
    }
    
    public static String getEndpointIndexFileName(final int docSourceId, final int indexVersion) {
        return "EndpointDocSource." + docSourceId + '.' + indexVersion + ".rdx";
    }
    
    public static boolean snippetsMatched(final byte[] indexSnippet, final char[] messageText, final int position, final int k) {
        for (int numBytes = indexSnippet.length / 2, i = 0, j = indexSnippet.length - 1; i < numBytes; ++i, --j) {
            if (indexSnippet[i] != (byte)messageText[position + i]) {
                return false;
            }
            if (indexSnippet[j] != (byte)messageText[position + k - 1 - i]) {
                return false;
            }
        }
        return true;
    }
}
