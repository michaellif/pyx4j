/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UnitTestExecuteRequest implements Serializable {

    private String className;

    private String testName;

    public UnitTestExecuteRequest() {

    }

    public UnitTestExecuteRequest(String className, String testName) {
        this.className = className;
        this.testName = testName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
