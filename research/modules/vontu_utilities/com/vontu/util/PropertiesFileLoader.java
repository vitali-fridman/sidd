// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.logging.Level;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.vontu.util.concurrent.DaemonThreadFactory;
import com.vontu.util.concurrent.NamedThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.io.File;
import java.util.logging.Logger;

public class PropertiesFileLoader implements Runnable
{
    private static Logger logger;
    private final File propertiesFile;
    private final PropertiesAcceptor propertiesAcceptorToNotify;
    private final ScheduledExecutorService executor;
    private long prevLastModified;
    
    public PropertiesFileLoader(final File propertiesFile, final PropertiesAcceptor propertiesAcceptorToNotify, final int reloadCheckFrequencyInMilliseconds) {
        this.propertiesFile = propertiesFile;
        this.propertiesAcceptorToNotify = propertiesAcceptorToNotify;
        this.executor = createDefaultActivityExecutor();
        this.prevLastModified = propertiesFile.lastModified();
        this.executor.schedule(this, reloadCheckFrequencyInMilliseconds, TimeUnit.MILLISECONDS);
    }
    
    private static ScheduledExecutorService createDefaultActivityExecutor() {
        final ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
        final ThreadFactory namedThreadFactory = new NamedThreadFactory(currentThreadGroup, PropertiesFileLoader.class.getSimpleName());
        final ThreadFactory daemonThreadFactory = new DaemonThreadFactory(namedThreadFactory);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(daemonThreadFactory);
        return executor;
    }
    
    public static Properties loadPropertiesFromFile(final File propertiesFile) throws IOException {
        final Properties properties = new Properties();
        final FileInputStream fis = new FileInputStream(propertiesFile);
        properties.load(fis);
        fis.close();
        return properties;
    }
    
    public synchronized void loadProperties() throws Exception {
        final Properties properties = loadPropertiesFromFile(this.propertiesFile);
        this.propertiesAcceptorToNotify.acceptProperties(properties);
    }
    
    @Override
    public void run() {
        this.reloadIfPropertiesFileModified();
    }
    
    private void reloadIfPropertiesFileModified() {
        final long newLastModified = this.propertiesFile.lastModified();
        if (newLastModified != this.prevLastModified) {
            try {
                this.loadProperties();
            }
            catch (Exception e) {
                PropertiesFileLoader.logger.log(Level.SEVERE, "Failure while reloading the properties file and notifying the properties acceptor.", e);
            }
            this.prevLastModified = newLastModified;
        }
    }
    
    public void shutDown() {
        this.executor.shutdownNow();
    }
    
    static {
        PropertiesFileLoader.logger = Logger.getLogger(PropertiesFileLoader.class.getName());
    }
}
