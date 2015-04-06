package com.pyx4j.workflow.attempt1;

/**
 * {@link Condition} is a predicate that signals a {@link Task} execution. It may also have associated
 * set of Value Types (Colors in Petri-net notion), which may be a robust equivalent to having multiple
 * AND/OR-conditions. {@link Condition}s are able to fire {@link WorkflowEvent}s when they become true.
 */
public interface Condition {
    public static enum SyncType {
        AND, OR
    }

    public static class ValueType {
    }

    public boolean isTrue(); // check for any type

    public boolean isTrue(Class<? extends ValueType> typeClass);

    public void setCondValue(Class<? extends ValueType> typeClass, boolean value);
}
