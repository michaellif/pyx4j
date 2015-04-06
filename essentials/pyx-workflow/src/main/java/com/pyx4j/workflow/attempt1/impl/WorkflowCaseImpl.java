package com.pyx4j.workflow.attempt1.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.workflow.attempt1.Condition;
import com.pyx4j.workflow.attempt1.Task;
import com.pyx4j.workflow.attempt1.Task.TaskStatus;
import com.pyx4j.workflow.attempt1.WorkflowCase;

public class WorkflowCaseImpl implements WorkflowCase {
    private final Set<Task> taskList;

    private Task firstTask;

    private final Set<Task> lastTasks;

    public WorkflowCaseImpl() {
        taskList = new HashSet<Task>();
        lastTasks = new HashSet<Task>();
    }

    @Override
    public void addTask(Task task) {
        taskList.add(task);
    }

    @Override
    public void setFirst(Task task) {
        firstTask = task;
        if (!taskList.contains(task)) {
            addTask(task);
        }
    }

    @Override
    public void setLast(Task task) {
        if (!taskList.contains(task)) {
            addTask(task);
        }
        if (!lastTasks.contains(task)) {
            lastTasks.add(task);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<Task>(taskList);
    }

    @Override
    public void start() {
        if (firstTask == null) {
            throw new RuntimeException("Fist task was not found in the Workflow case.");
        }
        for (Condition cond : firstTask.getConditions()) {
            if (!cond.isTrue()) {
                return;
            }
        }
        firstTask.execute();
    }

    @Override
    public boolean isExecuting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isComplete() {
        for (Task task : lastTasks) {
            if (task.getStatus().equals(TaskStatus.Completed)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getTasksInStatus(TaskStatus status) {
        // TODO Auto-generated method stub
        return null;
    }

}
