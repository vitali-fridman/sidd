// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.model.data.DocSource;
import com.vontu.profiles.monitor.document.DocumentIndexFilenameTemplate;
import com.vontu.model.data.DirectoryConnectionSource;
import com.vontu.model.data.DataSource;
import com.vontu.profiles.monitor.database.DatabaseIndexFilenameTemplate;
import java.util.Collections;
import java.util.Arrays;
import com.vontu.communication.dataflow.UnstructuredDataType;
import com.vontu.model.data.InfoSource;
import java.util.List;

public enum ProfileType
{
    DATABASE("database", 1, (IndexFilenameTemplate)new DatabaseIndexFilenameTemplate(), UnstructuredDataType.EDM_INDEX, (Class<? extends InfoSource>[])new Class[] { DataSource.class, DirectoryConnectionSource.class }), 
    DOCUMENT("document", 2, (IndexFilenameTemplate)new DocumentIndexFilenameTemplate(), UnstructuredDataType.IDM_INDEX, (Class<? extends InfoSource>[])new Class[] { DocSource.class });
    
    private final String _value;
    private final int _infoSourceType;
    private final List<Class<? extends InfoSource>> _infoSourceInterfaces;
    private final IndexFilenameTemplate _indexFilenameTemplate;
    private final UnstructuredDataType _dataFlowType;
    
    private ProfileType(final String value, final int infoSourceType, final IndexFilenameTemplate filenameTemplate, final UnstructuredDataType dataFlowType, final Class<? extends InfoSource>[] infoSourceInterfaces) {
        this._value = value;
        this._infoSourceType = infoSourceType;
        this._infoSourceInterfaces = Arrays.asList(infoSourceInterfaces);
        this._indexFilenameTemplate = filenameTemplate;
        this._dataFlowType = dataFlowType;
    }
    
    public List<Class<? extends InfoSource>> infoSourceInterfaces() {
        return Collections.unmodifiableList((List<? extends Class<? extends InfoSource>>)this._infoSourceInterfaces);
    }
    
    public int infoSourceType() {
        return this._infoSourceType;
    }
    
    public String value() {
        return this._value;
    }
    
    public IndexFilenameTemplate indexFilenameTemplate() {
        return this._indexFilenameTemplate;
    }
    
    public UnstructuredDataType dataFlowType() {
        return this._dataFlowType;
    }
}
