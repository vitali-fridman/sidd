// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import com.vontu.detection.output.DocumentMatch;
import com.vontu.docindex.util.IndexUtil;
import java.util.Iterator;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.AbstractIntObjectMap;
import java.util.Set;
import cern.colt.function.IntObjectProcedure;
import cern.colt.list.IntArrayList;
import com.vontu.docindex.algorithm.HashWithPosition;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import com.vontu.detection.output.DocumentConditionViolation;
import java.io.IOException;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import com.vontu.profileindex.document.docindex.load.DocumentIndexLoader;
import java.util.logging.Level;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.document.docindex.hash.HashGeneratorFactory;
import com.vontu.profileindex.document.docindex.hash.HashGenerator;
import com.vontu.docindex.algorithm.HashSelector;
import com.vontu.util.Stopwatch;
import java.util.logging.Logger;
import com.vontu.profileindex.document.DocIndex;

public class DocIndexImpl implements DocIndex
{
    private static final Logger logger;
    private static final Stopwatch indexLoadindStopWatch;
    private DocumentIndex index;
    private ThreadLocal<HashSelector> hashSelector;
    private ThreadLocal<HashGenerator> digester;
    private final HashGeneratorFactory generatorFactory;
    
    public DocIndexImpl(final Configuration configuration, final InputStreamFactory inputStreamFactory) throws IndexException {
        final String fileName = inputStreamFactory.name();
        try {
            DocIndexImpl.logger.log(Level.INFO, "Loading Document Index from [" + fileName + "]");
            if (DocIndexImpl.logger.isLoggable(Level.FINEST)) {
                DocIndexImpl.indexLoadindStopWatch.start();
            }
            final DocumentIndexLoader loader = new DocumentIndexLoader();
            this.index = loader.load(inputStreamFactory);
            if (DocIndexImpl.logger.isLoggable(Level.FINEST)) {
                final Stopwatch.Statistics stats = DocIndexImpl.indexLoadindStopWatch.stop();
                DocIndexImpl.logger.log(Level.FINEST, "Loading [" + fileName + "] took " + stats.getLastTime() + " ms.");
            }
        }
        catch (IOException e) {
            throw new IndexException(IndexError.INDEX_LOAD_ERROR, fileName, e);
        }
        this.generatorFactory = new HashGeneratorFactory(configuration);
        this.hashSelector = new ThreadLocal<HashSelector>() {
            @Override
            protected HashSelector initialValue() {
                return new HashSelector(DocIndexImpl.this.index.getK(), DocIndexImpl.this.index.getT());
            }
        };
        this.digester = new ThreadLocal<HashGenerator>() {
            @Override
            protected HashGenerator initialValue() {
                return DocIndexImpl.this.generatorFactory.newGenerator();
            }
        };
    }
    
    @Override
    public DocumentConditionViolation findPartialDocMatches(final CharSequence textContent, final int threshold) {
        if (textContent.length() == 0) {
            return null;
        }
        final Set<HashWithPosition> fingerprint = this.hashSelector.get().getHashes(textContent);
        if (fingerprint.size() == 0) {
            return this.exactNormalizedTextMatch(textContent);
        }
        final AbstractIntObjectMap intersections = (AbstractIntObjectMap)new OpenIntObjectHashMap();
        final AbstractIntIntMap fingerprintMap = (AbstractIntIntMap)new OpenIntIntHashMap();
        for (final HashWithPosition hp : fingerprint) {
            final int hash = hp.getHash();
            final int[] docs = this.index.lookupHash(hash);
            if (docs.length > 0) {
                fingerprintMap.put(hash, hp.getPosition());
            }
            for (int i = 0; i < docs.length; ++i) {
                IntArrayList intersectionHashes = (IntArrayList)intersections.get(docs[i]);
                if (intersectionHashes == null) {
                    intersectionHashes = new IntArrayList(fingerprint.size());
                    intersections.put(docs[i], (Object)intersectionHashes);
                }
                intersectionHashes.add(hash);
            }
        }
        final HitCheckProcedure htp = new HitCheckProcedure(this.index, threshold, fingerprintMap, textContent);
        intersections.forEachPair((IntObjectProcedure)htp);
        return htp.getMatches();
    }
    
    @Override
    public DocumentConditionViolation findExactDocMatches(final CharSequence textContent) {
        if (textContent.length() == 0) {
            return null;
        }
        return this.exactNormalizedTextMatch(textContent);
    }
    
    @Override
    public DocumentConditionViolation findBinaryDocMatches(final byte[] binaryContent) {
        if (binaryContent.length == 0) {
            return null;
        }
        return this.exactBinaryMatch(binaryContent);
    }
    
    private DocumentConditionViolation exactNormalizedTextMatch(final CharSequence normalizedText) {
        final byte[] bytes = IndexUtil.bytesFromCharacters(normalizedText);
        return this.exactBinaryMatch(bytes);
    }
    
    private DocumentConditionViolation exactBinaryMatch(final byte[] bytes) {
        final DocumentConditionViolationImpl violation = new DocumentConditionViolationImpl();
        final byte[] hash = this.digester.get().generate(bytes);
        final int documentId = this.index.lookupDocumentId(hash);
        if (documentId == -1) {
            return null;
        }
        violation.addMatch((DocumentMatch)new DocumentMatchImpl(documentId));
        return (DocumentConditionViolation)violation;
    }
    
    static {
        logger = Logger.getLogger(DocIndexImpl.class.getName());
        indexLoadindStopWatch = new Stopwatch("Load DocIndex");
    }
}
