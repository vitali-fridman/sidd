// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.communication.data.DatabaseIndexDescriptorMarshallable;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.communication.data.ProfileDescriptorMarshallable;
import com.vontu.monitor.api.profile.ProfileDescriptor;
import com.vontu.communication.data.PayloadMarshallable;
import com.vontu.util.collection.Converter;

public class DescriptorConverter implements Converter<IndexDescriptor, PayloadMarshallable>
{
    private static ProfileDescriptorMarshallable createProfileDescriptorMarshallable(final ProfileDescriptor source) {
        final ProfileDescriptorMarshallable marshallable = new ProfileDescriptorMarshallable();
        marshallable.setProfileID(source.profileId());
        marshallable.setProfileName(source.name());
        return marshallable;
    }
    
    public IndexDescriptorMarshallable convert(final IndexDescriptor source) {
        IndexDescriptorMarshallable marshallable;
        if (source instanceof DatabaseIndexDescriptor) {
            marshallable = (IndexDescriptorMarshallable)new DatabaseIndexDescriptorMarshallable();
            ((DatabaseIndexDescriptorMarshallable)marshallable).setCryptoKeyAlias(((DatabaseIndexDescriptor)source).getCryptoKeyAlias());
        }
        else {
            marshallable = new IndexDescriptorMarshallable();
        }
        marshallable.setIndexId(source.getIndexedInfoSource().getIndexedInfoSourceID());
        marshallable.setIndexVersion(source.getIndexedInfoSource().getVersion());
        marshallable.setProfile(createProfileDescriptorMarshallable(source.profile()));
        marshallable.setBaseFileName(source.getBaseFileName());
        marshallable.setEndpointBaseFileName(source.getEndpointBaseFileName());
        final int numberOfSubIndexes = source.getIndexedInfoSource().getNumberOfSubIndexes();
        marshallable.setNumberOfFiles((numberOfSubIndexes == 0) ? 1 : numberOfSubIndexes);
        marshallable.setFileSize((long)source.getIndexedInfoSource().getFileSize());
        return marshallable;
    }
}
