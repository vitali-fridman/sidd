// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.text.MessageFormat;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.profileindex.IndexException;
import java.util.logging.Level;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.profileindex.ProfileIndex;
import com.vontu.util.concurrent.ConcurrentReaderHashMap;
import java.io.File;
import com.vontu.profileindex.tokenizer.TokenizedContentFactory;
import com.vontu.profileindex.ProfileIndexFactory;
import java.util.Map;
import java.util.logging.Logger;

public class LoadedIndexMap
{
    private static final Logger _logger;
    private final Map<String, DescribedProfileIndex> _loadedIndices;
    private final ProfileIndexFactory _indexFactory;
    private final String _profileType;
    private final TokenizedContentFactory _tokenizerFactory;
    private FileIndexDescriptorFactory _descriptorFactory;
    
    public LoadedIndexMap(final ProfileIndexFactory indexFactory, final String profileType, final File indexFolder, final TokenizedContentFactory tokenizerFactory) {
        this(indexFactory, profileType, tokenizerFactory, new FileIndexDescriptorFactory(indexFolder));
    }
    
    LoadedIndexMap(final ProfileIndexFactory indexFactory, final String profileType, final TokenizedContentFactory tokenizerFactory, final FileIndexDescriptorFactory descriptorFactory) {
        this._loadedIndices = (Map<String, DescribedProfileIndex>)new ConcurrentReaderHashMap();
        this._indexFactory = indexFactory;
        this._profileType = profileType;
        this._tokenizerFactory = tokenizerFactory;
        this._descriptorFactory = descriptorFactory;
    }
    
    public ProfileIndex getIndex(final String profileId) {
        final DescribedProfileIndex describedIndex = this.getDescribedIndex(profileId);
        return (describedIndex == null) ? null : describedIndex.index();
    }
    
    private DescribedProfileIndex getDescribedIndex(final String profileId) {
        return this._loadedIndices.get(profileId);
    }
    
    public IndexDescriptorMarshallable getIndexDescriptor(final String profileId) {
        final DescribedProfileIndex describedIndex = this.getDescribedIndex(profileId);
        return (describedIndex == null) ? null : describedIndex.descriptor();
    }
    
    public boolean isLoaded(final String profileId) {
        return this._loadedIndices.containsKey(profileId);
    }
    
    public void load(final String profileId, final IndexDescriptorMarshallable descriptor) throws IndexException, IllegalStateException {
        if (this._loadedIndices.containsKey(profileId)) {
            throw new IllegalStateException("Index for " + this._profileType + " profile ID \"" + profileId + "\" is already loaded.");
        }
        this._loadedIndices.put(profileId, this.createDescribedIndex(descriptor));
        if (LoadedIndexMap._logger.isLoggable(Level.FINE)) {
            LoadedIndexMap._logger.fine("Loaded " + this._profileType + " profile index map updated. " + this.toString());
        }
    }
    
    private DescribedProfileIndex createDescribedIndex(final IndexDescriptorMarshallable descriptor) throws IndexException {
        final ProfileIndexDescriptor fileIndexDescriptor = this._descriptorFactory.newInstance(descriptor);
        final ProfileIndex profileIndex = this._indexFactory.loadInstance(fileIndexDescriptor);
        this.loadIntoTokenizerFactory(fileIndexDescriptor);
        return new DescribedProfileIndex(profileIndex, descriptor, fileIndexDescriptor);
    }
    
    public boolean unload(final String profileId) throws IndexException {
        final DescribedProfileIndex describedIndex = this.getDescribedIndex(profileId);
        if (describedIndex == null) {
            return false;
        }
        this._loadedIndices.remove(profileId);
        if (LoadedIndexMap._logger.isLoggable(Level.FINE)) {
            LoadedIndexMap._logger.fine("Loaded " + this._profileType + " profile index map updated. " + this.toString());
        }
        this.unloadInTokenizerFactory(describedIndex);
        describedIndex.unload();
        return true;
    }
    
    public void unloadAll() {
        for (final DescribedProfileIndex describedIndex : this._loadedIndices.values()) {
            try {
                this.unloadInTokenizerFactory(describedIndex);
                describedIndex.unload();
            }
            catch (IndexException e) {
                LoadedIndexMap._logger.log(Level.SEVERE, MessageFormat.format("Failed to unload {3} profile index [{0}:{1} version {2}]", IndexLogMessage.prependIndexDescriptorArgs(describedIndex.descriptor(), this._profileType)), (Throwable)e);
            }
        }
        this._loadedIndices.clear();
        LoadedIndexMap._logger.fine("All " + this._profileType + " profiles are unloaded.");
    }
    
    private void unloadInTokenizerFactory(final DescribedProfileIndex index) {
        if (this._tokenizerFactory != null) {
            this._tokenizerFactory.unloadDescriptor(index.indexDescriptor());
        }
    }
    
    private void loadIntoTokenizerFactory(final ProfileIndexDescriptor index) {
        if (this._tokenizerFactory != null) {
            this._tokenizerFactory.loadDescriptor(index);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder entries = new StringBuilder();
        for (final DescribedProfileIndex describedIndex : this._loadedIndices.values()) {
            if (entries.length() > 0) {
                entries.append(", ");
            }
            entries.append(describedIndex.descriptor());
        }
        final int size = this._loadedIndices.size();
        return (size > 0) ? ("Loaded profiles count=" + size + ": " + (Object)entries + '.') : "No entires exist.";
    }
    
    static {
        _logger = Logger.getLogger(LoadedIndexMap.class.getName());
    }
    
    private static class DescribedProfileIndex
    {
        private final ProfileIndex _index;
        private final IndexDescriptorMarshallable _descriptor;
        private final ProfileIndexDescriptor _indexDescriptor;
        
        DescribedProfileIndex(final ProfileIndex index, final IndexDescriptorMarshallable descriptor, final ProfileIndexDescriptor indexDescriptor) {
            this._index = index;
            this._descriptor = descriptor;
            this._indexDescriptor = indexDescriptor;
        }
        
        ProfileIndex index() {
            return this._index;
        }
        
        IndexDescriptorMarshallable descriptor() {
            return this._descriptor;
        }
        
        private void unload() throws IndexException {
            this.index().unload();
        }
        
        ProfileIndexDescriptor indexDescriptor() {
            return this._indexDescriptor;
        }
    }
}
