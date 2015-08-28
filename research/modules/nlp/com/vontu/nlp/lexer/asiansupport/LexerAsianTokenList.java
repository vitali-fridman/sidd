// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.asiansupport;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LexerAsianTokenList
{
    private AsianTokenLookupList _lookupList;
    
    public LexerAsianTokenList(final AsianTokenLookupList list) {
        this._lookupList = list;
    }
    
    public LexerAsianTokenList(final InputStream inputStream) throws IOException {
        this.loadFromStream(inputStream);
    }
    
    public void loadFromStream(final InputStream inputStream) throws IOException {
        this._lookupList = AsianTokenIdxRdxParser.readLookupList(inputStream);
    }
    
    public void writeToStream(final OutputStream out) throws IOException {
        AsianTokenIdxRdxParser.writeLookupList(out, this._lookupList);
    }
    
    public void setAsianTokenLookupList(final AsianTokenLookupList list) {
        this._lookupList = list;
    }
    
    public AsianTokenLookupList getAsianTokenLookupList() {
        return this._lookupList;
    }
    
    public boolean isEmpty() {
        return this._lookupList.getSize() == 0;
    }
    
    public static void skipInStream(final InputStream in) throws IOException {
        AsianTokenIdxRdxParser.skipAsianTokenLookupList(in);
    }
}
