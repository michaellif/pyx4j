/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id: GUnitTester.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.unit.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import com.google.gwt.core.client.GWT;

import com.pyx4j.unit.client.impl.AbstractGCaseMeta;
import com.pyx4j.unit.client.impl.GUnitMetaData;


public class GUnitTester {

    private static GUnitMetaData meta;

    public static GUnitMetaData getMeta() {
        if (meta == null) {
            meta = GWT.create(GUnitMetaData.class);
        }
        return meta;
    }

    public static List<Class<? extends TestCase>> getAllCases() {
        List<Class<? extends TestCase>> sortedList = new Vector<Class<? extends TestCase>>();
        sortedList.addAll(getMeta().getAllCases());
        Collections.sort(sortedList, new Comparator<Class<? extends TestCase>>() {
            @Override
            public int compare(Class<? extends TestCase> o1, Class<? extends TestCase> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return sortedList;
    }

    public static List<GCaseMeta> getClassMeta(Class<? extends TestCase> gCaseClass) {
        return getMeta().getClassMeta(gCaseClass);
    }

    /**
     * Put the current test in asynchronous mode.
     * 
     * @param testInstance
     * @param timeoutMillis
     */
    public static void delayTestFinish(TestCase testInstance, int timeoutMillis) {
        AbstractGCaseMeta.delayTestFinish(testInstance, timeoutMillis);
    }

    /**
     * Cause this test to succeed during asynchronous mode.
     * 
     * @param testInstance
     */
    public static void finishTest(TestCase testInstance) {
        AbstractGCaseMeta.finishTest(testInstance);
    }

    public static void setTestAwareExceptionHandler(TestAwareExceptionHandler testAwareExceptionHandler) {
        AbstractGCaseMeta.setTestAwareExceptionHandler(testAwareExceptionHandler);
    }
}
