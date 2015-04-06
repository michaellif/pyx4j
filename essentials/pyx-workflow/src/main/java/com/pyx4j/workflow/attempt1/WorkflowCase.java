package com.pyx4j.workflow.attempt1;

import java.util.List;

/**
 * {@link WorkflowCase} is a complete workflow process with a Start-{@link Condition} and associated
 * set of {@link Task}s. The Start-{@link Condition} will be added to a {@link Task} that is set as First.
 * The First and Last {@link Task}s must be explicitly set. An Exception is raised otherwise.
 */
public interface WorkflowCase {
    public static enum CaseStatus {
        Ready, Executing, Completed
    }

    public void addTask(Task task);

    public void setFirst(Task task);

    public void setLast(Task task);

    public List<Task> getTasks();

    public void start();

    public boolean isExecuting();

    public boolean isComplete();

    public List<Task> getTasksInStatus(Task.TaskStatus status);
}
