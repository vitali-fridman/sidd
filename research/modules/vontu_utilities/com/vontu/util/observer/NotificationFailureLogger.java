// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.observer;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationFailureLogger<Observer> implements NotificationFailureHandler<Observer>
{
    private final Logger _logger;
    private final Level _logLevel;
    
    public NotificationFailureLogger() {
        this(Logger.getLogger(NotificationFailureLogger.class.getName()), Level.SEVERE);
    }
    
    public NotificationFailureLogger(final Logger logger, final Level logLevel) {
        this._logger = logger;
        this._logLevel = logLevel;
    }
    
    @Override
    public void handleNotificationFailed(final Observer failedObserver, final Throwable error) {
        this._logger.log(this._logLevel, MessageFormat.format("Failed to notify observer {0}", failedObserver), error);
    }
}
