// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import com.vontu.profileindexer.IndexerException;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;

public class EndpointDocumentIndexCreator
{
    private static final Logger logger;
    private boolean isRunning;
    private final MessageDigestCreator messageDigestCreator;
    private final EndpointIndexWriter endpointIndexWriter;
    private final EndpointIndexProvider currentIndexProvider;
    
    public EndpointDocumentIndexCreator(final MessageDigestCreator messageDigestCreator, final EndpointIndexWriter endpointIndexWriter) throws IOException {
        this(messageDigestCreator, endpointIndexWriter, new EndpointEmptyIndexProvider());
    }
    
    public EndpointDocumentIndexCreator(final MessageDigestCreator messageDigestCreator, final EndpointIndexWriter endpointIndexWriter, final EndpointIndexProvider currentIndexProvider) {
        this.isRunning = true;
        this.messageDigestCreator = messageDigestCreator;
        this.endpointIndexWriter = endpointIndexWriter;
        this.currentIndexProvider = currentIndexProvider;
    }
    
    public void addDocument(final int docID, final byte[] content) throws IndexerException, IOException {
        this.writeEntriesFromPreviousIndexWithLesserDocId(docID);
        if (this.currentIndexProvider.hasMoreEntries() && this.currentIndexProvider.peekNextEntryDocId() == docID) {
            EndpointDocumentIndexCreator.logger.log(Level.SEVERE, "Trying to add document that already exists. Keeping original entry.");
            this.endpointIndexWriter.writeEntry(this.currentIndexProvider.getNextEntry());
            return;
        }
        final EndpointIndexEntry endpointIndexEntry = new EndpointIndexEntry(docID, content.length, this.messageDigestCreator.createMD5Digest(content));
        this.endpointIndexWriter.writeEntry(endpointIndexEntry);
    }
    
    public void replaceDocument(final int docID, final byte[] content) throws IndexerException, IOException {
        this.writeEntriesFromPreviousIndexWithLesserDocId(docID);
        if (this.currentIndexProvider.hasMoreEntries() && this.currentIndexProvider.peekNextEntryDocId() == docID) {
            this.currentIndexProvider.skipNextEntry();
            final EndpointIndexEntry endpointIndexEntry = new EndpointIndexEntry(docID, content.length, this.messageDigestCreator.createMD5Digest(content));
            this.endpointIndexWriter.writeEntry(endpointIndexEntry);
        }
        else {
            EndpointDocumentIndexCreator.logger.log(Level.SEVERE, "Trying replace document that does not exist in the index.");
        }
    }
    
    public void deleteDocument(final int docID) throws IOException {
        this.writeEntriesFromPreviousIndexWithLesserDocId(docID);
        if (this.currentIndexProvider.hasMoreEntries() && this.currentIndexProvider.peekNextEntryDocId() == docID) {
            this.currentIndexProvider.skipNextEntry();
        }
        else {
            EndpointDocumentIndexCreator.logger.log(Level.SEVERE, "Trying to delete document that does not exist in the index.");
        }
    }
    
    private void writeEntriesFromPreviousIndexWithLesserDocId(final int docId) throws IOException {
        while (this.currentIndexProvider.hasMoreEntries() && this.currentIndexProvider.peekNextEntryDocId() < docId) {
            this.endpointIndexWriter.writeEntry(this.currentIndexProvider.getNextEntry());
        }
    }
    
    public void finish() throws IOException {
        while (this.currentIndexProvider.hasMoreEntries()) {
            this.endpointIndexWriter.writeEntry(this.currentIndexProvider.getNextEntry());
        }
        this.endpointIndexWriter.writeHeader();
        this.isRunning = false;
    }
    
    public void cancelIndexCreation() throws IOException {
        this.endpointIndexWriter.cancelIndexWriting();
    }
    
    static {
        logger = Logger.getLogger(EndpointDocumentIndexCreator.class.getName());
    }
}
