// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public final class IndexFilePrinter
{
    private static final int BUFFER_SIZE = 5242880;
    private final RandomAccessFile _indexFile;
    private final PrintStream _output;
    
    private IndexFilePrinter(final String ramIndexFileName) throws IOException {
        final File ramIndexFile = new File(ramIndexFileName);
        this._indexFile = new RandomAccessFile(ramIndexFile, "r");
        this._output = System.out;
    }
    
    public IndexFilePrinter(final String ramIndexFileName, final String outputFileName) throws IOException {
        final File ramIndexFile = new File(ramIndexFileName);
        this._indexFile = new RandomAccessFile(ramIndexFile, "rw");
        this._output = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFileName), 5242880));
    }
    
    public void printIndex() throws IOException {
        this.printIndexPart(true, "All Common Terms");
        this.printIndexPart(false, "Common Terms");
        this.printIndexPart(false, "Uncommon Terms");
    }
    
    private void printIndexPart(final boolean allCommonTerms, final String name) throws IOException {
        this._output.println(name + " index.");
        this._output.println("-----------------------");
        final int termLength = this._indexFile.readInt();
        this._output.println("\tTerm legth: " + termLength);
        final int spineLength = this._indexFile.readInt();
        this._output.println("\tIndex Spine length: " + spineLength);
        final int emptyBucketCount = this._indexFile.readInt();
        this._output.println("\tCount of empty buckets: " + emptyBucketCount);
        final int countOfEntries = this._indexFile.readInt();
        this._output.println("\tCount of Index Entries: " + countOfEntries);
        final byte[] term = new byte[termLength];
        for (int i = 0; i < spineLength; ++i) {
            final int bucketSize = this._indexFile.readInt();
            this._output.println("\t\tBucket #" + i + " size: " + bucketSize);
            for (int j = 0; j < bucketSize; ++j) {
                if (allCommonTerms) {
                    final int colMask = this._indexFile.readInt();
                    this._indexFile.readFully(term);
                    this._output.println("\t\t\tColumn Mask: " + colMask + " Term: " + bytes2hex(term));
                }
                else {
                    final int row = this._indexFile.readInt();
                    final int col = this._indexFile.readByte();
                    this._indexFile.readFully(term);
                    this._output.println("\t\t\tRow: " + row + " Col: " + col + " Term: " + bytes2hex(term));
                }
            }
        }
        this._output.println("============================");
    }
    
    public void close() throws IOException {
        this._indexFile.close();
        this._output.close();
    }
    
    public static void main(final String[] args) throws IOException {
        IndexFilePrinter pr = null;
        if (args.length == 0) {
            System.out.println("Usage: ramIndexFile outputFile");
            System.exit(-1);
        }
        else if (args.length == 1) {
            pr = new IndexFilePrinter(args[0]);
        }
        else {
            pr = new IndexFilePrinter(args[0], args[1]);
        }
        pr.printIndex();
        pr.close();
    }
    
    public static String bytes2hex(final byte[] b) {
        final StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(Integer.toString(b[i] & 0xFF, 16)).append(" ");
        }
        return sb.toString();
    }
}
