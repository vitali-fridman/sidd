// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.CharBuffer;
import com.vontu.util.CharArrayCharSequence;
import com.vontu.util.CharArraySlice;
import java.text.CharacterIterator;
import java.util.Iterator;
import java.util.HashMap;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.FallbackSettingProvider;
import com.vontu.util.config.SettingProvider;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.Map;
import com.ibm.icu.text.Normalizer;

public class UnicodeNormalizer implements StringNormalizer
{
    public static final Normalizer.Mode NORMALIZATION_MODE;
    private final Map<Integer, Collection<UnicodeCustomNormalization>> _customNormalizationLookup;
    private final boolean _areCustomNormalizationsLoaded;
    private final boolean _isNormalizationEnabled;
    private final boolean _isNewlineEliminationEnabled;
    private final Logger _logger;
    public static final int MAX_NORMALIZATION_MULTIPLIER = 4;
    
    public UnicodeNormalizer() {
        this(new DefaultSettings(), new ArrayList<UnicodeCustomNormalization>());
    }
    
    public UnicodeNormalizer(final SettingProvider settings, final Collection<UnicodeCustomNormalization> customNormalizations) {
        this._logger = Logger.getLogger(this.getClass().getName());
        final SettingReader settingReader = new SettingReader(new FallbackSettingProvider(settings, new DefaultSettings()));
        this._isNormalizationEnabled = settingReader.getBooleanSetting("UnicodeNormalizer.Enabled");
        this._isNewlineEliminationEnabled = settingReader.getBooleanSetting("UnicodeNormalizer.NewlineEliminationEnabled");
        this._areCustomNormalizationsLoaded = !customNormalizations.isEmpty();
        this._customNormalizationLookup = (this._areCustomNormalizationsLoaded ? load(customNormalizations) : null);
    }
    
    private static Map<Integer, Collection<UnicodeCustomNormalization>> load(final Collection<UnicodeCustomNormalization> customNormalizations) {
        final Map<Integer, Collection<UnicodeCustomNormalization>> customNormalizationLookup = new HashMap<Integer, Collection<UnicodeCustomNormalization>>();
        for (final UnicodeCustomNormalization customNormalization : customNormalizations) {
            final String from = customNormalization.getFrom();
            final int firstCodePoint = Character.codePointAt(from, 0);
            Collection<UnicodeCustomNormalization> sameCharNormalizations = customNormalizationLookup.get(firstCodePoint);
            if (sameCharNormalizations == null) {
                sameCharNormalizations = new ArrayList<UnicodeCustomNormalization>();
                customNormalizationLookup.put(firstCodePoint, sameCharNormalizations);
            }
            sameCharNormalizations.add(customNormalization);
        }
        return customNormalizationLookup;
    }
    
    private static UnicodeCustomNormalization matchAhead(final CharSequence src, final int startPos, final Collection<UnicodeCustomNormalization> customNormalizations) {
        for (final UnicodeCustomNormalization customNormalization : customNormalizations) {
            if (customNormalization.match(src, startPos)) {
                return customNormalization;
            }
        }
        return null;
    }
    
    private CharSequence customNormalize(final CharSequence src) {
        StringBuilder normalized = null;
        int prev = 0;
        int currPos = 0;
        while (currPos < src.length()) {
            final int currCodePoint = Character.codePointAt(src, currPos);
            final Collection<UnicodeCustomNormalization> customNormalizations = this._customNormalizationLookup.get(currCodePoint);
            UnicodeCustomNormalization matchingNormalization = null;
            if (customNormalizations != null) {
                matchingNormalization = matchAhead(src, currPos, customNormalizations);
                if (matchingNormalization != null) {
                    if (normalized == null) {
                        normalized = new StringBuilder();
                    }
                    if (prev != currPos) {
                        normalized.append(this.standardNormalize(src.subSequence(prev, currPos)));
                    }
                    normalized.append(matchingNormalization.getTo());
                    currPos = (prev = currPos + matchingNormalization.getFrom().length());
                }
            }
            if (matchingNormalization == null) {
                ++currPos;
                if (!Character.isSupplementaryCodePoint(currCodePoint)) {
                    continue;
                }
                ++currPos;
            }
        }
        final CharSequence remainingStr = this.standardNormalize(src.subSequence(prev, src.length()));
        if (normalized == null) {
            return remainingStr;
        }
        normalized.append(remainingStr);
        return normalized;
    }
    
    private CharSequence standardNormalize(final CharSequence src) {
        if (src.length() == 0) {
            return src;
        }
        final CharArraySlice charArray = createCharArrayProvider(src).getCharArray();
        if (Normalizer.quickCheck(charArray.allChars(), charArray.start(), charArray.end(), UnicodeNormalizer.NORMALIZATION_MODE, 0) == Normalizer.YES) {
            return src;
        }
        final Normalizer normalizer = new Normalizer((CharacterIterator)new CharSequenceIterator(src), UnicodeNormalizer.NORMALIZATION_MODE, 0);
        final int maxNormalizedLength = 4 * src.length();
        final StringBuilder builder = new StringBuilder(normalizer.getLength());
        try {
            for (int codePoint = normalizer.first(); codePoint != -1; codePoint = normalizer.next()) {
                builder.appendCodePoint(codePoint);
                if (builder.length() > maxNormalizedLength) {
                    this._logger.fine("Normalized content too long, using non-normalized content ");
                    return src;
                }
            }
            return builder;
        }
        catch (IndexOutOfBoundsException e) {
            final String javaNormalizedString = java.text.Normalizer.normalize(src, java.text.Normalizer.Form.NFKC);
            this._logger.fine("Caught IndexOutOfBoundsException at ICU, Unicode-normalized with Java instead");
            return javaNormalizedString;
        }
    }
    
    static CharArrayProvider createCharArrayProvider(final CharSequence src) {
        if (src instanceof StringBuilder) {
            return new StringBuilderCharArrayConverter((StringBuilder)src);
        }
        if (src instanceof String) {
            return new StringCharArrayConverter((String)src);
        }
        if (src instanceof CharArrayCharSequence) {
            return new CharArrayCharSequenceCharArrayProvider((CharArrayCharSequence)src);
        }
        if (src instanceof CharBuffer) {
            return new CharBufferCharArrayConverter((CharBuffer)src);
        }
        return new CharSequenceCharArrayConverter(src);
    }
    
    private static boolean isNewline(final int codePoint) {
        if (codePoint == 10 || codePoint == 13) {
            return true;
        }
        final int type = Character.getType(codePoint);
        return type == 14 || type == 13;
    }
    
    CharSequence eliminateNewlines(final CharSequence src) {
        if (!this._isNewlineEliminationEnabled) {
            return src;
        }
        final AsianTokenUtil asianTokenUtil = UnicodeNormalizerRegistry.getAsianTokenUtils();
        boolean consumingNewlines = false;
        final StringBuilder outString = new StringBuilder(src.length());
        final StringBuilder newlineBuffer = new StringBuilder();
        int currPos = 0;
        while (currPos < src.length()) {
            final int currCodePoint = Character.codePointAt(src, currPos);
            ++currPos;
            if (Character.isSupplementaryCodePoint(currCodePoint)) {
                ++currPos;
            }
            if (consumingNewlines && isNewline(currCodePoint)) {
                newlineBuffer.appendCodePoint(currCodePoint);
            }
            else {
                if (asianTokenUtil.isAsianCodePoint(currCodePoint)) {
                    consumingNewlines = true;
                }
                else {
                    consumingNewlines = false;
                    if (newlineBuffer.length() > 0) {
                        outString.append((CharSequence)newlineBuffer);
                    }
                }
                if (newlineBuffer.length() > 0) {
                    newlineBuffer.delete(0, newlineBuffer.length());
                }
                outString.appendCodePoint(currCodePoint);
            }
        }
        if (newlineBuffer.length() > 0) {
            outString.append((CharSequence)newlineBuffer);
        }
        return outString;
    }
    
    @Override
    public CharSequence normalize(final CharSequence src) {
        return this.normalize(src, true);
    }
    
    public CharSequence normalize(final CharSequence src, final boolean newlineElimination) {
        if (src == null) {
            return src;
        }
        final CharSequence normalized = this.checkAndNormalize(src);
        return newlineElimination ? this.eliminateNewlines(normalized) : normalized;
    }
    
    private CharSequence checkAndNormalize(final CharSequence src) {
        if (!this._isNormalizationEnabled) {
            return src;
        }
        return this._areCustomNormalizationsLoaded ? this.customNormalize(src) : this.standardNormalize(src);
    }
    
    static {
        NORMALIZATION_MODE = Normalizer.NFKC;
    }
}
