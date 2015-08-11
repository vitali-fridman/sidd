// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.OutputStream;
import com.vontu.ramindex.util.IdxRdxMetadata;
import java.io.DataOutputStream;
import java.io.File;

public class IdxHeaderHandler
{
    private final File _idxFile;
    private boolean _wroteHeader;
    private static final int BUFFER_SIZE = 1048576;
    
    public IdxHeaderHandler(final File idxFile) {
        this._wroteHeader = false;
        this._idxFile = idxFile;
    }
    
    public void copyIdxFooterToRdxHeader(final DataOutputStream rdxOutput) throws IOException, InterruptedException {
        if (this._wroteHeader) {
            throw new IllegalStateException("Cannot write the header twice. ");
        }
        try {
            IdxRdxMetadata.copyIdxFooterToRdxHeader(rdxOutput, this._idxFile);
        }
        finally {
            this._wroteHeader = true;
        }
    }
}
