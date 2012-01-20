package com.pyx4j.workflow.attempt1a.impl;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.workflow.attempt1a.State;
import com.pyx4j.workflow.attempt1a.Transition;

public abstract class TransitionImpl implements Transition {
    protected TransitionStatus status;

    private final List<State> condList;

    private final String name;

    public TransitionImpl(String name) {
        this.name = name;
        condList = new ArrayList<State>();
        status = TransitionStatus.Pending;
    }

    @Override
    public void fromState(State cond) {
        condList.add(cond);
    }

    @Override
    public List<State> getFromStates() {
        return condList;
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
