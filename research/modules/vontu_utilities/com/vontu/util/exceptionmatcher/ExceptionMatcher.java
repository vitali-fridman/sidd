// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.exceptionmatcher;

import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionMatcher
{
    private ExceptionMatcherConfig _config;
    private Logger _logger;
    
    public ExceptionMatcher(final ExceptionMatcherConfig config) {
        this(config, Logger.getLogger(ExceptionMatcher.class.getName()));
    }
    
    public ExceptionMatcher(final ExceptionMatcherConfig config, final Logger logger) {
        this._config = config;
        this._logger = logger;
    }
    
    public boolean isMatch(final Throwable t) {
        return null != this.match(t);
    }
    
    public Throwable match(final Throwable t) {
        for (Throwable item = t; item != null; item = item.getCause()) {
            if (this.matchPatterns(item)) {
                return item;
            }
        }
        return null;
    }
    
    private boolean matchPatterns(final Throwable t) {
        final ArrayList<Pattern> list = this._config.getPatterns(t.getClass().getName());
        this._logger.log(Level.FINE, "Checking exception " + t.getClass().getName() + ".");
        if (list != null) {
            for (final Pattern pattern : list) {
                this._logger.log(Level.FINE, "Testing pattern " + pattern.pattern() + ".");
                final Matcher m = pattern.matcher(t.getMessage());
                if (m.find()) {
                    return true;
                }
            }
        }
        return false;
    }
}
