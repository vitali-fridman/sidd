// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.model.data.InfoSource;
import com.vontu.detection.condition.EquivalentProfileDescriptor;

public final class InfoSourceAdapter extends EquivalentProfileDescriptor
{
    private final InfoSource _infoSource;
    
    public InfoSourceAdapter(final InfoSource infoSource) {
        this._infoSource = infoSource;
    }
    
    public String profileId() {
        return String.valueOf(this._infoSource.getInfoSourceID());
    }
    
    public String name() {
        return this._infoSource.getName();
    }
}
