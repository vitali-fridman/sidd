// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.load;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import com.vontu.profileindex.InputStreamFactory;
import java.util.logging.Logger;

public class DocumentIndexLoader
{
    private static final Logger log;
    private static final int BUFFER_SIZE = 524288;
    private final DocumentIndexHeaderReader headerReader;
    private final DocumentIndexEntryReader entryReader;
    
    public DocumentIndexLoader() {
        this(new DocumentIndexHeaderReader(), new DocumentIndexEntryReader());
    }
    
    DocumentIndexLoader(final DocumentIndexHeaderReader headerReader, final DocumentIndexEntryReader entryReader) {
        this.headerReader = headerReader;
        this.entryReader = entryReader;
    }
    
    public LiveDocumentIndex load(final InputStreamFactory streamFactory) throws IOException {
        DocumentIndexLoader.log.fine("Loading document profile from [" + streamFactory.name() + "]");
        final InputStream stream = streamFactory.getInputStream();
        try {
            final DataInputStream indexStream = new DataInputStream(new BufferedInputStream(stream, 524288));
            final DocumentIndexHeader header = this.headerReader.read(indexStream);
            final LiveDocumentIndex documentIndex = new LiveDocumentIndex(header);
            this.readDocuments(indexStream, header.getDocumentCount(), header.getSnippetSize(), documentIndex);
            return documentIndex;
        }
        finally {
            stream.close();
        }
    }
    
    private void readDocuments(final DataInputStream indexStream, final int documentCount, final int snippetSize, final LiveDocumentIndex documentIndex) throws IOException {
        for (int index = 0; index < documentCount; ++index) {
            final DocumentIndexEntry entry = this.entryReader.readDocument(indexStream, snippetSize);
            documentIndex.add(entry);
        }
        documentIndex.compact();
    }
    
    static {
        log = Logger.getLogger(DocumentIndexLoader.class.getName());
    }
}
