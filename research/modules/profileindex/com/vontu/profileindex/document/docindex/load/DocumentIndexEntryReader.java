// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

import java.io.IOException;
import java.io.DataInputStream;

class DocumentIndexEntryReader
{
    public DocumentIndexEntry readDocument(final DataInputStream indexStream, final int snippetSize) throws IOException {
        final int documentId = indexStream.readInt();
        final int originalLengthIgnored = indexStream.readInt();
        final int documentTypeIgnored = indexStream.readInt();
        final int textRetrievedIgnored = indexStream.readInt();
        final int normalizedLengthIgnored = indexStream.readInt();
        final byte[] md5 = new byte[16];
        indexStream.readFully(md5);
        final int fingerprintCount = indexStream.readInt();
        final int snippetPresent = indexStream.readInt();
        final DocumentIndexEntry entry = new DocumentIndexEntry(documentId, md5, fingerprintCount);
        readFingerprints(indexStream, snippetSize, fingerprintCount, snippetPresent, entry);
        return entry;
    }
    
    private static void readFingerprints(final DataInputStream indexStream, final int snippetSize, final int fingerprintCount, final int snippetPresent, final DocumentIndexEntry entry) throws IOException {
        for (int counter = 0; counter < fingerprintCount; ++counter) {
            final int hash = indexStream.readInt();
            if (snippetPresent == 1) {
                final byte[] snippet = new byte[snippetSize];
                indexStream.readFully(snippet);
                entry.addSnippet(hash, snippet);
            }
            entry.addFingerprint(hash);
        }
    }
}
