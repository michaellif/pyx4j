package com.pyx4j.workflow.attempt1;

import java.util.List;

/**
 * {@link Task} is a the main executable unit referred as Transition in Petri-net. Each {@link Task} has
 * an associated set of {@link Condition}s (Places in Petri-net notion) that have to be met before
 * it starts execution, and one or more {@link Trigger}s that have the ability to actually fire the {@link Task} provided all required Conditions are met. Each
 * {@link Task} can also pose itself as a {@link Condition} so be a prerequisite for execution of other {@link Task}s.
 * The following statuses are recognized:
 * - Started - when the {@link WorkflowCase} starts, each {@link Task} is set to this status
 * - Ready - when all conditions are met, but before being triggered, a {@link Task} will move to this status
 * - Triggered - once a {@link Trigger} calls {@link Task#execute()}, but before it completes
 * - Completed - after the {@link Task} completes but before it becomes started again
 */
public interface Task extends Condition {

    public static enum TaskStatus {
        New, Started, Ready, Triggered, Completed
    }

    public void addCondition(Condition cond);

    public List<Condition> getConditions();

    public TaskStatus getStatus();

    public void execute();

}
