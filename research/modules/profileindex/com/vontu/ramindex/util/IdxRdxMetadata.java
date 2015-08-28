// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.ramindex.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.vontu.nlp.lexer.asiansupport.LexerAsianTokenList;

public class IdxRdxMetadata
{
    private static final int TERM_SIZE = 20;
    private static final int IDX_MARKER_SIZE = 25;
    private static final long IDX_HEADERSIZE_HINT_SIZE = 8L;
    private static final int BUFFER_SIZE = 4096;
    private static final byte MARKER_ASIAN_TOKEN_LIST = 1;
    private static final byte MARKER_END_OF_DATA = -1;
    private static final long RDX_HEADER_MARKER = -1L;
    private LexerAsianTokenList _asianTokenList;
    
    public IdxRdxMetadata() {
        this._asianTokenList = null;
    }
    
    public void setAsianTokenList(final LexerAsianTokenList list) {
        this._asianTokenList = list;
    }
    
    public LexerAsianTokenList getAsianTokenList() {
        return this._asianTokenList;
    }
    
    public void writeIdxFooter(final OutputStream _indexStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(buffer);
        writeIdxMarker(out);
        this.writeAsianTokenFooter(out);
        out.flush();
        final long footerLength = buffer.size();
        out.writeLong(footerLength);
        out.flush();
        buffer.writeTo(_indexStream);
    }
    
    private void writeAsianTokenFooter(final DataOutputStream out) throws IOException {
        if (this._asianTokenList == null || this._asianTokenList.isEmpty()) {
            return;
        }
        out.writeByte(1);
        this._asianTokenList.writeToStream((OutputStream)out);
        out.writeByte(-1);
    }
    
    public static void writeIdxMarker(final DataOutputStream out) throws IOException {
        for (int i = 0; i < 20; ++i) {
            out.writeByte(0);
        }
        out.writeInt(0);
        out.writeByte(0);
        out.writeByte(-1);
    }
    
    public static IdxRdxMetadata loadRdxHeaderFromStream(final InputStream in) throws IOException {
        IdxRdxMetadata footer = null;
        final DataInputStream din = new DataInputStream(in);
        try {
            final long marker = din.readLong();
            if (marker != -1L) {
                return null;
            }
            footer = new IdxRdxMetadata();
            byte type;
            do {
                type = din.readByte();
                switch (type) {
                    case 1: {
                        final LexerAsianTokenList list = new LexerAsianTokenList((InputStream)din);
                        footer.setAsianTokenList(list);
                        continue;
                    }
                    case -1: {
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("Invalid type: " + type);
                    }
                }
            } while (type != -1);
            return footer;
        }
        catch (EOFException e) {
            return footer;
        }
    }
    
    public static boolean isIdxFooterMarker(final byte[] data, final int row, final int col) {
        return row == 0 && col == 0 && data.length == 20 && data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0 && data[4] == 0 && data[5] == 0 && data[6] == 0 && data[7] == 0 && data[8] == 0 && data[9] == 0 && data[10] == 0 && data[11] == 0 && data[12] == 0 && data[13] == 0 && data[14] == 0 && data[15] == 0 && data[16] == 0 && data[17] == 0 && data[18] == 0 && data[19] == 0;
    }
    
    public static void copyIdxFooterToRdxHeader(final OutputStream out, final File idx) throws IOException {
        final long footerStart = findFooterStart(idx);
        if (footerStart == -1L) {
            return;
        }
        final long headerLength = idx.length() - footerStart - 8L;
        if (headerLength <= 0L) {
            return;
        }
        final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(idx), 4096));
        skipNBytes(in, footerStart);
        final DataOutputStream dout = new DataOutputStream(out);
        dout.writeLong(-1L);
        final byte[] buffer = new byte[4096];
        long remainder = headerLength;
        do {
            final int read = in.read(buffer);
            dout.write(buffer, 0, Math.min((int)remainder, read));
            remainder = Math.max(0L, remainder - read);
        } while (remainder > 0L);
        dout.flush();
    }
    
    public static long findFooterStart(final File idx) throws IOException {
        DataInputStream in = null;
        try {
            long fileOffset = idx.length() - 8L;
            if (fileOffset < 0L) {
                return -1L;
            }
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(idx), 4096));
            skipNBytes(in, fileOffset);
            try {
                final long footerSize = in.readLong();
                in.close();
                in = null;
                if (footerSize + 8L > idx.length() || footerSize < 25L) {
                    return -1L;
                }
                fileOffset = idx.length() - 8L - footerSize;
                in = new DataInputStream(new BufferedInputStream(new FileInputStream(idx), 4096));
                skipNBytes(in, fileOffset);
                for (int i = 0; i < 25; ++i) {
                    if (in.readByte() != 0) {
                        return -1L;
                    }
                }
                if (in.readByte() != -1) {
                    return -1L;
                }
            }
            catch (EOFException e) {
                return -1L;
            }
            return fileOffset + 25L + 1L;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    private static void skipNBytes(final InputStream in, final long nBytes) throws IOException {
        long remainder = nBytes;
        do {
            remainder -= in.skip(remainder);
        } while (remainder > 0L);
    }
    
    public static void skipPastRdxHeaderMarker(DataInputStream in) throws IOException {
        if (!in.markSupported()) {
            in = new DataInputStream(new BufferedInputStream(in));
        }
        in.mark(16);
        Label_0056: {
            try {
                final long marker = in.readLong();
                if (marker != -1L) {
                    in.reset();
                    return;
                }
                break Label_0056;
            }
            catch (EOFException e) {
                in.reset();
                return;
            }
            try {
                byte type;
                do {
                    type = in.readByte();
                    switch (type) {
                        case 1: {
                            LexerAsianTokenList.skipInStream((InputStream)in);
                            continue;
                        }
                        case -1: {
                            continue;
                        }
                        default: {
                            throw new IllegalStateException("Invalid type: " + type);
                        }
                    }
                } while (type != -1);
            }
            catch (EOFException ex) {}
        }
    }
}
