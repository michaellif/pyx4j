package com.pyx4j.workflow.attempt1a;

import com.pyx4j.workflow.attempt1.WorkflowEvent;

/**
 * {@link State} (Place in Petri-net notion) defines a "stable point" of a Workflow Case processing before next {@link Transition} execution.
 * A {@link State} has to be in Ready state before the next {@link Transition} can fire. It may also have associated
 * set of Value Types (Colors in Petri-net notion), to distinguish between different types of {@link Transition}s. {@link State}s are able to fire
 * {@link WorkflowEvent}s when they become Ready.
 */
public interface State {
    boolean isReady();
}
