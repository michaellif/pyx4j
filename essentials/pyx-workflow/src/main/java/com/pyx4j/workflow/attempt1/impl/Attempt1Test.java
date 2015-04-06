package com.pyx4j.workflow.attempt1.impl;

import com.pyx4j.workflow.attempt1.WorkflowEvent;
import com.pyx4j.workflow.attempt1.WorkflowManager;

public class Attempt1Test {

    public static class TestTask extends TaskImpl {
        public TestTask(String name) {
            super(name);
        }

        @Override
        public void execute() {
            System.out.println("Task: Executing " + getName() + "...");
            setCondValue(ValueType.class, true);
            status = TaskStatus.Completed;
            WorkflowManager.fireEvent(new WorkflowEvent());
        }
    }

    public static void main(String[] args) {
        // create workflow case
        final WorkflowCaseImpl myCase = new WorkflowCaseImpl() {
            @Override
            public void start() {
                System.out.println("Case started...");
                super.start();
            }
        };
        // define tasks
        TestTask A = new TestTask("A");
        TestTask B = new TestTask("B");
        TestTask C = new TestTask("C");
        // add conditions
        B.addCondition(A);
        C.addCondition(B);
        // assemble the case
        myCase.setFirst(A);
        myCase.addTask(B);
        myCase.setLast(C);
        // add task triggers
        AutomaticTrigger trigger = new AutomaticTrigger();
        trigger.handleTask(B);
        trigger.handleTask(C);
        WorkflowManager.addWorkflowEventHandler(trigger);
        // start the case
        myCase.start();

        // set up the case watch
        Thread caseWatch = new Thread() {
            @Override
            public void run() {
                int count = 0;
                boolean success = true;
                while (!myCase.isComplete()) {
                    try {
                        System.out.print(".");
                        sleep(10);
                        if (++count > 5) {
                            throw new Exception("Timeout");
                        }
                    } catch (Exception e) {
                        System.out.println("Exit by Exception.... " + e.getMessage());
                        success = false;
                        break;
                    }
                }
                if (success) {
                    System.out.println("Case Completed....");
                }
            }
        };
        caseWatch.start();
    }
}
