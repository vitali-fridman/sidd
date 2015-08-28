// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Collections;
import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

public class WordNormalizer extends AbstractNormalizer
{
    private static final Logger _logger;
    private static Normalizer _instance;
    private Set _stopWords;
    private static final int MIN_WORD_LENGTH = 2;
    private static final String[] EMPTY_LIST;
    
    public static synchronized Normalizer createInstance(final LexerConfiguration lexerConfiguration) {
        if (WordNormalizer._instance == null) {
            WordNormalizer._instance = new WordNormalizer(lexerConfiguration);
        }
        return WordNormalizer._instance;
    }
    
    public static synchronized void releaseInstance() {
        WordNormalizer._instance = null;
    }
    
    public static synchronized Normalizer getInstance() {
        if (WordNormalizer._instance == null) {
            throw new IllegalStateException("WordNormalizer instance doesn't exist.");
        }
        return WordNormalizer._instance;
    }
    
    private WordNormalizer(final LexerConfiguration lexerConfiguration) {
        this.readStopWords(lexerConfiguration.getStopwordDictionaryFolder(), lexerConfiguration.getStopwordLanguages());
    }
    
    private void readStopWords(final File dictionaryFolder, final String languageCsvs) {
        if (languageCsvs == null) {
            this._stopWords = Collections.EMPTY_SET;
            return;
        }
        if (dictionaryFolder == null) {
            WordNormalizer._logger.warning("Stop word dictionary folder isn't specified. The stopword list will not be loaded.");
            this._stopWords = Collections.EMPTY_SET;
            return;
        }
        final String[] languages = languageCsvs.split(",");
        this._stopWords = new HashSet(1000);
        for (int i = 0; i < languages.length; ++i) {
            final File dictionaryFile = getDictionaryFile(dictionaryFolder, languages[i]);
            WordNormalizer._logger.fine("Loading stopword dictionary from " + dictionaryFile.getAbsolutePath() + '.');
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(dictionaryFile));
            }
            catch (FileNotFoundException e) {
                WordNormalizer._logger.warning("Stop word file " + dictionaryFile.getAbsolutePath() + " not found.");
                continue;
            }
            try {
                for (String word = in.readLine(); word != null; word = in.readLine()) {
                    this._stopWords.add(word);
                }
                in.close();
            }
            catch (IOException e2) {
                WordNormalizer._logger.warning("Error reading stop word file " + dictionaryFile.getAbsolutePath() + '.');
            }
        }
        WordNormalizer._logger.fine("Loaded " + this._stopWords.size() + " stopwords.");
    }
    
    private static File getDictionaryFile(final File dictionarayFolder, final String language) {
        return new File(dictionarayFolder, "stopwords_" + language + ".txt");
    }
    
    @Override
    public String[] normalize(final CharSequence text, final int position, int length) {
        String[] normalized;
        if (length < 2 && isOnlyAsciiChar(text)) {
            normalized = WordNormalizer.EMPTY_LIST;
        }
        else {
            if (length > 2 && text.charAt(length - 2) == '\'' && (text.charAt(length - 1) == 's' || text.charAt(length - 1) == 'S')) {
                length -= 2;
            }
            final StringBuffer normalizedWord = new StringBuffer(length);
            for (int i = position; i < position + length; ++i) {
                this.normalizeChar(normalizedWord, text.charAt(i));
            }
            final String word = normalizedWord.toString();
            if (this._stopWords.contains(word)) {
                return WordNormalizer.EMPTY_LIST;
            }
            normalized = new String[] { word };
        }
        return normalized;
    }
    
    private void normalizeChar(final StringBuffer word, final char character) {
        final char ch = Character.toLowerCase(character);
        switch (ch) {
            case '\'': {
                break;
            }
            case '\u00df': {
                word.append("ss");
                break;
            }
            case '\u00e0':
            case '\u00e1':
            case '\u00e2':
            case '\u00e3':
            case '\u00e5': {
                word.append('a');
                break;
            }
            case '\u00e4':
            case '\u00e6': {
                word.append("ae");
                break;
            }
            case '\u00e7': {
                word.append('c');
                break;
            }
            case '\u00e8':
            case '\u00e9':
            case '\u00ea':
            case '\u00eb': {
                word.append('e');
                break;
            }
            case '\u00ec':
            case '\u00ed':
            case '\u00ee':
            case '\u00ef': {
                word.append('i');
                break;
            }
            case '\u00f1': {
                word.append('n');
                break;
            }
            case '\u00f2':
            case '\u00f3':
            case '\u00f4':
            case '\u00f5':
            case '\u00f8': {
                word.append('o');
                break;
            }
            case '\u00f6': {
                word.append("oe");
                break;
            }
            case '\u00f9':
            case '\u00fa':
            case '\u00fb': {
                word.append('u');
                break;
            }
            case '\u00fc': {
                word.append("ue");
                break;
            }
            case '\u00fd':
            case '\u00ff': {
                word.append('y');
                break;
            }
            default: {
                word.append(ch);
                break;
            }
        }
    }
    
    public static boolean isOnlyAsciiChar(final CharSequence text) {
        for (int i = 0; i < text.length(); ++i) {
            final char c = text.charAt(i);
            if (c < '\0' || c > '\u007f') {
                return false;
            }
        }
        return true;
    }
    
    static {
        _logger = Logger.getLogger(WordNormalizer.class.getName());
        EMPTY_LIST = new String[0];
    }
}
