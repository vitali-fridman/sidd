// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.util.ProtectException;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.io.BufferedReader;
import com.vontu.nlp.lexer.Lexer;
import java.util.Collection;
import com.vontu.nlp.lexer.LexerSpecification;
import java.util.Collections;
import com.vontu.util.Stopwatch;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import com.vontu.util.config.SettingProvider;
import com.vontu.keystorehouse.KeyContainer;

public final class CryptoIndexCreator
{
    private final KeyContainer _key;
    private final DatabaseProfileDescriptor _descriptor;
    private final SettingProvider _settingProvider;
    public static final String DROP_INVALID_ROWS_NAME = "drop_invalid_rows";
    public static final boolean DROP_INVALID_ROWS_DEFAULT = false;
    
    public CryptoIndexCreator(final SettingProvider settingProvider, final DatabaseProfileDescriptor descriptor, final KeyContainer key) {
        this._settingProvider = settingProvider;
        this._descriptor = descriptor;
        this._key = key;
    }
    
    private OutputStream createIndexStream(final File indexFile, final int filestreamOutputBufferSize) throws IndexerException {
        try {
            return new BufferedOutputStream(new FileOutputStream(indexFile), filestreamOutputBufferSize);
        }
        catch (FileNotFoundException e) {
            throw new IndexerException(IndexerError.CRYPTO_FILE_WRITE_ERROR, this._descriptor.name(), e);
        }
    }
    
    private void closeIndexStream(final OutputStream indexStream) throws IndexerException {
        try {
            indexStream.close();
        }
        catch (IOException e) {
            throw new IndexerException(IndexerError.CRYPTO_FILE_WRITE_ERROR, this._descriptor.name(), e);
        }
    }
    
    public CryptoIndexResult createIndex(final Reader dataReader, final PrintWriter errorWriter, final File indexFile) throws IndexerException, InterruptedException {
        return this.createIndex(dataReader, errorWriter, indexFile, 32768);
    }
    
    public CryptoIndexResult createIndex(final Reader dataReader, final PrintWriter errorWriter, final File indexFile, final int filestreamOutputBufferSize) throws IndexerException, InterruptedException {
        final OutputStream indexStream = this.createIndexStream(indexFile, filestreamOutputBufferSize);
        try {
            final Stopwatch stopwatch = new Stopwatch("CryptoIndexCreator");
            stopwatch.start();
            final DataSourceStatistics dataSourceStatistics = this.createIndex(dataReader, this._key, indexStream, errorWriter);
            return new CryptoIndexResult(indexFile, this._key.getAlias(), dataSourceStatistics, stopwatch.stop().getLastTime());
        }
        finally {
            this.closeIndexStream(indexStream);
        }
    }
    
    private DataSourceStatistics createIndex(final Reader dataReader, final KeyContainer hmacKey, final OutputStream result, final PrintWriter errorWriter) throws IndexerException, InterruptedException {
        final IndexTokenSetBuilder tokenSetBuilder = new IndexTokenSetBuilder(this._settingProvider, this._descriptor, result, errorWriter, hmacKey);
        try {
            final Lexer lexer = new Lexer(new LexerSpecification(this._settingProvider, (Collection)Collections.singleton(tokenSetBuilder)));
            lexer.compile();
            lexer.run((Reader)new BufferedReader(dataReader));
        }
        catch (IndexerException e) {
            throw e;
        }
        catch (ProtectException e2) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, (Throwable)e2);
        }
        return tokenSetBuilder.getStatistics();
    }
}
