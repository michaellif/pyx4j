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
import java.util.List;
import java.util.Vector;

@SuppressWarnings("serial")
public class UnitTestInfo implements Serializable {

    private String className;

    private List<String> testNames;

    public UnitTestInfo() {

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getTestNames() {
        return testNames;
    }

    public void setTestNames(List<String> testNames) {
        this.testNames = testNames;
    }

    public void addTestName(String testName) {
        if (this.testNames == null) {
            this.testNames = new Vector<String>();
        }
        this.testNames.add(testName);
    }

}
