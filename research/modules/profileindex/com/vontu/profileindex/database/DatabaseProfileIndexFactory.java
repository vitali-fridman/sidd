// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.ramindex.RamIndexFactory;
import com.vontu.profileindex.ProfileIndex;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.keystorehouse.KeyStorehouse;
import com.vontu.util.config.SettingProvider;
import com.vontu.profileindex.ProfileIndexFactory;

public class DatabaseProfileIndexFactory implements ProfileIndexFactory
{
    private final SettingProvider _settings;
    private final KeyStorehouse _keyStorehouse;
    
    public DatabaseProfileIndexFactory(final SettingProvider settingProvider, final KeyStorehouse keyStorehouse) {
        this._settings = settingProvider;
        this._keyStorehouse = keyStorehouse;
    }
    
    @Override
    public ProfileIndex loadInstance(final ProfileIndexDescriptor descriptor) throws IndexException, IllegalArgumentException {
        if (!(descriptor instanceof DatabaseProfileIndexDescriptor)) {
            throw new IllegalArgumentException("The specified descriptor isn't a DatabaseProfileIndexDescriptor.");
        }
        final RamIndexFactory indexFactory = new RamIndexFactory(this._settings, descriptor);
        return new DatabaseInfoMatcher(indexFactory.loadInstance(), (DatabaseProfileIndexDescriptor)descriptor, this._keyStorehouse, this._settings);
    }
}
