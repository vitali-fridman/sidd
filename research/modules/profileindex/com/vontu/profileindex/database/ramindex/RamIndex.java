// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import java.util.ArrayList;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.function.IntObjectProcedure;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import cern.colt.map.AbstractIntObjectMap;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchCondition;
import com.vontu.profileindex.database.SearchTerm;
import com.vontu.profileindex.FileInputStreamFactory;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import java.util.logging.Level;
import com.vontu.util.Stopwatch;
import com.vontu.profileindex.InputStreamFactory;
import java.util.logging.Logger;
import com.vontu.profileindex.database.DatabaseIndex;

public final class RamIndex implements DatabaseIndex
{
    private static final Logger _logger;
    private static final DbRowHit[] NO_HITS;
    private final LiveIndex _index;
    private final String _sourceName;
    private final Configuration _configuration;
    
    public RamIndex(final Configuration configutaion, final InputStreamFactory inputStreamFactory) throws IndexException {
        this._configuration = configutaion;
        this._sourceName = inputStreamFactory.name();
        final Stopwatch indexLoadingStopWatch = new Stopwatch("Load RamIndex");
        try {
            if (RamIndex._logger.isLoggable(Level.FINE)) {
                RamIndex._logger.log(Level.FINE, "Loading RAM index from " + this._sourceName + '.');
            }
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                indexLoadingStopWatch.start();
            }
            this._index = new LiveIndex(inputStreamFactory.getInputStream());
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                final Stopwatch.Statistics stats = indexLoadingStopWatch.stop();
                RamIndex._logger.log(Level.FINEST, "Loading of index from " + this._sourceName + " took " + stats.getLastTime() + " ms.");
            }
        }
        catch (Exception e) {
            throw new IndexException(IndexError.INDEX_LOAD_ERROR, this._sourceName, e);
        }
    }
    
    public RamIndex(final Configuration configutaion, final String filePath) throws IndexException {
        this(configutaion, new FileInputStreamFactory(filePath));
    }
    
    @Override
    public boolean[] validateRows(final SearchTerm[][] searchTermsWithOperands, final int[] adjustThreshold, final SearchCondition condition) throws IndexException {
        final boolean[] result = new boolean[searchTermsWithOperands.length];
        for (int i = 0; i < searchTermsWithOperands.length; ++i) {
            final SearchTerm[] searchTerms = searchTermsWithOperands[i];
            final DatabaseRowMatch[] hits = this.findMatches(searchTerms, condition.getColumnMask(), adjustThreshold[i], condition.getExceptionTuples(), 1, false);
            if (hits.length > 0) {
                result[i] = true;
            }
        }
        return result;
    }
    
    @Override
    public DatabaseRowMatch[] findMatches(final SearchTerm[] terms, final int columnsToSearch, final int threshold, final int[] tupleExceptions, final int notUsed, final boolean isInputTabular) {
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Searching RAM index " + this._sourceName + " for " + terms.length + (isInputTabular ? " tabular" : " non-tabular") + " terms" + ", column mask=" + Integer.toString(columnsToSearch, 2) + ", threshold=" + threshold + ", number of tuple exceptions is " + tupleExceptions.length + '.');
        }
        final List commonTerms = new LinkedList();
        final List candidateCells = new LinkedList();
        final Map termDuplicatesMap = deDupTerms(terms);
        final int allCommonTermColumns = this.separateTerms(termDuplicatesMap.keySet(), columnsToSearch, candidateCells, commonTerms);
        if (candidateCells.size() == 0) {
            if (RamIndex._logger.isLoggable(Level.FINE)) {
                if (commonTerms.size() != 0) {
                    RamIndex._logger.log(Level.FINE, "Dropping search because query has only common terms.");
                }
                else if (RamIndex._logger.isLoggable(Level.FINEST)) {
                    RamIndex._logger.log(Level.FINEST, "No terms of the query found in the index - bailing out.");
                }
            }
            return RamIndex.NO_HITS;
        }
        final int thresholdForUncommonTerms = threshold - numColumnsInMask(allCommonTermColumns);
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Column threshold for uncommon terms is " + thresholdForUncommonTerms);
        }
        final AbstractIntObjectMap perRowCandidates = separateCandidatesPerRow(candidateCells);
        final int numCandidateRows = eliminateRowsWithNotEnoughUncommonTerms(perRowCandidates, thresholdForUncommonTerms);
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Number of candidate rows before checking common terms, proximity or tuple exceptions: " + numCandidateRows);
        }
        if (numCandidateRows == 0 && RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Not enough candidate matches to proceed - bailing out of search");
            return RamIndex.NO_HITS;
        }
        final List hits = this.findHits(perRowCandidates, commonTerms, threshold, columnsToSearch, tupleExceptions, isInputTabular, termDuplicatesMap);
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Found " + hits.size() + " possible matches.");
        }
        if (hits.size() == 0) {
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "No matches found.");
            }
            return RamIndex.NO_HITS;
        }
        if (this._configuration.shouldVerifyJohnJohn()) {
            if (!this._configuration.isProximityLogicEnabled() || !isInputTabular) {
                eliminateDuplicateUseOfTerms(hits, termDuplicatesMap, threshold, this._configuration.getSimpleTextProximityRadius());
            }
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "After veryfying for 'John/John' cases " + hits.size() + " macthes remains.");
            }
        }
        if (hits.size() == 0) {
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "No matches remains - bailing out.");
            }
            return RamIndex.NO_HITS;
        }
        if (hits.size() > 0) {
            DbRowHit[] dbRowHits = searchTermHitsToDbRowHits(hits);
            if (this._configuration.getMatchCountVariant() != 1) {
                dbRowHits = this.crossMatchTokenCheck(dbRowHits);
            }
            else if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "Returning " + hits.size() + " matches for this query.");
            }
            return dbRowHits;
        }
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Found " + hits.size() + " (less then minimum required) matches.");
        }
        return RamIndex.NO_HITS;
    }
    
    private DbRowHit[] crossMatchTokenCheck(final DbRowHit[] hits) {
        Arrays.sort(hits, new Comparator() {
            @Override
            public int compare(final Object hit1, final Object hit2) {
                return ((DbRowHit)hit1).getTokenIds().length - ((DbRowHit)hit2).getTokenIds().length;
            }
        });
        for (int i = 0; i < hits.length; ++i) {
            Arrays.sort(hits[i].getTokenIds());
        }
        int numRemoved = 0;
        for (int j = 0; j < hits.length; ++j) {
            final DbRowHit hit = hits[j];
            for (int k = j + 1; k < hits.length; ++k) {
                if (this._configuration.getMatchCountVariant() == 2) {
                    if (isSame(hit.getTokenIds(), hits[k].getTokenIds())) {
                        hit.remove();
                        ++numRemoved;
                        break;
                    }
                }
                else if (isSubset(hit.getTokenIds(), hits[k].getTokenIds())) {
                    hit.remove();
                    ++numRemoved;
                    break;
                }
            }
        }
        if (numRemoved <= 0) {
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "No token set eliminated, returning " + hits.length + " match(es)");
            }
            return hits;
        }
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            final String reason = (this._configuration.getMatchCountVariant() == 2) ? "(non-unique token sets)" : "(token subsets)";
            RamIndex._logger.log(Level.FINEST, "Eliminated " + numRemoved + " match(es) " + reason);
        }
        if (hits.length - numRemoved > 0) {
            final DbRowHit[] pruned = new DbRowHit[hits.length - numRemoved];
            int l = 0;
            int k = 0;
            while (l < hits.length) {
                if (!hits[l].isRemoved()) {
                    pruned[k] = hits[l];
                    ++k;
                }
                ++l;
            }
            if (RamIndex._logger.isLoggable(Level.FINEST)) {
                RamIndex._logger.log(Level.FINEST, "Returning " + pruned.length + " match(es) for this query.");
            }
            return pruned;
        }
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "After token set elimination no matches remains.");
        }
        return RamIndex.NO_HITS;
    }
    
    private static boolean isSame(final int[] hitTokens, final int[] longerHitTokens) {
        return hitTokens.length == longerHitTokens.length && isSubset(hitTokens, longerHitTokens);
    }
    
    private static boolean isSubset(final int[] hitTokens, final int[] longerHitTokens) {
        int i = 0;
        int j = 0;
        while (i < hitTokens.length) {
            while (j < longerHitTokens.length) {
                if (hitTokens[i] == longerHitTokens[j]) {
                    if (++j == longerHitTokens.length && i < hitTokens.length - 1) {
                        return false;
                    }
                    break;
                }
                else {
                    if (hitTokens[i] < longerHitTokens[j]) {
                        return false;
                    }
                    if (j == longerHitTokens.length - 1) {
                        return false;
                    }
                    ++j;
                }
            }
            ++i;
        }
        return true;
    }
    
    private int separateTerms(final Collection uniqueTerms, final int columnsToSearch, final Collection candidateCells, final Collection commonTerms) {
        int allCommonTermColumns = 0;
        for (final SearchTerm term : uniqueTerms) {
            final int colMask = this._index.lookupInAllCommonTerms(columnsToSearch, term);
            if (colMask == -1) {
                this._index.lookupInUncommonTerms(columnsToSearch, term, candidateCells);
            }
            else {
                commonTerms.add(term);
                allCommonTermColumns |= colMask;
            }
        }
        if (RamIndex._logger.isLoggable(Level.FINEST)) {
            RamIndex._logger.log(Level.FINEST, "Query has " + commonTerms.size() + " common terms and " + candidateCells.size() + " candidate cells for uncommon terms");
        }
        return allCommonTermColumns;
    }
    
    private static DbRowHit[] searchTermHitsToDbRowHits(final List hits) {
        final DbRowHit[] dbRowHits = new DbRowHit[hits.size()];
        int i = 0;
        for (final SearchTermHitsForRow hit : hits) {
            dbRowHits[i] = new DbRowHit(hit.getRow(), hit.getSearchTerms());
            ++i;
        }
        return dbRowHits;
    }
    
    private static void eliminateDuplicateUseOfTerms(final List hits, final Map termDupicatesMap, final int threshold, final int proximityRadius) {
        final Iterator hitIt = hits.iterator();
        while (hitIt.hasNext()) {
            final SearchTermHitsForRow hit = hitIt.next();
            final List searchTerms = hit.getSearchTerms();
            final Map deDupMap = new HashMap(searchTerms.size() * 2, 0.55f);
            int searchTermHitIndex = 0;
            for (final SearchTerm term : searchTerms) {
                List dupHitList = deDupMap.get(term);
                if (dupHitList == null) {
                    dupHitList = new LinkedList();
                    deDupMap.put(term, dupHitList);
                }
                dupHitList.add(new Integer(searchTermHitIndex));
                ++searchTermHitIndex;
            }
            boolean isTermsRemoved = false;
            final Set entrySet = deDupMap.entrySet();
            for (final Map.Entry entry : entrySet) {
                final SearchTerm term2 = entry.getKey();
                final List dupIndexList = entry.getValue();
                if (dupIndexList.size() > 1) {
                    final List originalDupTerms = termDupicatesMap.get(term2);
                    final Iterator dilIt = dupIndexList.iterator();
                    final Iterator odtIt = originalDupTerms.iterator();
                    while (dilIt.hasNext()) {
                        final int index = dilIt.next();
                        boolean isFoundUniqueOccurenceInProximity = false;
                        while (odtIt.hasNext()) {
                            final SearchTerm dupTerm = odtIt.next();
                            if (Math.abs(term2.getIndexInContent() - dupTerm.getIndexInContent()) < proximityRadius) {
                                searchTerms.set(index, dupTerm);
                                isFoundUniqueOccurenceInProximity = true;
                                break;
                            }
                        }
                        if (!isFoundUniqueOccurenceInProximity) {
                            searchTerms.set(index, null);
                            isTermsRemoved = true;
                        }
                    }
                }
            }
            if (isTermsRemoved) {
                final Iterator stIt2 = searchTerms.iterator();
                while (stIt2.hasNext()) {
                    if (stIt2.next() == null) {
                        stIt2.remove();
                    }
                    ++searchTermHitIndex;
                }
                if (searchTerms.size() >= threshold) {
                    continue;
                }
                hitIt.remove();
            }
        }
    }
    
    private List findHits(final AbstractIntObjectMap perRowCandidates, final List commonTerms, final int threshold, final int columnsToSearch, final int[] tupleExceptions, final boolean isInputTabular, final Map termDuplicatesMap) {
        final CheckHitProcedure checkHit = new CheckHitProcedure(this._index, commonTerms, threshold, columnsToSearch, tupleExceptions, isInputTabular, termDuplicatesMap, this._configuration);
        perRowCandidates.forEachPair((IntObjectProcedure)checkHit);
        return checkHit.getHits();
    }
    
    private static List doTabularProximityChecks(final List cellsForRow, final Map termDuplicatesMap) {
        final Map lineToCellsMap = new HashMap(cellsForRow.size() * 2, 0.55f);
        for (final CandidateCell cellFromHitList : cellsForRow) {
            final SearchTerm term = cellFromHitList.searchTerm;
            final List dupList = termDuplicatesMap.get(term);
            for (final SearchTerm dupTerm : dupList) {
                CandidateCell cell;
                if (dupTerm != term) {
                    cell = new CandidateCell(cellFromHitList.row, cellFromHitList.col, dupTerm);
                }
                else {
                    cell = cellFromHitList;
                }
                final Integer line = new Integer(dupTerm.getLineInContent());
                List perLineCellList = lineToCellsMap.get(line);
                if (perLineCellList == null) {
                    perLineCellList = new LinkedList();
                    lineToCellsMap.put(line, perLineCellList);
                }
                boolean toAdd = true;
                for (final CandidateCell cellOnList : perLineCellList) {
                    if (cellOnList.searchTerm.equals(cell.searchTerm) && cellOnList.col == cell.col) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    perLineCellList.add(cell);
                }
            }
        }
        List longestList = null;
        int longestSize = -1;
        for (final List cellList : lineToCellsMap.values()) {
            if (cellList.size() > longestSize) {
                longestList = cellList;
                longestSize = longestList.size();
            }
        }
        return longestList;
    }
    
    private static List doSimpleTextProximityChecks(final List cellsForRow, final Map termDuplicatesMap, final int proximityRadius) {
        int numTermsWithoutDuplicates = 0;
        int middleLocation = 0;
        final List cellsWithDups = new LinkedList();
        final List cellsWithoutDups = new LinkedList();
        final List dupsForCellsWithDups = new LinkedList();
        for (final CandidateCell cell : cellsForRow) {
            final SearchTerm term = cell.searchTerm;
            final List dupList = termDuplicatesMap.get(term);
            if (dupList.size() == 1) {
                ++numTermsWithoutDuplicates;
                middleLocation += term.getIndexInContent();
                cellsWithoutDups.add(cell);
            }
            else {
                cellsWithDups.add(cell);
                dupsForCellsWithDups.add(dupList);
            }
        }
        if (numTermsWithoutDuplicates == 0) {
            if (RamIndex._logger.isLoggable(Level.FINE)) {
                RamIndex._logger.log(Level.FINE, "Skipping proximity check for non-tabular search - all candidate terms have duplicates");
            }
            return cellsForRow;
        }
        middleLocation /= numTermsWithoutDuplicates;
        Iterator cellIt = cellsWithDups.iterator();
        final Iterator dupListsIt = dupsForCellsWithDups.iterator();
        while (cellIt.hasNext()) {
            final CandidateCell cell2 = cellIt.next();
            final List dupList = dupListsIt.next();
            SearchTerm term2 = cell2.searchTerm;
            for (final SearchTerm dupTerm : dupList) {
                if (Math.abs(dupTerm.getIndexInContent() - middleLocation) < Math.abs(term2.getIndexInContent() - middleLocation)) {
                    term2 = dupTerm;
                }
            }
            if (Math.abs(term2.getIndexInContent() - middleLocation) <= proximityRadius) {
                cell2.searchTerm = term2;
            }
            else {
                cell2.searchTerm = null;
            }
        }
        cellIt = cellsWithoutDups.iterator();
        while (cellIt.hasNext()) {
            final CandidateCell cell = cellIt.next();
            if (Math.abs(cell.searchTerm.getIndexInContent() - middleLocation) > proximityRadius) {
                cell.searchTerm = null;
            }
        }
        cellIt = cellsForRow.iterator();
        while (cellIt.hasNext()) {
            final CandidateCell cell = cellIt.next();
            if (cell.searchTerm == null) {
                cellIt.remove();
            }
        }
        return cellsForRow;
    }
    
    private static boolean tupleException(final List cells, final int[] tupleExceptions) {
        if (tupleExceptions != null) {
            final int allColumnsForCells = allColumns(cells);
            for (int i = 0; i < tupleExceptions.length; ++i) {
                if (tupleExceptions[i] == allColumnsForCells) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int allColumns(final List cells) {
        int allColumns = 0;
        for (final CandidateCell cell : cells) {
            allColumns |= 1 << cell.col;
        }
        return allColumns;
    }
    
    private static int eliminateRowsWithNotEnoughUncommonTerms(final AbstractIntObjectMap perRowCandidates, final int thresholdForUncommonTerms) {
        int numCandidateRows = 0;
        final IntArrayList keys = new IntArrayList(perRowCandidates.size());
        perRowCandidates.keys(keys);
        final int[] rows = keys.elements();
        for (int i = 0; i < rows.length; ++i) {
            if (((List)perRowCandidates.get(rows[i])).size() < thresholdForUncommonTerms) {
                perRowCandidates.removeKey(rows[i]);
            }
            else {
                ++numCandidateRows;
            }
        }
        return numCandidateRows;
    }
    
    private static AbstractIntObjectMap separateCandidatesPerRow(final List candidateCells) {
        final AbstractIntObjectMap perRowMap = (AbstractIntObjectMap)new OpenIntObjectHashMap(candidateCells.size() * 2, 0.550000011920929, 0.9900000095367432);
        for (final CandidateCell cell : candidateCells) {
            List sameRowCells = (List)perRowMap.get(cell.row);
            if (sameRowCells == null) {
                sameRowCells = new ArrayList(31);
                perRowMap.put(cell.row, (Object)sameRowCells);
            }
            sameRowCells.add(cell);
        }
        return perRowMap;
    }
    
    private static byte numColumnsInMask(final int allCommonTermColumns) {
        byte numColumns = 0;
        for (int i = 0; i < 31; ++i) {
            if ((allCommonTermColumns & 1 << i) != 0x0) {
                ++numColumns;
            }
        }
        return numColumns;
    }
    
    private static Map deDupTerms(final SearchTerm[] terms) {
        final Map termDuplicatesMap = new HashMap(terms.length * 2, 0.55f);
        for (int i = 0; i < terms.length; ++i) {
            List dupList = termDuplicatesMap.get(terms[i]);
            if (dupList == null) {
                dupList = new LinkedList();
                termDuplicatesMap.put(terms[i], dupList);
            }
            dupList.add(terms[i]);
        }
        return termDuplicatesMap;
    }
    
    static {
        _logger = Logger.getLogger(RamIndex.class.getName());
        NO_HITS = new DbRowHit[0];
    }
    
    private static final class CheckHitProcedure implements IntObjectProcedure
    {
        private final List _hits;
        private final LiveIndex _index;
        private final Collection _commonTerms;
        private final int _columnsToSearch;
        private final int _threshold;
        private final boolean _isInputTabular;
        private final int[] _exceptionTuples;
        private final Map _termDuplicatesMap;
        private final Configuration _configuration;
        
        CheckHitProcedure(final LiveIndex index, final Collection commonTerms, final int threshold, final int columnsToSearch, final int[] exceptionTuples, final boolean isInputTabular, final Map termDuplicatesMap, final Configuration configuration) {
            this._hits = new LinkedList();
            this._index = index;
            this._commonTerms = commonTerms;
            this._columnsToSearch = columnsToSearch;
            this._threshold = threshold;
            this._exceptionTuples = exceptionTuples;
            this._isInputTabular = isInputTabular;
            this._termDuplicatesMap = termDuplicatesMap;
            this._configuration = configuration;
        }
        
        public boolean apply(final int row, final Object value) {
            List cellsForRow = (List)value;
            for (final SearchTerm commonTerm : this._commonTerms) {
                this._index.lookupInCommonTerms(this._columnsToSearch, commonTerm, row, cellsForRow);
            }
            if (cellsForRow.size() >= this._threshold) {
                if (this._configuration.isProximityLogicEnabled()) {
                    if (this._isInputTabular) {
                        cellsForRow = doTabularProximityChecks(cellsForRow, this._termDuplicatesMap);
                    }
                    else {
                        cellsForRow = doSimpleTextProximityChecks(cellsForRow, this._termDuplicatesMap, this._configuration.getSimpleTextProximityRadius());
                    }
                }
                if (cellsForRow.size() >= this._threshold && !tupleException(cellsForRow, this._exceptionTuples)) {
                    final SearchTermHitsForRow hit = new SearchTermHitsForRow(row, cellsForRow);
                    this._hits.add(hit);
                    if (this._hits.size() == this._configuration.getMaxMatchCount()) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        List getHits() {
            return this._hits;
        }
    }
}
