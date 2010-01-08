/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.unit.shared.UnitTestExecuteRequest;
import com.pyx4j.unit.shared.UnitTestInfo;
import com.pyx4j.unit.shared.UnitTestResult;
import com.pyx4j.unit.shared.UnitTestsServices.ExectuteTest;
import com.pyx4j.unit.shared.UnitTestsServices.GetTestsList;

public abstract class UnitTestsServicesImpl {

    private static final Logger log = LoggerFactory.getLogger(UnitTestsServicesImpl.class);

    public static class GetTestsListImpl implements GetTestsList {

        @SuppressWarnings("unchecked")
        @Override
        public Vector<UnitTestInfo> execute(String request) {
            Vector<UnitTestInfo> tests = new Vector<UnitTestInfo>();

            ClassLoader cld = Thread.currentThread().getContextClassLoader();

            List<String> classes = TestsClassFinder.findTestClasses();
            for (String className : classes) {
                Class<?> c;
                try {
                    c = cld.loadClass(className);
                } catch (Throwable e) {
                    log.warn("Can't load class {}", className);
                    continue;
                }
                if (!TestCase.class.isAssignableFrom(c)) {
                    log.warn("Not a TestCase class {}", className);
                }
                //Use jUnit 'API' to get lists of tests
                TestSuite ts = new TestSuite((Class<TestCase>) c);
                if (ts.countTestCases() != 0) {
                    UnitTestInfo ti = new UnitTestInfo(className);
                    if (tests.contains(ti)) {
                        continue;
                    }
                    Enumeration<Test> iter = ts.tests();
                    while (iter.hasMoreElements()) {
                        Test t = iter.nextElement();
                        if (t instanceof TestCase) {
                            ti.addTestName(((TestCase) t).getName());
                        } else {
                            log.warn("Not a TestCase {}", t);
                        }
                    }
                    if (ti.getTestNames() != null) {
                        tests.add(ti);
                    }
                }
            }

            return tests;
        }

    };

    public static class ExectuteTestImpl implements ExectuteTest {

        @SuppressWarnings("unchecked")
        @Override
        public UnitTestResult execute(UnitTestExecuteRequest request) {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            Class<?> c;
            try {
                c = cld.loadClass(request.getClassName());
            } catch (ClassNotFoundException e) {
                log.warn("Can't load class {}", request.getClassName());
                return new UnitTestResult("Can't load test class");
            }
            if (!TestCase.class.isAssignableFrom(c)) {
                log.warn("Not a TestCase class {}", request.getClassName());
                return new UnitTestResult("Not a TestCase class");
            }
            TestSuite ts = new TestSuite((Class<TestCase>) c);
            TestCase tc = null;
            Enumeration<Test> iter = ts.tests();
            while (iter.hasMoreElements()) {
                Test t = iter.nextElement();
                if ((t instanceof TestCase) && ((TestCase) t).getName().equals(request.getTestName())) {
                    tc = (TestCase) t;
                }
            }
            if (tc == null) {
                return new UnitTestResult("Test " + request.toString() + " not found");
            }
            TestResult testResult = new TestResult();
            long start = System.currentTimeMillis();
            tc.run(testResult);
            long duration = System.currentTimeMillis() - start;
            if (testResult.runCount() != 1) {
                return new UnitTestResult("Can't run test");
            }
            if (testResult.errorCount() > 0) {
                TestFailure failure = testResult.errors().nextElement();
                return new UnitTestResult(false, failure.exceptionMessage(), duration);
            } else if (testResult.failureCount() > 0) {
                TestFailure failure = testResult.failures().nextElement();
                return new UnitTestResult(false, failure.exceptionMessage(), duration);
            } else {
                return new UnitTestResult(true, null, duration);
            }
        }
    }

}
