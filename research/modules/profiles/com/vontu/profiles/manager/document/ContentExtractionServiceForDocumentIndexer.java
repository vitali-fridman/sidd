// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.cracker.ContentExtractorException;
import com.vontu.cracker.EncodedString;
import com.vontu.cracker.DocumentType;
import com.vontu.cracker.MessageTimeOut;

public class ContentExtractionServiceForDocumentIndexer implements DocumentExtractor
{
    private static ContentExtractionServiceForDocumentIndexer instance;
    private ContentExtractionProvider contentExtractionProvider;
    
    private ContentExtractionServiceForDocumentIndexer() {
        this.contentExtractionProvider = null;
        this.reset();
    }
    
    private void reset() {
        if (this.contentExtractionProvider != null) {
            this.contentExtractionProvider.dispose();
        }
        this.contentExtractionProvider = ContentExtractionProviderFactory.getInstance().createContentExtractionProvider();
    }
    
    public static synchronized ContentExtractionServiceForDocumentIndexer getInstance() {
        if (ContentExtractionServiceForDocumentIndexer.instance == null) {
            ContentExtractionServiceForDocumentIndexer.instance = new ContentExtractionServiceForDocumentIndexer();
        }
        return ContentExtractionServiceForDocumentIndexer.instance;
    }
    
    @Override
    public EncodedString getTextForFile(final byte[] fileData, final String sourceEncoding, final String name, final MessageTimeOut messageTimeOut, final DocumentType type) throws DocumentExtractionException {
        try {
            return this.contentExtractionProvider.getTextExtractor().getTextForFile(fileData, sourceEncoding, name, messageTimeOut, type);
        }
        catch (ContentExtractorException exception) {
            this.reset();
            throw new DocumentExtractionException((Throwable)exception);
        }
    }
    
    @Override
    public DocumentType getTypeForFile(final byte[] fileData, final MessageTimeOut messageTimeOut) throws DocumentExtractionException {
        try {
            return this.contentExtractionProvider.getTypeIdentifier().getTypeForFile(fileData, messageTimeOut);
        }
        catch (ContentExtractorException exception) {
            this.reset();
            throw new DocumentExtractionException((Throwable)exception);
        }
    }
    
    static {
        ContentExtractionServiceForDocumentIndexer.instance = null;
    }
}
