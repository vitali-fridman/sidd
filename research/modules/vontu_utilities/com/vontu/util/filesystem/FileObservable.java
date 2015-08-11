// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.logging.Level;
import java.util.TimerTask;
import java.util.Timer;
import com.vontu.util.observer.NullNotifier;
import java.io.File;
import com.vontu.util.observer.Notifier;
import java.util.logging.Logger;
import com.vontu.util.observer.Observable;

public class FileObservable extends Observable<FileObserver>
{
    private static final long CHECK_PERIOD = 5000L;
    private static final Logger logger;
    private final Notifier<FileObserver> nullNotifier;
    private final FileModifiedNotifier fileModifiedNotifier;
    private final FileChangeDetector changeDetector;
    
    public FileObservable(final File file) {
        this.nullNotifier = new NullNotifier<FileObserver>();
        this.fileModifiedNotifier = new FileModifiedNotifier();
        this.changeDetector = new FileChangeDetector(file, this);
    }
    
    public File getFile() {
        return this.changeDetector.getFile();
    }
    
    public void start() {
        this.start(new Timer("FileObservable", true));
    }
    
    public void start(final Timer timer) {
        timer.schedule(this.changeDetector, 0L, 5000L);
    }
    
    public void start(final Timer timer, final long checkPeriod) {
        timer.schedule(this.changeDetector, 0L, checkPeriod);
    }
    
    public void stop() {
        this.changeDetector.cancel();
    }
    
    @Override
    protected Notifier<FileObserver> getNewObserverNotifier() {
        return this.nullNotifier;
    }
    
    private void notifyFileModified() {
        this.executeForEachObserver(this.fileModifiedNotifier);
    }
    
    static {
        logger = Logger.getLogger(FileObservable.class.getName());
    }
    
    private static class FileModifiedNotifier implements Notifier<FileObserver>
    {
        @Override
        public void notify(final FileObserver observer) {
            observer.fileModified();
        }
    }
    
    private static class FileChangeDetector extends TimerTask
    {
        private long lastModified;
        private final File file;
        private final FileObservable fileObservable;
        
        FileChangeDetector(final File file, final FileObservable fileObservable) {
            this.lastModified = System.currentTimeMillis();
            this.file = file;
            this.fileObservable = fileObservable;
        }
        
        @Override
        public void run() {
            try {
                if (this.file.lastModified() > this.lastModified) {
                    this.lastModified = this.file.lastModified();
                    this.fileObservable.notifyFileModified();
                }
            }
            catch (Throwable t) {
                final String message = "An error occurred while monitoring a file: " + this.file;
                FileObservable.logger.log(Level.WARNING, message, t);
            }
        }
        
        public File getFile() {
            return this.file;
        }
    }
}
