// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Collection;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class UnicodeNormalizationConfigLoader
{
    private static final String INTERNAL_CONFIG_FILE = "CustomNormalizationsConfig.txt";
    private final Logger _logger;
    private final List<UnicodeCustomNormalization> _customNormalizations;
    
    private String trim(final String line) {
        final String trimmedLine = line.trim();
        if (trimmedLine.length() == 0 || trimmedLine.charAt(0) == '#') {
            return null;
        }
        return trimmedLine;
    }
    
    private void addExceptions(int start, final int end) {
        while (start <= end) {
            final String strStart = new String(Character.toChars(start));
            this._customNormalizations.add(new UnicodeCustomNormalization(strStart, strStart));
            ++start;
        }
        this._logger.fine("added exception:" + start + "-" + end);
    }
    
    private void loadException(final String exception) throws UnicodeNormalizationConfigLoaderException {
        final String[] tokens = exception.split("-");
        if (tokens.length != 2) {
            throw new UnicodeNormalizationConfigLoaderException("Not a valid customization command", UnicodeNormalizationConfigLoaderException.ErrorType.MALFORMED_LINE);
        }
        final int[] codePointPair = new int[2];
        for (int i = 0; i < 2; ++i) {
            codePointPair[i] = this.getCodePoint(tokens[i].trim());
        }
        final int from = codePointPair[0];
        final int to = codePointPair[1];
        if (from > to) {
            final String errorStr = " Codepoint " + Integer.toString(from, 16) + " is greater than codepoint " + Integer.toString(to, 16);
            throw new UnicodeNormalizationConfigLoaderException(errorStr, UnicodeNormalizationConfigLoaderException.ErrorType.INVALID_RANGE);
        }
        this.addExceptions(from, to);
    }
    
    private void loadCustom(final String custom) throws UnicodeNormalizationConfigLoaderException {
        final String[] tokens = custom.split("-");
        if (tokens.length != 2) {
            throw new UnicodeNormalizationConfigLoaderException("Not a valid customization command", UnicodeNormalizationConfigLoaderException.ErrorType.MALFORMED_LINE);
        }
        final StringBuilder[] normalization = new StringBuilder[2];
        for (int i = 0; i < 2; ++i) {
            final String[] codePoints = tokens[i].trim().split("\\s");
            if (codePoints.length == 0) {
                throw new UnicodeNormalizationConfigLoaderException("At least one code point is needed on either side of the '-'", UnicodeNormalizationConfigLoaderException.ErrorType.MALFORMED_LINE);
            }
            normalization[i] = new StringBuilder();
            for (final String codePoint : codePoints) {
                normalization[i].appendCodePoint(this.getCodePoint(codePoint));
            }
        }
        final String normalizationFrom = normalization[0].toString();
        final String normalizationTo = normalization[1].toString();
        this._logger.log(Level.FINE, "added custom normailization:{0}->{1}", new Object[] { normalizationFrom, normalizationTo });
        this._customNormalizations.add(new UnicodeCustomNormalization(normalizationFrom, normalizationTo));
    }
    
    private int getCodePoint(final String codePoint) throws UnicodeNormalizationConfigLoaderException {
        int codePointInt;
        try {
            codePointInt = Integer.parseInt(codePoint, 16);
        }
        catch (NumberFormatException numberFormatException) {
            final String errorStr = codePoint + " is not a valid hexadecimal Integer ";
            throw new UnicodeNormalizationConfigLoaderException(errorStr, UnicodeNormalizationConfigLoaderException.ErrorType.MALFORMED_LINE);
        }
        if (!Character.isValidCodePoint(codePointInt)) {
            final String errorStr2 = codePoint + " is not a valid Unicode codePoint";
            throw new UnicodeNormalizationConfigLoaderException(errorStr2, UnicodeNormalizationConfigLoaderException.ErrorType.INVALID_CODE_POINT);
        }
        return codePointInt;
    }
    
    public Collection<UnicodeCustomNormalization> getNormalizations() {
        return Collections.unmodifiableCollection((Collection<? extends UnicodeCustomNormalization>)this._customNormalizations);
    }
    
    public void loadLine(final String line) throws UnicodeNormalizationConfigLoaderException {
        final String trimmedLine = this.trim(line);
        if (trimmedLine != null && trimmedLine.length() > 0) {
            switch (trimmedLine.charAt(0)) {
                case 'E': {
                    this.loadException(trimmedLine.substring(1));
                    break;
                }
                case 'C': {
                    this.loadCustom(trimmedLine.substring(1));
                    break;
                }
                default: {
                    throw new UnicodeNormalizationConfigLoaderException("Not a valid customization command", UnicodeNormalizationConfigLoaderException.ErrorType.MALFORMED_LINE);
                }
            }
        }
    }
    
    public void loadInternalFile() throws IOException, UnicodeNormalizationConfigLoaderException {
        final InputStream inputStream = this.getClass().getResourceAsStream("CustomNormalizationsConfig.txt");
        if (inputStream == null) {
            throw new IOException("CustomNormalizationsConfig.txt cannot be opened for read");
        }
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream));
        this.loadStream(reader);
    }
    
    public void loadFile(final String filename) throws IOException, UnicodeNormalizationConfigLoaderException {
        final LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        this.loadStream(reader);
    }
    
    private void loadStream(final LineNumberReader reader) throws IOException, UnicodeNormalizationConfigLoaderException {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                this.loadLine(line);
                continue;
            }
            catch (UnicodeNormalizationConfigLoaderException configException) {
                configException.setLineNumber(reader.getLineNumber());
                throw configException;
            }
        }
    }
    
    public UnicodeNormalizationConfigLoader() {
        this._logger = Logger.getLogger(this.getClass().getName());
        this._customNormalizations = new ArrayList<UnicodeCustomNormalization>();
    }
}
