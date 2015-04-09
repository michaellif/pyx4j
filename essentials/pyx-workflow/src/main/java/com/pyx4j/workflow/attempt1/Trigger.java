package com.pyx4j.workflow.attempt1;

/**
 * {@link Trigger} is a handler of an external event that has the ability to trigger a {@link Task} execution
 * provided the associated conditions are met. The following {@link Trigger} types are recognized:
 * Automatic - the event for this type of triggers are fired by {@link Condition}s whenever they become true
 * Event - triggered by some other System events
 * Scheduled - triggered by a timer events
 */
public interface Trigger extends WorkflowEventHandler {
    public static enum TriggerType {
        Automatic, Event, Scheduled
    }

    public TriggerType getType();

    public void handleTask(Task task);

}
