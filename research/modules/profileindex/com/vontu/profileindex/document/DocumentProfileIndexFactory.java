// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document;

import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.document.docindex.DocIndexFactory;
import com.vontu.profileindex.ProfileIndex;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindex.ProfileIndexFactory;

public class DocumentProfileIndexFactory implements ProfileIndexFactory
{
    private SettingProvider _settings;
    
    @Override
    public ProfileIndex loadInstance(final ProfileIndexDescriptor descriptor) throws IndexException {
        final DocIndexFactory indexFactory = new DocIndexFactory(this._settings, descriptor);
        return new DocumentIndexMatcher(indexFactory.loadInstance(), descriptor, this._settings);
    }
    
    public DocumentProfileIndexFactory(final SettingProvider settingProvider) {
        this._settings = settingProvider;
    }
}
