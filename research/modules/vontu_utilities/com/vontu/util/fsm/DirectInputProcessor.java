// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.fsm;

public class DirectInputProcessor<I, O> implements InputProcessor
{
    private final StateHolder<I, O> _stateHolder;
    private final O _output;
    private final I _input;
    
    public DirectInputProcessor(final I input, final O output, final StateHolder<I, O> stateHolder) {
        this._stateHolder = stateHolder;
        this._output = output;
        this._input = input;
    }
    
    @Override
    public void processInputs() {
        this._stateHolder.getState().processInputs(this._input, this._output, this._stateHolder);
    }
    
    String getState() {
        return this._stateHolder.getStateName();
    }
}
