// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.icu;

import com.vontu.util.unicode.UnsupportedEncodingException;
import com.vontu.util.unicode.CharacterConversionException;
import com.vontu.util.unicode.CharacterEncodingManager;
import java.util.Properties;
import com.vontu.util.unicode.CharacterEncoding;
import java.util.logging.Logger;

public class NotGuessingGuesser implements Guesser
{
    private static final Logger _logger;
    private String _defaultEncodingStr;
    private CharacterEncoding _defaultEncoding;
    
    public NotGuessingGuesser() {
        this(new Properties());
    }
    
    public NotGuessingGuesser(final Properties properties) {
        this._defaultEncodingStr = "ISO-8859-1";
        this._defaultEncodingStr = properties.getProperty("DefaultEncoding", "ISO-8859-1");
        this._defaultEncoding = null;
    }
    
    @Override
    public BestGuessResult guessEncoding(final byte[] originalMessage) {
        if (this._defaultEncoding == null) {
            this._defaultEncoding = CharacterEncodingManager.getInstance().getEncoding(this._defaultEncodingStr);
            if (this._defaultEncoding == null) {
                NotGuessingGuesser._logger.warning("Bad default encoding: " + this._defaultEncodingStr + " - Will use " + "ISO-8859-1");
                this._defaultEncodingStr = "ISO-8859-1";
                this._defaultEncoding = CharacterEncodingManager.getInstance().getEncoding(this._defaultEncodingStr);
            }
        }
        return convertUnguessed(originalMessage, this._defaultEncoding);
    }
    
    public static BestGuessResult convertUnguessed(final byte[] originalMessage, final CharacterEncoding encoding) {
        try {
            return new BestGuessResult(encoding, originalMessage, encoding.convert(originalMessage), 0, true);
        }
        catch (CharacterConversionException e) {
            NotGuessingGuesser._logger.info("Default encoding " + encoding + " failed: " + e.getMessage() + " - returning null.");
            return new BestGuessResult(encoding, originalMessage, null, -1, true);
        }
        catch (UnsupportedEncodingException e2) {
            NotGuessingGuesser._logger.info("Default encoding " + encoding + " failed: " + e2.getMessage() + " - returning null.");
            return new BestGuessResult(encoding, originalMessage, null, -1, true);
        }
    }
    
    @Override
    public String getName() {
        return "Non-Guessing Guesser using " + this._defaultEncodingStr;
    }
    
    @Override
    public String getBackupEncoding() {
        return this._defaultEncodingStr;
    }
    
    static {
        _logger = Logger.getLogger(IcuGuesser.class.getName());
    }
}
