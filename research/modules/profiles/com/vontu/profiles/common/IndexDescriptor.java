// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.vontu.model.data.NullValue;
import com.vontu.model.data.InfoSource;
import com.vontu.monitor.api.profile.ProfileDescriptor;
import com.vontu.model.data.IndexedInfoSource;

public class IndexDescriptor
{
    private final IndexedInfoSource _indexedInfoSource;
    private final ProfileDescriptor _profileDescriptor;
    private final String _baseFileName;
    private String profileId;
    private int indexedInfoSourceVersion;
    private final String endpointBaseFileName;
    
    public IndexDescriptor(final IndexedInfoSource indexedInfoSource, final IndexFilenameTemplate filenameTemplate) {
        this._indexedInfoSource = indexedInfoSource;
        this._profileDescriptor = (ProfileDescriptor)new InfoSourceAdapter(this._indexedInfoSource.getInfoSource());
        this._baseFileName = filenameTemplate.createBaseFileName(this._profileDescriptor.profileId(), indexedInfoSource.getVersion());
        this.endpointBaseFileName = filenameTemplate.createEndpointBaseFileName(this._profileDescriptor.profileId(), indexedInfoSource.getVersion());
        this.profileId = this._profileDescriptor.profileId();
        this.indexedInfoSourceVersion = indexedInfoSource.getVersion();
    }
    
    public IndexedInfoSource getIndexedInfoSource() {
        return this._indexedInfoSource;
    }
    
    public int getIndexedInfoSourceID() {
        return this._indexedInfoSource.getIndexedInfoSourceID();
    }
    
    public InfoSource getInfoSource() {
        return this._indexedInfoSource.getInfoSource();
    }
    
    public int getNumberOfSubIndexes() {
        return this._indexedInfoSource.getNumberOfSubIndexes();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof IndexDescriptor && !NullValue.isNull(this.getIndexedInfoSourceID()) && this.getIndexedInfoSourceID() == ((IndexDescriptor)obj).getIndexedInfoSourceID());
    }
    
    @Override
    public int hashCode() {
        return IndexedInfoSource.class.hashCode() + this.getIndexedInfoSourceID();
    }
    
    public String getBaseFileName() {
        return this._baseFileName;
    }
    
    public String getEndpointBaseFileName() {
        return this.endpointBaseFileName;
    }
    
    public FilenameCollection getFileNames() {
        final Collection<String> indexfilenames = new ArrayList<String>();
        if (this.getNumberOfSubIndexes() > 1) {
            final String[] fileNames = new String[this.getNumberOfSubIndexes()];
            for (int i = 0; i < fileNames.length; ++i) {
                fileNames[i] = this.getFileName(i);
            }
            indexfilenames.addAll(Arrays.asList(fileNames));
        }
        else {
            indexfilenames.add(this.getBaseFileName());
            if (this._indexedInfoSource.getInfoSource().getType() == 2) {
                indexfilenames.add(this.getEndpointBaseFileName());
            }
        }
        return new FilenameCollection(indexfilenames);
    }
    
    private String getFileName(final int i) {
        return this.getBaseFileName() + '.' + i;
    }
    
    public ProfileDescriptor profile() {
        return this._profileDescriptor;
    }
    
    public int version() {
        return this._indexedInfoSource.getVersion();
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("[{0}:{1} version {2}]", this.getIndexedInfoSourceID(), this.profile().name(), this.version());
    }
}
