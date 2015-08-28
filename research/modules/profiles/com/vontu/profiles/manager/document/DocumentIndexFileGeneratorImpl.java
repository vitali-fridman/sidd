// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.profiles.common.DocIndexerUtil;
import java.io.File;

public class DocumentIndexFileGeneratorImpl
{
    private String _indexFolder;
    private int _indexVersion;
    private int _docSourceId;
    private File _whiteListedFile;
    
    public DocumentIndexFileGeneratorImpl(final String indexFolder, final int docSourceId, final int indexVersion) {
        this._indexFolder = indexFolder;
        this._indexVersion = indexVersion;
        this._docSourceId = docSourceId;
        this._whiteListedFile = this.getWhiteListedFile();
    }
    
    public int getDocSourceId() {
        return this._docSourceId;
    }
    
    public int getIndexVersion() {
        return this._indexVersion;
    }
    
    public String getIndexFolder() {
        return this._indexFolder;
    }
    
    public int getPrevVersion() {
        return this._indexVersion - 1;
    }
    
    public File getIndexFile(final int indexVersion, final boolean incremental) {
        final String fileName = DocIndexerUtil.getIndexFileName(this._docSourceId, indexVersion) + (incremental ? ".i" : "");
        return new File(this._indexFolder, fileName);
    }
    
    public File getIndexFile() {
        return this.getIndexFile(this._indexVersion, false);
    }
    
    public File getPrevIndexFile() {
        return this.getIndexFile(this.getPrevVersion(), false);
    }
    
    public File getEndpointIndexFile() {
        final String fileName = DocIndexerUtil.getEndpointIndexFileName(this._docSourceId, this._indexVersion);
        return new File(this._indexFolder, fileName);
    }
    
    public File getPrevEndpointIndexFile() {
        final String fileName = DocIndexerUtil.getEndpointIndexFileName(this._docSourceId, this.getPrevVersion());
        return new File(this._indexFolder, fileName);
    }
    
    public File getWhiteListedFileForDocSource() {
        return this._whiteListedFile;
    }
    
    private File getWhiteListedFile() {
        final String wlPrefix = System.getProperty("com.vontu.documents.dir", this._indexFolder) + File.separator + "whitelisted" + File.separator + "Whitelisted";
        final File wlFileForDocSource = new File(wlPrefix + "." + this._docSourceId + ".txt");
        return wlFileForDocSource;
    }
    
    public File getWhiteListedFileCommon() {
        final String wlPrefix = System.getProperty("com.vontu.documents.dir", this._indexFolder) + File.separator + "whitelisted" + File.separator + "Whitelisted";
        return new File(wlPrefix + ".txt");
    }
}
