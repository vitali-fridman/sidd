// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.Arrays;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import java.util.Collections;
import java.util.Collection;

public class FilenameCollection implements Collection<String>
{
    private final Collection<String> _fileNames;
    
    public FilenameCollection(final Collection<String> fileNames) {
        this._fileNames = Collections.unmodifiableCollection((Collection<? extends String>)fileNames);
    }
    
    public static FilenameCollection forIndex(final IndexDescriptorMarshallable index) {
        final int numberOfFiles = index.getNumberOfFiles();
        if (numberOfFiles > 1) {
            final String[] fileNames = new String[numberOfFiles];
            for (int i = 0; i < fileNames.length; ++i) {
                fileNames[i] = index.getBaseFileName() + '.' + i;
            }
            return new FilenameCollection(Arrays.asList(fileNames));
        }
        return new FilenameCollection(Collections.singleton(index.getBaseFileName()));
    }
    
    @Override
    public boolean isEmpty() {
        return this._fileNames.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this._fileNames.contains(o);
    }
    
    @Override
    public Object[] toArray() {
        return this._fileNames.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this._fileNames.toArray(a);
    }
    
    @Override
    public boolean add(final String o) {
        return this._fileNames.add(o);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this._fileNames.remove(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this._fileNames.containsAll(c);
    }
    
    @Override
    public boolean addAll(final Collection<? extends String> c) {
        return this._fileNames.addAll(c);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this._fileNames.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this._fileNames.retainAll(c);
    }
    
    @Override
    public void clear() {
        this._fileNames.clear();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this._fileNames.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this._fileNames.hashCode();
    }
    
    @Override
    public Iterator<String> iterator() {
        return this._fileNames.iterator();
    }
    
    @Override
    public int size() {
        return this._fileNames.size();
    }
    
    public File[] toFiles(final File indexFolder) {
        final Collection<File> files = new ArrayList<File>(this.size());
        for (final String fileName : this) {
            files.add(new File(indexFolder, fileName));
        }
        return files.toArray(new File[files.size()]);
    }
    
    public String toCsv() {
        final StringBuilder listBuilder = new StringBuilder();
        for (final String filename : this) {
            if (listBuilder.length() > 0) {
                listBuilder.append(", ");
            }
            listBuilder.append(filename);
        }
        return listBuilder.toString();
    }
    
    @Override
    public String toString() {
        return this._fileNames.toString();
    }
}
