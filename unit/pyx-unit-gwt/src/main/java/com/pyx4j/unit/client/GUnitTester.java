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
 */
package com.pyx4j.unit.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import com.google.gwt.core.client.GWT;

import com.pyx4j.unit.client.impl.AbstractGCaseMeta;
import com.pyx4j.unit.client.impl.TestSuiteMetaData;

public class GUnitTester {

    private static TestSuiteMetaData meta;

    public static TestSuiteMetaData getMeta() {
        if (meta == null) {
            meta = GWT.create(TestSuiteMetaData.class);
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

    public static Collection<List<GCaseMeta>> getAllGCaseMeta() {
        List<List<GCaseMeta>> sortedList = new Vector<List<GCaseMeta>>();
        sortedList.addAll(getMeta().getAllGCaseMeta());
        Collections.sort(sortedList, new Comparator<List<GCaseMeta>>() {
            @Override
            public int compare(List<GCaseMeta> o1, List<GCaseMeta> o2) {
                return o1.get(0).getTestClassName().compareTo(o2.get(0).getTestClassName());
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
