// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.asiansupport;

import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Logger;

public class AsianTokenIdxRdxParser
{
    private static final Logger _logger;
    private static final long BYTES_PER_HASH = 2L;
    private static final long BYTES_PER_LENGTH = 2L;
    private static final long BYTES_PER_EOLL = 2L;
    private static final long BYTES_FOR_DATALENGTH = 8L;
    private static final int MAX_LENGTH = 65519;
    private static final char EOLL = '\uffff';
    private static final char ILV = '\0';
    
    public static long predictOutputLength(final AsianTokenLookupList lookupList) {
        long length = 8L;
        length += lookupList.getSize() * 2L;
        length += lookupList.getSize() * 2L;
        for (final Character c : lookupList.getAllHashedCharacters()) {
            length += lookupList.getLengthList(c, true).size() * 2L;
        }
        return length;
    }
    
    public static void writeLookupList(final OutputStream outputStream, final AsianTokenLookupList lookupList) throws IOException {
        final DataOutputStream out = new DataOutputStream(outputStream);
        final long totalLength = predictOutputLength(lookupList);
        out.writeLong(totalLength - 8L);
        for (final Character c : lookupList.getAllHashedCharacters()) {
            out.writeChar(c);
            for (final Integer length : lookupList.getLengthList(c, true)) {
                char l;
                if (length < 1 || length > 65519) {
                    l = '\0';
                    AsianTokenIdxRdxParser._logger.info("Invalid token length found (ignored): " + length);
                }
                else {
                    l = (char)(length & 0xFFFF);
                }
                out.writeChar(l);
            }
            out.writeChar(65535);
        }
        out.flush();
    }
    
    public static AsianTokenLookupList readLookupList(final InputStream inputStream) throws IOException {
        final DataInputStream input = new DataInputStream(inputStream);
        final AsianTokenLookupList list = new AsianTokenLookupList();
        final long maxLength = input.readLong();
        long count = 0L;
        while (count < maxLength) {
            try {
                final char currentHashed = input.readChar();
                count += 2L;
                boolean eollFound = false;
                while (count < maxLength && !eollFound) {
                    final char length = input.readChar();
                    count += 2L;
                    switch (length) {
                        case '\uffff': {
                            eollFound = true;
                            continue;
                        }
                        case '\0': {
                            continue;
                        }
                        default: {
                            list.addHashedCharacter(currentHashed, length);
                            continue;
                        }
                    }
                }
            }
            catch (EOFException e) {
                break;
            }
        }
        return list;
    }
    
    public static void skipAsianTokenLookupList(final InputStream in) throws IOException {
        final DataInputStream input = new DataInputStream(in);
        try {
            long remainder = input.readLong();
            do {
                remainder -= in.skip(remainder);
            } while (remainder > 0L);
        }
        catch (EOFException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    static {
        _logger = Logger.getLogger(AsianTokenIdxRdxParser.class.getName());
    }
}
