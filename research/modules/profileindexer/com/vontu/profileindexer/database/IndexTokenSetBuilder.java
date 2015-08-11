// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.nlp.lexer.asiansupport.LexerAsianTokenList;
import com.vontu.ramindex.util.IdxRdxMetadata;
import com.vontu.nlp.lexer.Token;
import java.util.logging.Level;
import com.vontu.security.crypto.CryptoException;
import com.vontu.profileindexer.DataSourceCorruptedException;
import java.io.IOException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import com.vontu.nlp.lexer.asiansupport.AsianTokenLookupList;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.vontu.keystorehouse.KeyContainer;
import java.io.OutputStream;
import java.io.PrintWriter;
import com.vontu.nlp.lexer.pattern.SystemPattern;
import java.util.logging.Logger;
import com.vontu.nlp.lexer.TokenSetBuilder;

class IndexTokenSetBuilder extends TokenSetBuilder
{
    static final int CORRUPTION_CHECK_START_ROW_COUNT = 1000;
    private static final Logger _logger;
    private static final String DESCRIPTIVE_NAME = "Index Token Set Builder";
    private final int _columnCount;
    private final SystemPattern[] _systemPatterns;
    private final String _columnSeparator;
    private int _currentDataSourceLine;
    private int _currentProfileColumn;
    private final DatabaseProfileDescriptor _descriptor;
    private final PrintWriter _errorWriter;
    private final OutputStream _indexStream;
    private final KeyContainer _hmacKey;
    private final StringBuffer _lineData;
    private boolean _previousWasSeparator;
    private final ByteArrayOutputStream _rowBuffer;
    private final DataOutputStream _rowBufferData;
    private final boolean _shouldDropInvalidRows;
    private final boolean _shouldTrackErrors;
    private final StatisticsBuilder _statisticsBuilder;
    private final TermBuilder _termBuilder;
    private final AsianTokenLookupList _charLookup;
    
    IndexTokenSetBuilder(final SettingProvider settingProvider, final DatabaseProfileDescriptor descriptor, final OutputStream result, final PrintWriter errorWriter, final KeyContainer hmacKey) {
        super(0);
        this._lineData = new StringBuffer();
        this._termBuilder = new TermBuilder();
        this._indexStream = result;
        this._descriptor = descriptor;
        this._columnSeparator = new String(new char[] { this._descriptor.columnSeparator() });
        this._systemPatterns = getSystemPatterns(descriptor.columns());
        this._columnCount = this._systemPatterns.length;
        this._hmacKey = hmacKey;
        this._previousWasSeparator = true;
        final SettingReader settingReader = new SettingReader(settingProvider, IndexTokenSetBuilder._logger);
        this._shouldDropInvalidRows = settingReader.getBooleanSetting("drop_invalid_rows", false);
        IndexTokenSetBuilder._logger.fine("Skip bad lines while parsing data dump file is set to: " + this._shouldDropInvalidRows);
        this._errorWriter = errorWriter;
        this._shouldTrackErrors = (errorWriter != null);
        this._rowBuffer = new ByteArrayOutputStream();
        this._rowBufferData = new DataOutputStream(this._rowBuffer);
        this._statisticsBuilder = new StatisticsBuilder(descriptor);
        this._currentProfileColumn = 0;
        this._currentDataSourceLine = 0;
        this._charLookup = new AsianTokenLookupList();
    }
    
    private void appendPreviousToken() throws IndexerException {
        if (this._currentProfileColumn >= this._columnCount) {
            return;
        }
        final String term = this._termBuilder.getTerm();
        if (term.length() == 0) {
            if (this._currentProfileColumn < this._columnCount) {
                this._statisticsBuilder.incrementEmptyCellCount(this.getCurrentProfileColumnIndex());
            }
            return;
        }
        if (this._statisticsBuilder.incrementRowCellCount() + this._statisticsBuilder.cellCount() == Integer.MAX_VALUE) {
            throw new IndexerException(IndexerError.MAXIMUM_SIZE_EXCEEDED, new Object[] { this._descriptor.name(), String.valueOf(Integer.MAX_VALUE) });
        }
        this._charLookup.addUnhashedCharater(term.charAt(0), term.length());
        final byte[] cryptoHash = this.computeRawTermHash(term);
        try {
            this._rowBufferData.write(cryptoHash, 0, cryptoHash.length);
            this._rowBufferData.writeInt(this.getRowsProcessed() + 1);
            this._rowBufferData.writeByte((byte)this._currentProfileColumn);
        }
        catch (IOException e) {
            throw new IndexerException(IndexerError.CRYPTO_FILE_WRITE_ERROR, this._descriptor.name(), e);
        }
    }
    
    private void checkCorruptionThresholdExceeded() throws DataSourceCorruptedException {
        if (this.getRowsProcessed() > 1000 && this.isCorruptionThresholdExceeded()) {
            throw new DataSourceCorruptedException(IndexerError.PROFILE_DATA_CORRUPTED, new Object[] { this._descriptor.name(), String.valueOf(this._descriptor.errorThreshold()), this.getStatistics() }, this.getStatistics());
        }
    }
    
    private byte[] computeRawTermHash(final String term) throws IndexerException {
        try {
            return this._hmacKey.computeDigest(term);
        }
        catch (CryptoException e) {
            throw new IndexerException(IndexerError.CRYPTOGRAPHIC_ERROR, new Object[] { this._descriptor.name(), term, String.valueOf(this._statisticsBuilder.rowCount()), String.valueOf(this.getCurrentProfileColumnIndex()) }, (Throwable)e);
        }
    }
    
    public void done() {
        super.done();
        try {
            this.appendPreviousToken();
            this.writePreviousLine();
            this.writeFooter();
            this._statisticsBuilder.addRow();
            if (this._shouldTrackErrors) {
                this._errorWriter.flush();
            }
        }
        catch (IndexerException e) {
            IndexTokenSetBuilder._logger.log(Level.SEVERE, IndexerError.CRYPTO_FILE_WRITE_ERROR.getDescription(new Object[] { this._descriptor.name() }), (Throwable)e);
        }
    }
    
    private int getCurrentLineForReporting() {
        return this._currentDataSourceLine + 1;
    }
    
    public String getDescriptiveName() {
        return "Index Token Set Builder";
    }
    
    private int getRowsProcessed() {
        return this._statisticsBuilder.rowCount();
    }
    
    private int getCurrentProfileColumnIndex() {
        return this._currentProfileColumn + 1;
    }
    
    public DataSourceStatistics getStatistics() {
        return this._statisticsBuilder;
    }
    
    private static SystemPattern[] getSystemPatterns(final DatabaseProfileColumn[] columns) {
        final SystemPattern[] systemPatterns = new SystemPattern[columns.length];
        for (int i = 0; i < systemPatterns.length; ++i) {
            final int systemPatternIndex = columns[i].index() - 1;
            if (systemPatterns[systemPatternIndex] != null) {
                throw new IllegalArgumentException("More than column has index " + columns[i].index() + '.');
            }
            systemPatterns[systemPatternIndex] = columns[i].systemPattern();
        }
        return systemPatterns;
    }
    
    private boolean isColumnSeparator(final Token token) {
        return token.getValue().equals(this._columnSeparator) || token.getValue().startsWith(this._columnSeparator);
    }
    
    private boolean isCorruptionThresholdExceeded() {
        return this.getRowsProcessed() > 0 && this._statisticsBuilder.invalidRowCount() * 100 / this.getRowsProcessed() > this._descriptor.errorThreshold();
    }
    
    private boolean isRowInvalid() {
        return this._statisticsBuilder.isCurrentRowInvalid();
    }
    
    private void prepareForNextCell() {
        this._termBuilder.reset();
        this._previousWasSeparator = true;
    }
    
    private boolean isHeaderRow() {
        return this._currentDataSourceLine == 0 && this._descriptor.hasHeaders();
    }
    
    public void process(final Token currentToken) throws IndexerException, InterruptedException {
        if (currentToken.getPosition().line != this._currentDataSourceLine) {
            if (!this.isHeaderRow()) {
                this.appendPreviousToken();
                this.checkCorruptionThresholdExceeded();
                this.writePreviousLine();
                this.prepareForNextLine();
            }
            this._currentDataSourceLine = currentToken.getPosition().line;
        }
        if (this.isHeaderRow()) {
            IndexTokenSetBuilder._logger.fine("Skipping token \"" + currentToken.getValue() + "\" (should be header) in row number 1.");
            return;
        }
        if (this._shouldTrackErrors) {
            this._lineData.append(currentToken.getValue());
        }
        if (currentToken.getType() == 12 && this.isColumnSeparator(currentToken)) {
            this.processSeparator();
        }
        else {
            this.processToken(currentToken);
        }
    }
    
    private void processSeparator() throws IndexerException {
        if (this._previousWasSeparator) {
            this._statisticsBuilder.incrementEmptyCellCount(this.getCurrentProfileColumnIndex());
        }
        else {
            this.appendPreviousToken();
        }
        ++this._currentProfileColumn;
        this.prepareForNextCell();
    }
    
    private void processToken(final Token token) {
        if (this._previousWasSeparator) {
            this.validateToken(token);
            this._previousWasSeparator = false;
            this._termBuilder.append(token);
        }
        else {
            if (token.getType() == 11) {
                return;
            }
            this._termBuilder.append(token);
        }
    }
    
    private boolean shouldSkipThisRow() {
        return this.isRowInvalid() && this._shouldDropInvalidRows;
    }
    
    private void writePreviousLine() throws IndexerException {
        this._statisticsBuilder.validateColumnCount(this.getCurrentProfileColumnIndex());
        if (this.isRowInvalid() && this._shouldTrackErrors) {
            this._errorWriter.print("Line " + this.getCurrentLineForReporting() + ": ");
            this._errorWriter.print(this._lineData);
            this._errorWriter.print(": ");
            this._statisticsBuilder.writeRowErrors(this._errorWriter);
            this._errorWriter.println();
        }
        if (this.shouldSkipThisRow()) {
            IndexTokenSetBuilder._logger.fine("Skipping invalid line " + this.getCurrentLineForReporting() + '.');
        }
        else {
            try {
                this._statisticsBuilder.addRowCellCountToTotal();
                this._rowBuffer.writeTo(this._indexStream);
            }
            catch (IOException e) {
                throw new IndexerException(IndexerError.CRYPTO_FILE_WRITE_ERROR, this._descriptor.name(), e);
            }
        }
    }
    
    private void prepareForNextLine() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        this._statisticsBuilder.addRow();
        this._rowBuffer.reset();
        this._lineData.setLength(0);
        this._currentProfileColumn = 0;
        if (IndexTokenSetBuilder._logger.isLoggable(Level.FINE) && this.getCurrentLineForReporting() % 10000 == 0) {
            IndexTokenSetBuilder._logger.fine("Indexing line number " + this.getCurrentLineForReporting() + '.');
        }
        this.prepareForNextCell();
    }
    
    private void validateToken(final Token token) {
        if (token.getType() == 12) {
            return;
        }
        if (this._currentProfileColumn >= this._systemPatterns.length) {
            return;
        }
        final SystemPattern systemPattern = this._systemPatterns[this._currentProfileColumn];
        if (systemPattern != null && !systemPattern.getPatternMatcher().matchesSystemPattern(token)) {
            this._statisticsBuilder.addFormatError(token, this.getCurrentProfileColumnIndex(), systemPattern);
        }
    }
    
    private void writeFooter() throws IndexerException {
        try {
            final IdxRdxMetadata footer = new IdxRdxMetadata();
            footer.setAsianTokenList(new LexerAsianTokenList(this._charLookup));
            footer.writeIdxFooter(this._indexStream);
            if (IndexTokenSetBuilder._logger.isLoggable(Level.FINER)) {
                IndexTokenSetBuilder._logger.warning(this._charLookup.toString());
            }
        }
        catch (IOException e) {
            throw new IndexerException(IndexerError.CRYPTO_FILE_WRITE_ERROR, this._descriptor.name(), e);
        }
    }
    
    static {
        _logger = Logger.getLogger(IndexTokenSetBuilder.class.getName());
    }
}
