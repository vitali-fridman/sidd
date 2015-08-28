// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.cracker.TypeIdentifierDelegate;
import com.vontu.cracker.TextExtractionDelegate;
import com.vontu.cracker.NativeExtractionEngine;
import com.vontu.cracker.ExtractionSettings;
import com.vontu.cracker.ContentExtractionSystemConfiguration;
import com.vontu.cracker.TypeIdentificationHandler;
import com.vontu.cracker.TextExtractorVisitor;
import com.vontu.cracker.ExtractorFactory;
import com.vontu.cracker.ExtractionEngineFactory;

public class ContentExtractionProvider
{
    private ExtractionEngineFactory extractionEngineFactory;
    private ExtractorFactory extractorFactory;
    private TextExtractorVisitor textExtractor;
    private TypeIdentificationHandler typeIdentifier;
    
    public ContentExtractionProvider(final ContentExtractionSystemConfiguration configuration, final ExtractionSettings extractionSettings) {
        this((ExtractionEngineFactory)new NativeExtractionEngine(configuration), extractionSettings);
    }
    
    ContentExtractionProvider(final ExtractionEngineFactory extractionEngineFactory, final ExtractionSettings extractorSettings) {
        this(extractionEngineFactory, extractionEngineFactory.newExtractorFactory(), extractorSettings);
    }
    
    ContentExtractionProvider(final ExtractionEngineFactory extractionEngineFactory, final ExtractorFactory extractorFactory, final ExtractionSettings extractionSettings) {
        this(extractionEngineFactory, extractorFactory, (TextExtractorVisitor)new TextExtractionDelegate(extractorFactory.newTextExtractor(), extractionSettings), (TypeIdentificationHandler)new TypeIdentifierDelegate(extractorFactory.newFileTypeIdentifier(), (long)extractionSettings.getMaxSize()));
    }
    
    ContentExtractionProvider(final ExtractionEngineFactory extractionEngineFactory, final ExtractorFactory extractorFactory, final TextExtractorVisitor textExtractor, final TypeIdentificationHandler typeIdentifier) {
        this.extractionEngineFactory = extractionEngineFactory;
        this.extractorFactory = extractorFactory;
        this.textExtractor = textExtractor;
        this.typeIdentifier = typeIdentifier;
    }
    
    public TextExtractorVisitor getTextExtractor() {
        return this.textExtractor;
    }
    
    public TypeIdentificationHandler getTypeIdentifier() {
        return this.typeIdentifier;
    }
    
    public void dispose() {
        this.extractorFactory.dispose();
        this.extractionEngineFactory.dispose();
    }
}
