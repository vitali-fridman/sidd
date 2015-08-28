// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.cracker.ExtractionStatistics;

public class DocExtractionStats implements ExtractionStatistics
{
    private int _encrypted;
    private int _timedOut;
    private int _tooLong;
    private int _unprocessed;
    
    public void incrementEncrypted() {
        ++this._encrypted;
    }
    
    public void incrementTimedOut() {
        ++this._timedOut;
    }
    
    public void incrementTooLong() {
        ++this._tooLong;
    }
    
    public void incrementUnprocessed() {
        ++this._unprocessed;
    }
    
    public int getEncrypted() {
        return this._encrypted;
    }
    
    public int getTimedOut() {
        return this._timedOut;
    }
    
    public int getTooLong() {
        return this._tooLong;
    }
    
    public int getUnprocessed() {
        return this._unprocessed;
    }
}
