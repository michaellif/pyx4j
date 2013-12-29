/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 14, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.workflow.attempt2.example;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.workflow.attempt2.Condition;
import com.pyx4j.workflow.attempt2.Place;
import com.pyx4j.workflow.attempt2.Process;
import com.pyx4j.workflow.attempt2.Task;
import com.pyx4j.workflow.attempt2.Transition;
import com.pyx4j.workflow.attempt2.Transition.TriggerType;
import com.pyx4j.workflow.attempt2.WorkflowManager;
import com.pyx4j.workflow.attempt2.domain.Case;
import com.pyx4j.workflow.attempt2.domain.Token;

public class Example1 {

    public static class TestProcess extends Process {
        public TestProcess() {
            super("TestProcess");

            Place<Token1> placeA = registerPlace("placeA");
            Place<Token2> placeB = registerPlace("placeB");
            Place<Token2> placeC = registerPlace("placeC");

            Transition a = registerTransition("transitionA", new TestTask1(), TriggerType.Automatic);
            Transition b = registerTransition("transitionB", new TestTask2(), TriggerType.Event);

            a.input(Token1.class, placeA, new Condition1());
            a.output(Token2.class, placeB, null);
            b.input(Token2.class, placeB, null);
            b.output(Token2.class, placeC, null);

        }

        @Override
        public Token1 createStartToken() {
            return EntityFactory.create(Token1.class);
        }

    }

    public static class TestTask1 implements Task {
        @Override
        public void execute() {
            System.out.println("======= Test Task 1 =======");
        }
    }

    public static class TestTask2 implements Task {
        @Override
        public void execute() {
            System.out.println("======= Test Task 2 =======");
        }
    }

    public static interface Token1 extends Token {
    }

    public static interface Token2 extends Token {
    }

    public static class Condition1 implements Condition<Token1> {

        @Override
        public boolean isValid(Token1 token) {
            return true;
        }
    }

    public static void main(String[] args) {
        WorkflowManager.Instance.registerProcess(new TestProcess());

        Case testCase = WorkflowManager.Instance.openCase("TestProcess");

        WorkflowManager.Instance.triggerCase(testCase);

        WorkflowManager.Instance.triggerCase(testCase);

        WorkflowManager.Instance.closeCase(testCase);

    }
}
