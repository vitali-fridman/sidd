// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.ProfileDescriptorMarshallable;
import com.vontu.profileindex.ProtectedProfileDescriptor;
import com.vontu.profileindex.FileInputStreamFactory;
import java.util.ArrayList;
import com.vontu.profiles.common.FilenameCollection;
import java.io.File;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.profileindex.InputStreamFactory;
import java.util.Collection;
import com.vontu.profileindex.ProfileIndexDescriptor;

class FileIndexDescriptor implements ProfileIndexDescriptor
{
    private static final int KB = 1024;
    private final Collection<InputStreamFactory> _streamFactories;
    private final IndexDescriptorMarshallable _indexDescriptor;
    
    FileIndexDescriptor(final IndexDescriptorMarshallable indexDescriptor, final File indexFolder) {
        this._indexDescriptor = indexDescriptor;
        this._streamFactories = createStreamFactories(FilenameCollection.forIndex(indexDescriptor), indexFolder);
    }
    
    private static Collection<InputStreamFactory> createStreamFactories(final FilenameCollection fileNames, final File localIndexFolder) {
        final Collection<InputStreamFactory> factories = new ArrayList<InputStreamFactory>(fileNames.size());
        for (final File file : fileNames.toFiles(localIndexFolder)) {
            factories.add((InputStreamFactory)new FileInputStreamFactory(file.getAbsolutePath()));
        }
        return factories;
    }
    
    public ProtectedProfileDescriptor profile() {
        return (ProtectedProfileDescriptor)new ProfileDescriptorAdapter(this._indexDescriptor.getProfile());
    }
    
    public long size() {
        return this._indexDescriptor.getFileSize() * 1024L * this._indexDescriptor.getNumberOfFiles();
    }
    
    public InputStreamFactory[] streams() {
        return this._streamFactories.toArray(new InputStreamFactory[this._streamFactories.size()]);
    }
    
    public int version() {
        return this._indexDescriptor.getIndexVersion();
    }
    
    private static class ProfileDescriptorAdapter implements ProtectedProfileDescriptor
    {
        private final ProfileDescriptorMarshallable _adpatee;
        
        ProfileDescriptorAdapter(final ProfileDescriptorMarshallable adaptee) {
            this._adpatee = adaptee;
        }
        
        public String profileId() {
            return this._adpatee.getProfileID();
        }
        
        public String name() {
            return this._adpatee.getProfileName();
        }
    }
}
