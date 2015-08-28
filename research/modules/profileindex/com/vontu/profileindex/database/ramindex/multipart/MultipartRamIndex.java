// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.multipart;

import com.vontu.util.BooleanArray;
import com.vontu.profileindex.database.SearchCondition;
import java.util.Collection;
import java.util.Arrays;
import com.vontu.profileindex.database.ramindex.DbRowHit;
import java.util.LinkedList;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchTerm;
import java.util.Iterator;
import com.vontu.util.Disposable;
import java.lang.reflect.InvocationTargetException;
import com.vontu.profileindex.IndexError;
import java.util.ArrayList;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.ramindex.rmi.RmiRamIndexProxy;
import com.vontu.profileindex.InputStreamFactory;
import edu.oswego.cs.dl.util.concurrent.Callable;
import edu.oswego.cs.dl.util.concurrent.FutureResult;
import edu.oswego.cs.dl.util.concurrent.PooledExecutor;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.util.config.SettingProvider;
import edu.oswego.cs.dl.util.concurrent.Executor;
import java.util.List;
import com.vontu.profileindex.database.DatabaseIndex;
import com.vontu.util.DisposableImpl;

public final class MultipartRamIndex extends DisposableImpl implements DatabaseIndex
{
    private final List _parts;
    private final String[] _indexIdentifiers;
    private final Executor _executor;
    
    public MultipartRamIndex(final SettingProvider settigns, final ProfileIndexDescriptor indexDescriptor) throws IndexException {
        assert indexDescriptor.streams().length > 1;
        this._indexIdentifiers = new String[] { indexDescriptor.profile().name(), String.valueOf(indexDescriptor.version()) };
        final int partCount = indexDescriptor.streams().length;
        final PooledExecutor executor = new PooledExecutor();
        executor.setMinimumPoolSize(partCount);
        this._executor = (Executor)executor;
        final String NAME_PREFIX = "IDX_" + indexDescriptor.profile().profileId() + '_' + indexDescriptor.version() + '.';
        final long FILE_SIZE = indexDescriptor.size() / partCount;
        final FutureResult[] callResults = new FutureResult[partCount];
        boolean isFailed = false;
        try {
            for (int i = 0; i < partCount; ++i) {
                final String NAME = NAME_PREFIX + i;
                final InputStreamFactory INDEX_STREAM = indexDescriptor.streams()[i];
                final FutureResult callResult = new FutureResult();
                final Runnable loader = callResult.setter((Callable)new Callable() {
                    public Object call() throws IndexException {
                        return new RmiRamIndexProxy(settigns, NAME, INDEX_STREAM, FILE_SIZE);
                    }
                });
                this._executor.execute(loader);
                callResults[i] = callResult;
            }
            this._parts = new ArrayList(partCount);
            for (int i = 0; i < partCount; ++i) {
                this._parts.add(callResults[i].get());
            }
        }
        catch (InterruptedException e2) {
            isFailed = true;
            Thread.currentThread().interrupt();
            throw new IndexException(IndexError.INDEX_LOAD_INTERRUPTED, this._indexIdentifiers);
        }
        catch (InvocationTargetException e) {
            isFailed = true;
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw (IndexException)cause;
        }
        finally {
            if (isFailed) {
                try {
                    this.dispose();
                }
                catch (Throwable t) {
                    throw new IndexException(IndexError.INDEX_UNLOAD_ERROR, this._indexIdentifiers, t);
                }
            }
        }
    }
    
    public synchronized void dispose() throws Throwable {
        super.dispose();
        Throwable throwable = null;
        final Iterator partIterator = this._parts.iterator();
        while (partIterator.hasNext()) {
            try {
                partIterator.next().dispose();
            }
            catch (Throwable t) {
                if (throwable != null) {
                    continue;
                }
                throwable = t;
            }
        }
        if (throwable != null) {
            throw throwable;
        }
    }
    
    public DatabaseRowMatch[] findMatches(final SearchTerm[] terms, final int columnMask, final int threshold, final int[] exceptionTuples, final int minMatches, final boolean isInputTabular) throws IndexException {
        final int partCount = this._parts.size();
        final FutureResult[] callResults = new FutureResult[partCount];
        final Collection results = new LinkedList();
        try {
            for (int i = 0; i < partCount; ++i) {
                final DatabaseIndex ramIndex = this._parts.get(i);
                final FutureResult callResult = new FutureResult();
                final Runnable finder = callResult.setter((Callable)new Callable() {
                    public Object call() throws IndexException {
                        return ramIndex.findMatches(terms, columnMask, threshold, exceptionTuples, 0, isInputTabular);
                    }
                });
                this._executor.execute(finder);
                callResults[i] = callResult;
            }
            for (int i = 0; i < partCount; ++i) {
                final DbRowHit[] partResult = (DbRowHit[])callResults[i].get();
                results.addAll(Arrays.asList(partResult));
            }
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw new IndexException(IndexError.INDEX_LOOKUP_INTERRUPTED, this._indexIdentifiers);
        }
        catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw (IndexException)cause;
        }
        return results.toArray(new DbRowHit[0]);
    }
    
    public boolean[] validateRows(final SearchTerm[][] searchTermsWithOperands, final int[] adjustThreshold, final SearchCondition condition) throws IndexException {
        final int partCount = this._parts.size();
        final FutureResult[] callResults = new FutureResult[partCount];
        boolean[] results = new boolean[searchTermsWithOperands.length];
        try {
            for (int i = 0; i < partCount; ++i) {
                final DatabaseIndex ramIndex = this._parts.get(i);
                final FutureResult callResult = new FutureResult();
                final Runnable finder = callResult.setter((Callable)new Callable() {
                    public Object call() throws IndexException {
                        return ramIndex.validateRows(searchTermsWithOperands, adjustThreshold, condition);
                    }
                });
                this._executor.execute(finder);
                callResults[i] = callResult;
            }
            for (int i = 0; i < partCount; ++i) {
                final boolean[] partResult = (boolean[])callResults[i].get();
                results = BooleanArray.or(results, partResult);
            }
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw new IndexException(IndexError.INDEX_LOOKUP_INTERRUPTED, this._indexIdentifiers);
        }
        catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw (IndexException)cause;
        }
        return results;
    }
}
