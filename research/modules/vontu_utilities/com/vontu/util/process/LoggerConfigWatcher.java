// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import com.vontu.util.observer.Observable;
import java.util.Iterator;
import java.text.MessageFormat;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.Timer;
import java.util.logging.Level;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import com.vontu.util.filesystem.FileObservable;
import java.io.File;
import java.util.logging.Logger;
import com.vontu.util.filesystem.FileObserver;

public class LoggerConfigWatcher implements FileObserver
{
    private static final Logger logger;
    public static final String DEFAULT_LOGGING_SYSTEM_PROPERTY = "java.util.logging.config.file";
    private final File configurationFileBeingMonitored;
    private final FileObservable fileObservable;
    private final Collection<LoggerConfigChangeNotifiable> logConfigObservers;
    
    public LoggerConfigWatcher() {
        this(System.getProperty("java.util.logging.config.file"));
    }
    
    public LoggerConfigWatcher(final String configFilePath) {
        this.logConfigObservers = (Collection<LoggerConfigChangeNotifiable>)Collections.synchronizedSet(new HashSet<LoggerConfigChangeNotifiable>());
        if (configFilePath == null) {
            LoggerConfigWatcher.logger.log(Level.INFO, "The configuration file path for the configuration watcher was null. Hence monitoring is disabled.");
            this.configurationFileBeingMonitored = null;
            this.fileObservable = null;
        }
        else {
            this.configurationFileBeingMonitored = new File(configFilePath);
            ((this.fileObservable = new FileObservable(this.configurationFileBeingMonitored))).addObserver(this);
        }
    }
    
    public LoggerConfigWatcher(final File file, final FileObservable fileObservable) {
        this.logConfigObservers = (Collection<LoggerConfigChangeNotifiable>)Collections.synchronizedSet(new HashSet<LoggerConfigChangeNotifiable>());
        this.configurationFileBeingMonitored = file;
        ((this.fileObservable = fileObservable)).addObserver(this);
    }
    
    public void start() {
        this.start(new Timer("LoggerConfigWatcher", true));
    }
    
    public void start(final Timer timer) {
        if (this.fileObservable != null) {
            final File file = this.fileObservable.getFile();
            LoggerConfigWatcher.logger.info("Logger configuration watcher is monitoring " + file + ".");
            this.fileObservable.start(timer);
        }
    }
    
    public void start(final Timer timer, final long checkPeriod) {
        if (this.fileObservable != null) {
            final File file = this.fileObservable.getFile();
            LoggerConfigWatcher.logger.info("Logger configuration watcher is monitoring " + file + " with file reloading frequency of " + checkPeriod + " milliseconds.");
            this.fileObservable.start(timer, checkPeriod);
        }
    }
    
    public void stop() {
        if (this.fileObservable != null) {
            LoggerConfigWatcher.logger.fine("Stopping LoggerConfigWatcher for file " + this.fileObservable.getFile() + ".");
            this.fileObservable.stop();
        }
    }
    
    public void addLogConfigNotifiable(final LoggerConfigChangeNotifiable loggerConfigChangeNotifiable) {
        this.logConfigObservers.add(loggerConfigChangeNotifiable);
    }
    
    public boolean removeLogConfigNotifiable(final LoggerConfigChangeNotifiable loggerConfigChangeNotifiable) {
        return this.logConfigObservers.remove(loggerConfigChangeNotifiable);
    }
    
    @Override
    public void fileModified() {
        final boolean isReloadSuccessful = this.reloadLogConfiguration();
        if (isReloadSuccessful) {
            this.notifyLogConfigObservers();
        }
    }
    
    private boolean reloadLogConfiguration() {
        final File configurationFile = this.fileObservable.getFile();
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(configurationFile));
            LoggerConfigWatcher.logger.info("Logger configuration reloaded from " + configurationFile + ".");
            return true;
        }
        catch (Throwable t) {
            final String message = MessageFormat.format("Unable to re-load logger configuration from {0}.", configurationFile);
            LoggerConfigWatcher.logger.log(Level.WARNING, message, t);
            return false;
        }
    }
    
    private void notifyLogConfigObservers() {
        synchronized (this.logConfigObservers) {
            for (final LoggerConfigChangeNotifiable notifiable : this.logConfigObservers) {
                this.notify(notifiable);
            }
        }
    }
    
    private void notify(final LoggerConfigChangeNotifiable notifiable) {
        try {
            if (LoggerConfigWatcher.logger.isLoggable(Level.FINE)) {
                LoggerConfigWatcher.logger.log(Level.FINE, "Notifiying " + notifiable.getLoggingConfigChangeNotifiableDebugName() + ".");
            }
            notifiable.onLoggingPropertiesUpdated(this.configurationFileBeingMonitored);
        }
        catch (Throwable t) {
            LoggerConfigWatcher.logger.log(Level.SEVERE, "Unexpected exception while notifiying " + notifiable.getLoggingConfigChangeNotifiableDebugName() + ".", t);
        }
    }
    
    static {
        logger = Logger.getLogger(LoggerConfigWatcher.class.getName());
    }
}
