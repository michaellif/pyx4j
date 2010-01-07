/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Timer;

import com.pyx4j.unit.client.GCaseMeta;
import com.pyx4j.unit.client.GCaseResultAsyncCallback;
import com.pyx4j.unit.client.GResult;
import com.pyx4j.unit.client.TestAwareExceptionHandler;

public abstract class AbstractGCaseMeta implements GCaseMeta {

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

        void dispose() {
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
            if (instance != null) {
                running.remove(instance);
                instance = null;
            }
            if (timeoutTimer != null) {
                timeoutTimer.cancel();
                timeoutTimer = null;
            }
        }

        @Override
        public void onUncaughtException(Throwable t) {
            String exceptionMessage = t.getClass().getName();
            if (t.getMessage() != null) {
                exceptionMessage += " [" + t.getMessage() + "]";
            }
            dispose();
            long duration = (startTime == 0) ? 0 : System.currentTimeMillis() - startTime;
            callback.onComplete(new GResult(false, exceptionMessage, duration));
        }
    }

    private static Map<TestCase, RunningCase> running = new HashMap<TestCase, RunningCase>();

    public static void setTestAwareExceptionHandler(TestAwareExceptionHandler testAwareExceptionHandler) {
        AbstractGCaseMeta.testAwareExceptionHandler = testAwareExceptionHandler;
    }

    @Override
    public void execute(GCaseResultAsyncCallback callback) {
        String exceptionMessage = null;
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
            exceptionMessage = t.getClass().getName();
            if (t.getMessage() != null) {
                exceptionMessage += " [" + t.getMessage() + "]";
            }
        }
        // delayTestFinish was not called
        if ((rc.timeoutTimer == null) || (exceptionMessage != null)) {
            rc.dispose();
            long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
            callback.onComplete(new GResult(exceptionMessage == null, exceptionMessage, duration));
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
                rc.dispose();
                long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
                rc.callback.onComplete(new GResult(false, "Test timeout", duration));
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
        rc.dispose();
        long duration = (rc.startTime == 0) ? 0 : System.currentTimeMillis() - rc.startTime;
        rc.callback.onComplete(new GResult(true, null, duration));
    }

    protected abstract TestCase createTestCase() throws Exception;

    protected abstract void run(TestCase instance) throws Exception;
}
