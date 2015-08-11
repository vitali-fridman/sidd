// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;

final class IndexRangeWriter
{
    private final Configuration _configuration;
    private final AllCommonTermIndex _allCommonTerms;
    private final IndexHashedByTermAndRow _commonTermIndex;
    private final IndexHashedByTermInRange _uncommonTermIndex;
    private final File _rangeFilePathPrefix;
    
    IndexRangeWriter(final Configuration configuration, final File workFolder, final File ramIndexFile, final AllCommonTermIndex allCommonTerms, final IndexHashedByTermAndRow commonTermIndex, final IndexHashedByTermInRange uncomonTermIndex, final int rangeNumber) {
        this._configuration = configuration;
        final String rangeFileNamePrefix = ramIndexFile.getName() + "." + rangeNumber + ".";
        this._rangeFilePathPrefix = new File(workFolder, rangeFileNamePrefix);
        this._allCommonTerms = allCommonTerms;
        this._commonTermIndex = commonTermIndex;
        this._uncommonTermIndex = uncomonTermIndex;
    }
    
    void writeIndex() throws IOException {
        DataOutputStream indexFile = null;
        final int fileStreamOutputBufferSize = this._configuration.getFilestreamOutputBufferSize();
        try {
            String indexFilePath = this._rangeFilePathPrefix.getPath() + "act";
            indexFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFilePath), fileStreamOutputBufferSize));
            this._allCommonTerms.writeToFile(indexFile);
            indexFile.close();
            indexFile = null;
            indexFilePath = this._rangeFilePathPrefix.getPath() + "cti";
            indexFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFilePath), fileStreamOutputBufferSize));
            this._commonTermIndex.writeToFile(indexFile);
            indexFile.close();
            indexFile = null;
            indexFilePath = this._rangeFilePathPrefix.getPath() + "uti";
            indexFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFilePath), fileStreamOutputBufferSize));
            this._uncommonTermIndex.writeToFile(indexFile);
            indexFile.close();
            indexFile = null;
        }
        finally {
            if (indexFile != null) {
                indexFile.close();
            }
        }
    }
}
