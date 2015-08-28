// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.nlp.lexer.TokenPosition;
import com.vontu.detection.output.TokenMarker;
import java.util.logging.Level;
import com.vontu.nlp.lexer.TextToken;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Collection;
import java.util.logging.Logger;
import com.vontu.detection.output.ConditionViolation;

final class DatabaseMatchConditionViolation implements ConditionViolation
{
    private static final Logger _logger;
    private final Collection _markers;
    private final Set _dbRowNumbers;
    
    DatabaseMatchConditionViolation() {
        this._markers = new LinkedList();
        this._dbRowNumbers = new HashSet();
    }
    
    public void addMatch(final DatabaseRowMatch match, final SearchContent searchContent) {
        this.addMessageMarkers(match.getRow(), searchContent.getTokenIterator(match.getTokenIds()));
    }
    
    private void addMessageMarkers(final int dbRowNumber, final Iterator matchedTokens) {
        this._dbRowNumbers.add(new Integer(dbRowNumber));
        while (matchedTokens.hasNext()) {
            final TextToken matchedToken = matchedTokens.next();
            final TokenMarker marker = createTokenMarker(dbRowNumber, matchedToken.getPosition());
            if (DatabaseMatchConditionViolation._logger.isLoggable(Level.FINEST)) {
                DatabaseMatchConditionViolation._logger.finest("Adding marker for token: " + matchedToken);
            }
            this._markers.add(marker);
        }
    }
    
    private static TokenMarker createTokenMarker(final int dbRow, final TokenPosition tokenPosition) {
        return (TokenMarker)new DatabaseRowMarker(dbRow, tokenPosition);
    }
    
    public int matchCount() {
        return this._dbRowNumbers.size();
    }
    
    public TokenMarker[] tokenMarkers() {
        return this._markers.toArray(new TokenMarker[this._markers.size()]);
    }
    
    static {
        _logger = Logger.getLogger(DatabaseMatchConditionViolation.class.getName());
    }
}
