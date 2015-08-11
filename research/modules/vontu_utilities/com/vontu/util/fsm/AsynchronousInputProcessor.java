// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.fsm;

import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class AsynchronousInputProcessor implements InputProcessor
{
    private static Logger _logger;
    private final ProcessInputsTask _processorTask;
    private final Executor _executor;
    
    public AsynchronousInputProcessor(final InputProcessor delegate, final Executor executor) {
        this._executor = executor;
        this._processorTask = new ProcessInputsTask(delegate);
    }
    
    @Override
    public void processInputs() {
        this._executor.execute(this._processorTask);
    }
    
    static void setLogger(final Logger logger) {
        AsynchronousInputProcessor._logger = logger;
    }
    
    static {
        AsynchronousInputProcessor._logger = Logger.getLogger(AsynchronousInputProcessor.class.getName());
    }
    
    private static class ProcessInputsTask implements Runnable
    {
        private final InputProcessor _inputProcessor;
        
        ProcessInputsTask(final InputProcessor inputProcessor) {
            this._inputProcessor = inputProcessor;
        }
        
        @Override
        public void run() {
            try {
                this._inputProcessor.processInputs();
            }
            catch (RejectedExecutionException e) {
                AsynchronousInputProcessor._logger.log(Level.INFO, "Unable to submit a task for execution.  This can happen in the normal course of events when the server is being shut down.", e);
            }
            catch (Throwable e2) {
                AsynchronousInputProcessor._logger.log(Level.SEVERE, "Failed to process state machine inputs", e2);
            }
        }
    }
}
