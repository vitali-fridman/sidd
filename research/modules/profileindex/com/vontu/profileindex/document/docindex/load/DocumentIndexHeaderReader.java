// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

import java.io.IOException;
import java.io.DataInputStream;

class DocumentIndexHeaderReader
{
    public DocumentIndexHeader read(final DataInputStream indexStream) throws IOException {
        final int magicNumber = indexStream.readInt();
        if (magicNumber != 62052) {
            throw new IOException(String.format("Magic number mismatch in document index file, expected [%1$d] but found [%2$d]", 62052, magicNumber));
        }
        final int k = indexStream.readInt();
        final int t = indexStream.readInt();
        final int snippetSize = indexStream.readInt() * 2;
        final int snippetCheckThreshold = indexStream.readInt();
        final int numDocs = indexStream.readInt();
        final int numHashes = indexStream.readInt();
        final int numDocsWithSnippets = indexStream.readInt();
        return new DocumentIndexHeader(magicNumber, k, t, snippetSize, snippetCheckThreshold, numDocs, numHashes, numDocsWithSnippets);
    }
}
