// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.Set;
import java.io.IOException;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.ramindex.util.IdxRdxMetadata;
import java.util.HashMap;
import com.vontu.nlp.lexer.asiansupport.LexerAsianTokenList;
import com.vontu.profileindex.ProfileIndexDescriptor;
import java.util.Map;
import com.vontu.nlp.lexer.asiansupport.AsianTokenMergedLookup;

public class AsianTokenListManager
{
    private AsianTokenMergedLookup _mergeList;
    private final Map<ProfileIndexDescriptor, LexerAsianTokenList> _tokenListLookup;
    
    public AsianTokenListManager() {
        this._mergeList = new AsianTokenMergedLookup();
        this._tokenListLookup = new HashMap<ProfileIndexDescriptor, LexerAsianTokenList>();
    }
    
    public synchronized void load(final ProfileIndexDescriptor descriptor) throws IOException {
        if (!(descriptor instanceof DatabaseProfileIndexDescriptor)) {
            return;
        }
        final InputStreamFactory[] streams = descriptor.streams();
        final IdxRdxMetadata metadata = IdxRdxMetadata.loadRdxHeaderFromStream(streams[0].getInputStream());
        if (metadata == null) {
            return;
        }
        final LexerAsianTokenList list = metadata.getAsianTokenList();
        if (list == null || list.isEmpty()) {
            return;
        }
        this._tokenListLookup.put(descriptor, list);
        this.addAsianTokenList(list);
    }
    
    void addAsianTokenList(final LexerAsianTokenList list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        this._mergeList.addLookupList(list.getAsianTokenLookupList());
    }
    
    public AsianTokenMergedLookup getAsianTokenMergedLookup() {
        return this._mergeList;
    }
    
    public void unload(final ProfileIndexDescriptor descriptor) {
        final LexerAsianTokenList list = this._tokenListLookup.get(descriptor);
        if (list != null) {
            this.removeAsianTokenList(list);
            this._tokenListLookup.remove(descriptor);
        }
    }
    
    void removeAsianTokenList(final LexerAsianTokenList list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        this._mergeList.removeLookupList(list.getAsianTokenLookupList());
    }
    
    public Set<Integer> getMergeLengths(final char unhashedCharacter) {
        return (Set<Integer>)this._mergeList.getLengths(unhashedCharacter);
    }
}
