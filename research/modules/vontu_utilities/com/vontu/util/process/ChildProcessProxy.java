// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.OutputStream;
import java.util.TimerTask;
import java.io.IOException;
import java.util.Arrays;
import com.vontu.util.ProtectException;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import com.vontu.util.OutputRedirector;
import java.util.logging.Logger;

public abstract class ChildProcessProxy
{
    protected static final Logger _logger;
    private final ChildProcessMonitor _childProcessMonitor;
    private final String[] _command;
    private int _nativeProcessId;
    private Process _process;
    private final String _shortName;
    private OutputRedirector _stderrRedirector;
    private OutputRedirector _stdoutRedirector;
    private Timer _heartbeatTimer;
    public static final int HEARTBEAT_INTERVAL = 1000;
    
    public ChildProcessProxy(final String shortName, final String[] command) {
        this._shortName = shortName;
        this._command = command.clone();
        this._childProcessMonitor = new ChildProcessMonitor(this);
    }
    
    public ChildProcessProxy(final String shortName, final String command) {
        this(shortName, new String[] { command });
    }
    
    public final void addObserver(final ChildProcessStateObserver observer) {
        this._childProcessMonitor.addObserver(observer);
    }
    
    private synchronized void attach(final Process process) {
        (this._stderrRedirector = new OutputRedirector(process.getErrorStream(), this._shortName)).start();
        (this._stdoutRedirector = new OutputRedirector(process.getInputStream(), this._shortName)).start();
        this._process = process;
        this._childProcessMonitor.observe(process);
    }
    
    public synchronized void destroy() {
        this._stderrRedirector.detach();
        this._stdoutRedirector.detach();
        this.cancelHeartbeat();
        if (!this.killNativeProcess()) {
            this._process.destroy();
        }
        ChildProcessProxy._logger.info("Process " + this._shortName + " is terminated.");
    }
    
    private synchronized boolean ensureShutdown(final int timeoutMsecs) {
        try {
            if (!this._childProcessMonitor.getProcessStateMonitor().waitForShutdown(timeoutMsecs)) {
                ChildProcessProxy._logger.warning("Process " + this._shortName + " failed to shut down within " + TimeUnit.MILLISECONDS.convert(timeoutMsecs, TimeUnit.SECONDS) + " second(s). It will be terminated.");
                this.destroy();
                return false;
            }
        }
        catch (InterruptedException e) {
            ChildProcessProxy._logger.warning("The wait for the process " + this._shortName + " shutdown was interrupted. The process will be terminated immediately.");
            this.destroy();
            return false;
        }
        return true;
    }
    
    public String getShortName() {
        return this._shortName;
    }
    
    public boolean isRunning() {
        Thread.yield();
        return this.isRunning(0);
    }
    
    public boolean isRunning(final int waitMsecs) {
        try {
            return !this._childProcessMonitor.getProcessStateMonitor().waitForShutdown(waitMsecs);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }
    
    private boolean killNativeProcess() {
        if (this._nativeProcessId > 0) {
            try {
                NativeProcessControl.newInstance().kill(this._nativeProcessId);
                return true;
            }
            catch (ProtectException e) {
                final Level level = (e.getMessage() != null && e.getMessage().indexOf("Access is denied") >= 0) ? Level.WARNING : Level.SEVERE;
                ChildProcessProxy._logger.log(level, "Failed to kill process " + this._shortName + ", native PID=" + this._nativeProcessId + ". " + e.getMessage());
            }
        }
        return false;
    }
    
    public void launch() throws IOException, IllegalStateException {
        if (this.isRunning()) {
            throw new IllegalStateException("Process " + this._shortName + " is already running.");
        }
        ChildProcessProxy._logger.fine("Launching process: " + Arrays.toString(this._command));
        final ProcessBuilder processBuilder = new ProcessBuilder(this._command);
        this.attach(processBuilder.start());
        this.startHeartbeat(1000L);
    }
    
    public final void removeObserver(final ChildProcessStateObserver observer) {
        this._childProcessMonitor.removeObserver(observer);
    }
    
    protected abstract void requestShutdown() throws ProtectException;
    
    public synchronized void setNativeProcessId(final int id) {
        this._nativeProcessId = id;
    }
    
    public boolean shutdown(final int timeoutMsecs) {
        if (!this.isRunning()) {
            return true;
        }
        this.cancelHeartbeat();
        try {
            this.requestShutdown();
        }
        catch (ProtectException e) {
            ChildProcessProxy._logger.log(Level.WARNING, "Process " + this._shortName + " failed to respond to the shutdown request." + " It will be terminated.", e);
            this.destroy();
            return false;
        }
        return this.ensureShutdown(timeoutMsecs);
    }
    
    public void waitForShutdown() throws InterruptedException {
        this._childProcessMonitor.getProcessStateMonitor().waitForShutdown();
        this.cancelHeartbeat();
    }
    
    private synchronized void startHeartbeat(final long interval) {
        if (this._heartbeatTimer == null) {
            (this._heartbeatTimer = new Timer("ChildProcessProxy Heartbeat", true)).schedule(new HeartbeatTask(this._process.getOutputStream()), interval, interval);
        }
    }
    
    public synchronized void cancelHeartbeat() {
        if (this._heartbeatTimer != null) {
            this._heartbeatTimer.cancel();
            this._heartbeatTimer = null;
        }
    }
    
    static {
        _logger = Logger.getLogger(ChildProcessProxy.class.getName());
    }
    
    private static final class HeartbeatTask extends TimerTask
    {
        private final OutputStream _heartbeatStream;
        
        private HeartbeatTask(final OutputStream stream) {
            this._heartbeatStream = stream;
        }
        
        @Override
        public void run() {
            try {
                this._heartbeatStream.write("heartbeat\n".getBytes());
                this._heartbeatStream.flush();
            }
            catch (IOException e) {
                ChildProcessProxy._logger.finer("Exception thrown writing heartbeat " + e.getMessage());
            }
        }
    }
}
