// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.fsm;

import com.vontu.util.observer.Notifier;
import com.vontu.util.observer.Observable;

public class StateHolder<I, O> extends Observable<StateChangeObserver>
{
    private final O _output;
    private volatile State<I, O> _state;
    private volatile String _stateName;
    
    public StateHolder(final StateDescriptor<I, O> initialStateDescriptor, final O output) {
        this._output = output;
        this._stateName = initialStateDescriptor.getName();
        (this._state = initialStateDescriptor.getInstance()).enter(output);
    }
    
    public State<I, O> getState() {
        return this._state;
    }
    
    public String getStateName() {
        return this._stateName;
    }
    
    public void transitionTo(final StateDescriptor<I, O> newStateDescriptor, final I input) {
        this._state.exit(this._output);
        final State<I, O> newState = newStateDescriptor.getInstance();
        newState.enter(this._output);
        this._state = newState;
        this._stateName = newStateDescriptor.getName();
        this.executeForEachObserver(new StateChangeNotifier(this._stateName));
        this._state.processInputs(input, this._output, this);
    }
    
    @Override
    protected Notifier<StateChangeObserver> getNewObserverNotifier() {
        return new StateChangeNotifier(this._stateName);
    }
    
    private static class StateChangeNotifier implements Notifier<StateChangeObserver>
    {
        private final String _stateName;
        
        StateChangeNotifier(final String stateName) {
            this._stateName = stateName;
        }
        
        @Override
        public void notify(final StateChangeObserver stateChangeObserver) {
            stateChangeObserver.handleStateChange(this._stateName);
        }
    }
}
