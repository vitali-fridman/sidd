// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import com.vontu.external.csvfile.CsvWriter;
import com.vontu.util.collection.CollectionUtil;
import com.vontu.util.collection.IterableConverter;
import com.vontu.util.collection.Converter;
import java.util.Collections;
import java.util.Iterator;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Collection;
import java.io.Reader;
import com.vontu.external.csvfile.CsvReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CSVString<T>
{
    private final List<String> _list;
    private String _separator;
    
    public CSVString() {
        this._list = new ArrayList<String>();
    }
    
    public CSVString(final String source) {
        this._list = new ArrayList<String>();
        if (source != null) {
            try {
                final List<String> csvStrings = (List<String>)new CsvReader((Reader)new StringReader(source)).readFields();
                if (csvStrings != null) {
                    this._list.addAll(csvStrings);
                }
            }
            catch (IOException e) {
                throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final CsvToStringWriter csv = new CsvToStringWriter(writer, this._separator);
        csv.writeFields(this._list);
        try {
            csv.close();
        }
        catch (IOException e) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR);
        }
        return writer.toString().trim();
    }
    
    public void add(final T value) {
        this._list.add(value.toString());
    }
    
    public CSVString<T> addAllNonString(final Collection<T> values) {
        for (final T value : values) {
            this.add(value);
        }
        return this;
    }
    
    public void addAll(final Collection<? extends String> values) {
        this._list.addAll(values);
    }
    
    public List<String> getValues() {
        return Collections.unmodifiableList((List<? extends String>)this._list);
    }
    
    public List<T> getValues(final Converter<String, T> converter) {
        return CollectionUtil.createArrayList(new IterableConverter<String, T>(this._list, converter).iterator());
    }
    
    public boolean isEmpty() {
        return this._list.isEmpty();
    }
    
    public void setSeparator(final String separator) {
        this._separator = separator;
    }
    
    private static class CsvToStringWriter extends CsvWriter
    {
        private final String _fieldSeparator;
        
        CsvToStringWriter(final Writer writer, final String separator) {
            super(writer);
            if (separator != null) {
                this._fieldSeparator = separator;
            }
            else {
                this._fieldSeparator = String.valueOf(this.fieldSeparator);
            }
        }
        
        public void writeFields(final List<String> fields) {
            for (int n = fields.size() - 1, i = 0; i <= n; ++i) {
                if (fields.get(i).contains(this._fieldSeparator)) {
                    this.out.print(this.textQualifier + fields.get(i) + this.textQualifier);
                }
                else {
                    this.out.print(fields.get(i));
                }
                if (i < n) {
                    this.out.print(this._fieldSeparator);
                }
            }
        }
    }
}
