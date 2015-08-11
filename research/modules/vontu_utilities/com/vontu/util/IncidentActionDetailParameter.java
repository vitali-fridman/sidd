// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.Serializable;

public class IncidentActionDetailParameter implements Serializable
{
    private String _parameter;
    private boolean _isI18nKey;
    private static final long serialVersionUID = 5604068089859245766L;
    
    public IncidentActionDetailParameter(final String parameter, final boolean isI18nKey) {
        this._parameter = parameter;
        this._isI18nKey = isI18nKey;
    }
    
    public String getParameter() {
        return this._parameter;
    }
    
    public boolean getIsI18nKey() {
        return this._isI18nKey;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null || !(object instanceof IncidentActionDetailParameter)) {
            return false;
        }
        final IncidentActionDetailParameter incidentActionDetailParameter = (IncidentActionDetailParameter)object;
        return this._parameter.equals(incidentActionDetailParameter.getParameter()) && this._isI18nKey == incidentActionDetailParameter.getIsI18nKey();
    }
}
