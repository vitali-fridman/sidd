// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import java.util.Iterator;
import com.vontu.detection.output.DocumentMatch;
import java.util.Collections;
import com.vontu.detection.output.DocumentConditionViolation;
import cern.colt.list.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.function.IntObjectProcedure;

class HitCheckProcedure implements IntObjectProcedure
{
    private final DocumentIndex index;
    private final int threshold;
    private final AbstractIntIntMap fingerprintMap;
    private final CharSequence messageText;
    private List<DocHit> hits;
    
    HitCheckProcedure(final DocumentIndex index, final int threshold, final AbstractIntIntMap fingerprintMap, final CharSequence messageText) {
        this.hits = new ArrayList<DocHit>();
        this.index = index;
        this.threshold = threshold;
        this.fingerprintMap = fingerprintMap;
        this.messageText = messageText;
    }
    
    public boolean apply(final int docId, final Object object) {
        final IntArrayList intersectionHashes = (IntArrayList)object;
        final int docFpSize = this.index.lookupFingerprintCount(docId);
        int intersectionSize = intersectionHashes.size();
        int containment = 100 * intersectionSize / docFpSize;
        if (containment >= this.threshold) {
            if (intersectionHashes.size() < this.index.getSnippetCheckThreshold()) {
                for (int i = 0; i < intersectionHashes.size(); ++i) {
                    final int intersectionHash = intersectionHashes.getQuick(i);
                    final byte[] indexSnippet = this.index.getSnippet(docId, intersectionHash);
                    if (indexSnippet == null) {
                        --intersectionSize;
                    }
                    else {
                        final int position = this.fingerprintMap.get(intersectionHash);
                        if (!this.snippetsMatched(indexSnippet, this.messageText, position, this.index.getK())) {
                            --intersectionSize;
                        }
                    }
                }
                containment = 100 * intersectionSize / docFpSize;
                if (containment >= this.threshold) {
                    this.hits.add(new DocHit(docId, containment));
                }
            }
            else {
                this.hits.add(new DocHit(docId, containment));
            }
        }
        return true;
    }
    
    public DocumentConditionViolation getMatches() {
        final DocumentConditionViolationImpl violation = new DocumentConditionViolationImpl();
        final int numMatches = this.hits.size();
        if (numMatches > 0) {
            if (numMatches > 1) {
                Collections.sort(this.hits);
            }
            for (final DocHit dm : this.hits) {
                violation.addMatch((DocumentMatch)new DocumentMatchImpl(dm.getDocId(), dm.getCont()));
            }
            return (DocumentConditionViolation)violation;
        }
        return null;
    }
    
    private boolean snippetsMatched(final byte[] indexSnippet, final CharSequence messageText, final int position, final int k) {
        for (int numBytes = indexSnippet.length / 2, i = 0, j = indexSnippet.length - 1; i < numBytes; ++i, --j) {
            if (indexSnippet[i] != (byte)messageText.charAt(position + i)) {
                return false;
            }
            if (indexSnippet[j] != (byte)messageText.charAt(position + k - 1 - i)) {
                return false;
            }
        }
        return true;
    }
    
    private static class DocHit implements Comparable<DocHit>
    {
        private int docId;
        private int cont;
        
        DocHit(final int docId, final int cont) {
            this.docId = docId;
            this.cont = cont;
        }
        
        int getDocId() {
            return this.docId;
        }
        
        int getCont() {
            return this.cont;
        }
        
        @Override
        public int compareTo(final DocHit other) {
            return other.getCont() - this.cont;
        }
    }
}
