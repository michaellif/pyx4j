package com.pyx4j.workflow.attempt1a;

import java.util.List;

import com.pyx4j.workflow.attempt1.Trigger;
import com.pyx4j.workflow.attempt1.WorkflowCase;

/**
 * {@link Transition} is a the main executable unit in Petri-net. Each {@link Transition} has
 * one or more associated {@link State}s (Places in Petri-net notion) of the Workflow Case. When {@link Transition} is executed, it moves the process from one
 * of the Input {@link State}s to one of the Output ones.
 * A {@link Transition} will also have one or more {@link Trigger}s that have the ability to actually fire the {@link Transition}.
 * In order to execute a {@link Transition} one (or all) of its Input {@link State}s have to be in Ready condition and one of the (@link Trigger}s has to fire.
 * The following statuses are recognized:
 * - Pending - when the {@link WorkflowCase} starts, each {@link Transition} is set to this status
 * - Ready - when all required Input {@link State}s are ready, but before being triggered, a {@link Transition} will move to this status
 * - Triggered - once a {@link Trigger} calls {@link Transition#execute()}, but before it completes
 * - Completed - after the {@link Transition} completes but before it becomes Pending again
 */
public interface Transition {

    public static enum TransitionStatus {
        Pending, Ready, Triggered, Completed
    }

    void fromState(State state);

    void toState(State state);

    List<State> getFromList();

    List<State> getToList();

    TransitionStatus getStatus();

    void execute();

}
