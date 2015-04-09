package com.pyx4j.workflow.attempt1a.impl;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.workflow.attempt1a.Connector;
import com.pyx4j.workflow.attempt1a.State;
import com.pyx4j.workflow.attempt1a.Transition;

public abstract class TransitionImpl implements Transition {
    protected TransitionStatus status;

    private final List<State> fromStateList;

    private final List<State> toStateList;

    private final Connector in;

    private final Connector out;

    private final String name;

    public TransitionImpl(String name) {
        this.name = name;
        fromStateList = new ArrayList<State>();
        toStateList = new ArrayList<State>();
        in = new ConnectorImpl();
        out = new ConnectorImpl();
        status = TransitionStatus.Pending;
    }

    @Override
    public void fromState(State state) {
        fromStateList.add(state);
        // TODO attach connectors
    }

    @Override
    public List<State> getFromList() {
        return fromStateList;
    }

    @Override
    public void toState(State state) {
        toStateList.add(state);
        // TODO attach connectors
    }

    @Override
    public List<State> getToList() {
        return toStateList;
    }

    @Override
    public TransitionStatus getStatus() {
        return status;
    }

    @Override
    public abstract void execute();

    public String getName() {
        return name;
    }
}
