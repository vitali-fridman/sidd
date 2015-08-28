// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.directorycrawler.VontuFileException;
import java.util.Date;
import com.vontu.cracker.DocumentFormat;
import com.vontu.directorycrawler.VontuFile;
import com.vontu.model.data.DocSourceDoc;

public class DocSourceDocWrapper
{
    private final DocSourceDoc _docSourceDoc;
    private VontuFile _file;
    private int _status;
    private boolean _crackable;
    private DocumentFormat _fileType;
    static final int DOCUMENT_REMOVED = 0;
    static final int DOCUMENT_UNCHANGED = 1;
    static final int DOCUMENT_CHANGED = 2;
    static final int DOCUMENT_NEW = 3;
    
    public DocSourceDocWrapper(final DocSourceDoc docSourceDoc, final int status) {
        this._docSourceDoc = docSourceDoc;
        this._status = status;
        this._crackable = false;
    }
    
    public DocSourceDoc getDocSourceDoc() {
        return this._docSourceDoc;
    }
    
    public VontuFile getFile() {
        return this._file;
    }
    
    public int getStatus() {
        return this._status;
    }
    
    public void setVontuFile(final VontuFile file) {
        this._file = file;
    }
    
    public void setStatus(final int status) {
        this._status = status;
    }
    
    public boolean isCrackable() {
        return this._crackable;
    }
    
    public void setCrackable(final boolean crackable) {
        this._crackable = crackable;
    }
    
    public DocumentFormat getFileType() {
        return this._fileType;
    }
    
    public void setFileType(final DocumentFormat fileType) {
        this._fileType = fileType;
    }
    
    public void synchDocumentMetaInfo() throws VontuFileException, InterruptedException {
        this._docSourceDoc.setFileSize((int)this._file.length());
        this._docSourceDoc.setLastModified(new Date(this._file.lastModified()));
    }
    
    public static boolean isDocumentModified(final DocSourceDocWrapper docWrapper) throws VontuFileException, InterruptedException {
        final DocSourceDoc document = docWrapper.getDocSourceDoc();
        final VontuFile file = docWrapper.getFile();
        if (document == null || file == null) {
            throw new IllegalStateException("Cannot compare the files because either it's been removed, or it's a newly added file.");
        }
        return file.lastModified() != document.getLastModified().getTime();
    }
}
