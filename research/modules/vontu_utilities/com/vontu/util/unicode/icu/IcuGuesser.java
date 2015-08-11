// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.icu;

import com.vontu.util.unicode.UnsupportedEncodingException;
import com.vontu.util.unicode.CharacterConversionException;
import java.util.logging.Level;
import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetDetector;
import com.vontu.util.unicode.CharacterEncodingManager;
import java.util.Properties;
import com.vontu.util.unicode.CharacterEncoding;
import java.util.logging.Logger;

public class IcuGuesser implements Guesser
{
    private static final Logger _logger;
    private String _defaultEncodingStr;
    private CharacterEncoding _defaultEncoding;
    private int _minConfidence;
    
    public IcuGuesser() {
        this(new Properties());
    }
    
    public IcuGuesser(final Properties properties) {
        this._defaultEncodingStr = "ISO-8859-1";
        try {
            this._minConfidence = Integer.parseInt(properties.getProperty("MinimumConfidence", "50"));
        }
        catch (NumberFormatException e) {
            this._minConfidence = 50;
        }
        this._defaultEncodingStr = properties.getProperty("DefaultEncoding", "ISO-8859-1");
        this._defaultEncoding = null;
    }
    
    @Override
    public BestGuessResult guessEncoding(final byte[] originalMessage) {
        if (this._defaultEncoding == null) {
            this._defaultEncoding = CharacterEncodingManager.getInstance().getEncoding(this._defaultEncodingStr);
            if (this._defaultEncoding == null) {
                IcuGuesser._logger.warning("Bad default encoding: " + this._defaultEncodingStr + " - Will use " + "ISO-8859-1");
                this._defaultEncodingStr = "ISO-8859-1";
                this._defaultEncoding = CharacterEncodingManager.getInstance().getEncoding(this._defaultEncodingStr);
            }
        }
        if (originalMessage == null || originalMessage.length == 0) {
            return NotGuessingGuesser.convertUnguessed(new byte[0], this._defaultEncoding);
        }
        final CharsetDetector detector = new CharsetDetector();
        detector.setText(originalMessage);
        CharsetMatch[] matches;
        try {
            matches = detector.detectAll();
        }
        catch (Exception e) {
            IcuGuesser._logger.fine("ICU threw Exception: " + e.getMessage());
            matches = new CharsetMatch[0];
        }
        BestGuessResult result = null;
        for (final CharsetMatch match : matches) {
            if (match.getConfidence() < this._minConfidence) {
                break;
            }
            result = this.decodeUsingCharset(originalMessage, match);
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            IcuGuesser._logger.fine("No useful encoding guessed. Using default: " + this._defaultEncodingStr);
            return NotGuessingGuesser.convertUnguessed(originalMessage, this._defaultEncoding);
        }
        if (IcuGuesser._logger.isLoggable(Level.FINER)) {
            IcuGuesser._logger.finer("Guessed good encoding: " + result.getEncoding().getCharacterSet());
        }
        return result;
    }
    
    private BestGuessResult decodeUsingCharset(final byte[] originalMessage, final CharsetMatch match) {
        final CharacterEncoding guessedEncoding = CharacterEncodingManager.getInstance().getEncoding(match.getName());
        if (guessedEncoding == null) {
            IcuGuesser._logger.fine("Unknown encoding: " + match.getName());
            return null;
        }
        try {
            return new BestGuessResult(guessedEncoding, originalMessage, guessedEncoding.convert(originalMessage), match.getConfidence(), false);
        }
        catch (CharacterConversionException e) {
            IcuGuesser._logger.fine("Error decoding guessed encoding: " + match.getName());
            return null;
        }
        catch (UnsupportedEncodingException e2) {
            IcuGuesser._logger.fine("Unsupported encoding: " + match.getName());
            return null;
        }
    }
    
    @Override
    public String getBackupEncoding() {
        return this._defaultEncodingStr;
    }
    
    int getMinimumConfidence() {
        return this._minConfidence;
    }
    
    @Override
    public String toString() {
        return "Default: " + this._defaultEncodingStr + "; " + "MinConfidence: " + this._minConfidence;
    }
    
    @Override
    public String getName() {
        return "Guesser using ICU, fall-back: " + this._defaultEncodingStr;
    }
    
    static {
        _logger = Logger.getLogger(IcuGuesser.class.getName());
    }
}
