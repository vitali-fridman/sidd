// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;

public class IncidentActionDetail implements Serializable
{
    private String _i18nKeyOrString;
    private boolean _isInternationalized;
    private ArrayList<IncidentActionDetailParameter> _parameters;
    private static final long serialVersionUID = -6305345831973066842L;
    
    public IncidentActionDetail(final String i18nKeyOrString, final boolean isInternationalized) {
        this.setI18nKeyOrString(i18nKeyOrString);
        this._isInternationalized = isInternationalized;
    }
    
    public IncidentActionDetail(final String i18nKeyOrString, final IncidentActionDetailParameter[] parameters) {
        this.setI18nKeyOrString(i18nKeyOrString);
        this.setParameters(parameters);
        this._isInternationalized = true;
    }
    
    public String getI18nKeyOrString() {
        if (this._i18nKeyOrString == null) {
            return "";
        }
        return this._i18nKeyOrString;
    }
    
    public void setI18nKeyOrString(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("Invalid i18n key or string.");
        }
        this._i18nKeyOrString = str;
    }
    
    public boolean getIsI18n() {
        return this._isInternationalized;
    }
    
    public void setIsI18n(final boolean isInternationalized) {
        this._isInternationalized = isInternationalized;
    }
    
    public IncidentActionDetailParameter[] getParameters() {
        if (this._parameters == null) {
            return null;
        }
        final IncidentActionDetailParameter[] parameters = new IncidentActionDetailParameter[this._parameters.size()];
        return this._parameters.toArray(parameters);
    }
    
    public void setParameters(final IncidentActionDetailParameter[] parameters) {
        if (parameters != null) {
            this._parameters = new ArrayList<IncidentActionDetailParameter>();
            for (final IncidentActionDetailParameter parameter : parameters) {
                this._parameters.add(parameter);
            }
        }
    }
    
    public void appendParameter(final String parameter, final boolean isI18nKey) {
        if (this._parameters == null) {
            this._parameters = new ArrayList<IncidentActionDetailParameter>();
        }
        this._parameters.add(new IncidentActionDetailParameter(parameter, isI18nKey));
    }
    
    public static IncidentActionDetail getEmptyDetail() {
        final IncidentActionDetail emptyDetail = new IncidentActionDetail("", false);
        return emptyDetail;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null || !(object instanceof IncidentActionDetail)) {
            return false;
        }
        final IncidentActionDetail incidentActionDetail = (IncidentActionDetail)object;
        if (!this._i18nKeyOrString.equals(incidentActionDetail.getI18nKeyOrString()) || this._isInternationalized != incidentActionDetail.getIsI18n()) {
            return false;
        }
        if (this.getParameters() == null && incidentActionDetail.getParameters() == null) {
            return true;
        }
        if (this.getParameters() == null || incidentActionDetail.getParameters() == null) {
            return false;
        }
        for (final IncidentActionDetailParameter incidentActionDetailParameter1 : this._parameters) {
            boolean matchFound = false;
            for (final IncidentActionDetailParameter incidentActionDetailParameter2 : incidentActionDetail.getParameters()) {
                if (incidentActionDetailParameter1.equals(incidentActionDetailParameter2)) {
                    matchFound = true;
                }
            }
            if (!matchFound) {
                return false;
            }
        }
        return true;
    }
}
