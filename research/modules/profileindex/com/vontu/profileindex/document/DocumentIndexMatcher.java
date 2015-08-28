// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document;

import com.vontu.util.Disposable;
import com.vontu.profileindex.IndexError;
import com.vontu.docindex.algorithm.ContentNormalizer;
import java.util.logging.Level;
import com.vontu.util.CharArrayCharSequence;
import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;
import com.vontu.detection.condition.DocumentMatchCondition;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.Stopwatch;
import com.vontu.profileindex.ProfileIndexDescriptor;
import java.util.logging.Logger;
import com.vontu.profileindex.ProfileIndex;

class DocumentIndexMatcher implements DocumentProfileMatcher, ProfileIndex
{
    private static final Logger _logger;
    private static final String MIN_NORMALIZED_LENGTH_PROPERTY = "min_normalized_size";
    private static final int MIN_NORMALIZED_LENGTH_DEFAULT = 30;
    private final DocIndex _index;
    private final ProfileIndexDescriptor _indexDescriptor;
    private final int _minNormalizedSize;
    private final Stopwatch _stopwatch;
    
    DocumentIndexMatcher(final DocIndex index, final ProfileIndexDescriptor indexDescriptor, final SettingProvider settings) {
        this(index, indexDescriptor, new SettingReader(settings, DocumentIndexMatcher._logger));
    }
    
    DocumentIndexMatcher(final DocIndex index, final ProfileIndexDescriptor indexDescriptor, final SettingReader settingReader) {
        this._stopwatch = new Stopwatch("DocumentIndexMatcher");
        this._index = index;
        this._indexDescriptor = indexDescriptor;
        this._minNormalizedSize = settingReader.getIntSetting("min_normalized_size", 30);
    }
    
    @Override
    public DocumentConditionViolation detectViolation(final DocumentMatchCondition condition, final byte[] binaryContent) throws IndexException {
        return this._index.findBinaryDocMatches(binaryContent);
    }
    
    public DocumentConditionViolation detectViolation(final DocumentMatchCondition condition, final byte[] binary, final char[] text) throws IndexException {
        return this.detectViolation(condition, binary, (CharSequence)new CharArrayCharSequence(text));
    }
    
    @Override
    public DocumentConditionViolation detectViolation(final DocumentMatchCondition condition, final byte[] binary, final CharSequence text) throws IndexException {
        try {
            if (DocumentIndexMatcher._logger.isLoggable(Level.FINE)) {
                this._stopwatch.start();
            }
            final CharSequence normalizedText = ContentNormalizer.normalizeStatelessly(text);
            if (normalizedText == null || normalizedText.length() <= this._minNormalizedSize) {
                return this.detectViolation(condition, binary);
            }
            if (condition.isExact()) {
                return this._index.findExactDocMatches(normalizedText);
            }
            if (DocumentIndexMatcher._logger.isLoggable(Level.FINE)) {
                DocumentIndexMatcher._logger.fine("Document index search took " + this._stopwatch.stop().getLastTime() + " milliseconds.");
            }
            return this._index.findPartialDocMatches(normalizedText, condition.similarity());
        }
        catch (OutOfMemoryError e) {
            throw new IndexException(IndexError.INDEX_NOT_ENOUGH_RAM_TO_DETECT, e);
        }
    }
    
    @Override
    public ProfileIndexDescriptor getDescriptor() {
        return this._indexDescriptor;
    }
    
    @Override
    public void unload() throws IndexException {
        try {
            if (this._index instanceof Disposable) {
                ((Disposable)this._index).dispose();
            }
        }
        catch (IndexException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new IndexException(IndexError.INDEX_UNLOAD_ERROR, new Object[] { this._indexDescriptor.profile().name(), String.valueOf(this._indexDescriptor.version()) });
        }
    }
    
    static {
        _logger = Logger.getLogger(DocumentIndexMatcher.class.getName());
    }
}
