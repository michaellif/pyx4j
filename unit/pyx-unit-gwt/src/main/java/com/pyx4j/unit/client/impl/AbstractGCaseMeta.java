/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Timer;

import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.GCaseResultAsyncCallback;
import com.pyx4j.unit.client.GResult;
import com.pyx4j.unit.client.TestAwareExceptionHandler;

public abstract class AbstractGCaseMeta implements GCaseMeta {

    private static final Logger log = LoggerFactory.getLogger(AbstractGCaseMeta.class);

    private final Class<? extends TestCase> caseClass;

    private final String name;

    private static TestAwareExceptionHandler testAwareExceptionHandler;

    public AbstractGCaseMeta(Class<? extends TestCase> caseClass, String name) {
        this.caseClass = caseClass;
        this.name = name;
    }

    @Override
    public String getTestClassName() {
        return caseClass.getName();
    }

    @Override
    public String getTestName() {
        return name;
    }

    static class RunningCase implements UncaughtExceptionHandler {

        long startTime = 0;

        Timer timeoutTimer;

        TestCase instance;

        GCaseResultAsyncCallback callback;

        private UncaughtExceptionHandler oldHandler;

        RunningCase() {
            oldHandler = GWT.getUncaughtExceptionHandler();
            GWT.setUncaughtExceptionHandler(this);
            if (testAwareExceptionHandler != null) {
                testAwareExceptionHandler.delegateExceptionHandler(this);
            }
        }

        /**
         * @return false if test already completed
         */
        boolean dispose() {
            if (instance == null) {
                return false;
            }
            if (instance instanceof TestCaseAccessProtected) {
                try {
                    ((TestCaseAccessProtected) instance).accessProtectedTearDown();
                } catch (Throwable ignore) {
                }
            }
            if (oldHandler != null) {
                GWT.setUncaughtExceptionHandler(oldHandler);
                oldHandler = null;
            }
            if (testAwareExceptionHandler != null) {
                testAwareExceptionHandler.delegateExceptionHandler(null);
            }
            if (timeoutTimer != null) {
                timeoutTimer.cancel();
                timeoutTimer = null;
            }
            if (instance != null) {
                running.remove(instance);
                instance = null;
            }
            return true;
        }

        @Override
        public void onUncaughtException(Throwable t) {
            if (dispose()) {
                long duration = (startTime == 0) ? 0 : System.currentTimeMillis() - startTime;
                log.error("test execution UncaughtException", t);
                callback.onComplete(new GResult(t, duration));
            }
        }
    }

    private static Map<TestCase, RunningCase> running = new HashMap<TestCase, RunningCase>();

    public static void setTestAwareExceptionHandler(TestAwareExceptionHandler testAwareExceptionHandler) {
        AbstractGCaseMeta.testAwareExceptionHandler = testAwareExceptionHandler;
    }

    @Override
    public void execute(GCaseResultAsyncCallback callback) {
        Throwable exception = null;
        RunningCase rc = new RunningCase();
        rc.callback = callback;
        try {
            rc.instance = createTestCase();
            rc.instance.setName(getTestName());
            running.put(rc.instance, rc);
            if (rc.instance instanceof TestCaseAccessProtected) {
                ((TestCaseAccessProtected) rc.instance).accessProtectedSetUp();
            }
            rc.startTime = System.currentTimeMillis();
            run(rc.instance);
        } catch (Throwable t) {
            log.error("test execution error", t);
            exception = t;
        }
        if (exception != null) {
            if (rc.dispose()) {
                long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
                //log.debug("onComplete called");
                callback.onComplete(new GResult(exception, duration));
            }
        } else if (rc.timeoutTimer == null) { // delayTestFinish was not called
            if (rc.dispose()) {
                long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
                //log.debug("onComplete called");
                callback.onComplete(new GResult(duration));
            }
        }
    }

    public static void delayTestFinish(TestCase testInstance, int timeoutMillis) {
        final RunningCase rc = running.get(testInstance);
        if ((rc == null) || (rc.timeoutTimer != null)) {
            throw new Error("Not running Case");
        }
        rc.timeoutTimer = new Timer() {
            @Override
            public void run() {
                if (rc.dispose()) {
                    long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
                    //log.debug("onComplete called for timeout");
                    rc.callback.onComplete(new GResult(false, "Test timeout", null, duration));
                }
            }
        };
    }

    public static void finishTest(TestCase testInstance) {
        RunningCase rc = running.get(testInstance);
        if (rc == null) {
            throw new Error("Not running Case");
        }
        if (rc.timeoutTimer == null) {
            throw new Error("Not delayed Case");
        }
        if (rc.dispose()) {
            long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
            //log.debug("onComplete called for finishTest");
            rc.callback.onComplete(new GResult(duration));
        }
    }

    protected abstract TestCase createTestCase() throws Exception;

    protected abstract void run(TestCase instance) throws Exception;
}
