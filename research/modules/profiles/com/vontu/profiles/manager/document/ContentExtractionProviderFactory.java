// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.cracker.ExtractionSettings;
import com.vontu.cracker.ContentExtractionSystemConfiguration;

public class ContentExtractionProviderFactory
{
    private static ContentExtractionProviderFactory instance;
    private ContentExtractionSystemConfiguration configuration;
    private ExtractionSettings extractionSettings;
    
    private ContentExtractionProviderFactory(final ContentExtractionSystemConfiguration configuration, final ExtractionSettings extractionSettings) {
        this.configuration = configuration;
        this.extractionSettings = extractionSettings;
    }
    
    public static synchronized ContentExtractionProviderFactory getInstance() {
        if (ContentExtractionProviderFactory.instance == null) {
            ContentExtractionProviderFactory.instance = new ContentExtractionProviderFactory(new ContentExtractionSystemConfiguration("com.vontu.manager.log4cxx.config"), new ExtractionSettings(IndexerConfig.getSettings()));
        }
        return ContentExtractionProviderFactory.instance;
    }
    
    public ContentExtractionProvider createContentExtractionProvider() {
        return new ContentExtractionProvider(this.configuration, this.extractionSettings);
    }
    
    static {
        ContentExtractionProviderFactory.instance = null;
    }
}
