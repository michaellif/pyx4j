package com.pyx4j.workflow.attempt1.impl;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.workflow.attempt1.Condition;
import com.pyx4j.workflow.attempt1.Task;
import com.pyx4j.workflow.attempt1.Task.TaskStatus;
import com.pyx4j.workflow.attempt1.Trigger;
import com.pyx4j.workflow.attempt1.WorkflowEvent;

public class AutomaticTrigger implements Trigger {
    private final Set<Task> taskList;

    public AutomaticTrigger() {
        taskList = new HashSet<Task>();
    }

    @Override
    public TriggerType getType() {
        return Trigger.TriggerType.Automatic;
    }

    @Override
    public void handleTask(Task task) {
        taskList.add(task);
    }

    @Override
    public void handleEvent(WorkflowEvent evt) {
        System.out.println("Trigger: handling WorkflowEvent...");
        for (Task task : taskList) {
            if (task.getStatus().equals(TaskStatus.Completed)) {
                continue;
            }
            System.out.println("Trigger: checking Conditions for task " + ((TaskImpl) task).getName() + "...");
            boolean ready = true;
            for (Condition cond : task.getConditions()) {
                if (!cond.isTrue()) {
                    ready = false;
                    System.out.println("Trigger: Condition not met for task " + ((TaskImpl) task).getName() + "...");
                    break;
                }
            }
            if (ready) {
                System.out.println("Trigger: executing task " + ((TaskImpl) task).getName() + "...");
                task.execute();
            }
        }
    }

}
