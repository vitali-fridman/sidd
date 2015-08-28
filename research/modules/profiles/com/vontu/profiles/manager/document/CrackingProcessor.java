// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.cracker.DocumentFormatUtil;
import com.vontu.cracker.DocumentFormat;
import com.vontu.cracker.DocumentType;
import com.vontu.cracker.MessageTimeOut;
import com.vontu.util.TimeService;
import com.vontu.cracker.EncodedString;
import com.vontu.util.config.SettingReader;
import com.vontu.cracker.TimeOutSettings;

public class CrackingProcessor
{
    public static final String CRACKING_TIMEOUT = "com.vontu.profiles.documents.crackingTimeout";
    public static final int CRACKING_TIMEOUT_DEFAULT = 60000;
    private DocumentExtractor _documentExtractor;
    private int _crackerTimeout;
    private TimeOutSettings _timeOutSettings;
    
    public CrackingProcessor(final DocumentExtractor documentExtractor, final SettingReader settings) {
        this._documentExtractor = documentExtractor;
        this._timeOutSettings = new TimeOutSettings(settings);
        this._crackerTimeout = 60000;
    }
    
    public EncodedString crackDocument(final byte[] content, final String encodingName, final String docName) throws InterruptedException, DocumentExtractionException {
        final MessageTimeOut timeOut = new MessageTimeOut(new TimeService(), this._timeOutSettings, (long)this._crackerTimeout);
        final DocumentType docType = this.getDocumentTypeForFile(content);
        return this._documentExtractor.getTextForFile(content, encodingName, docName, timeOut, docType);
    }
    
    public DocumentFormat getFormatForFile(final byte[] content) throws DocumentExtractionException {
        return DocumentFormatUtil.getDocumentFormatFromId(this.getDocumentTypeForFile(content).getTypeName());
    }
    
    private DocumentType getDocumentTypeForFile(final byte[] content) throws DocumentExtractionException {
        final MessageTimeOut timeOut = new MessageTimeOut(new TimeService(), this._timeOutSettings, (long)this._crackerTimeout);
        return this._documentExtractor.getTypeForFile(content, timeOut);
    }
}
