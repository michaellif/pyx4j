/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.math.BigDecimal;

import junit.framework.AssertionFailedError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.server.DateUtils;

public class Tester {

    private final static Logger log = LoggerFactory.getLogger(Tester.class);

    public static boolean continueOnError = false;

    public Tester() {
    }

    protected Object ifNull(Object in, Object out) {
        return in == null ? out : in;
    }

    protected void assertEquals(String message, Object expected, Object actual) {
        if ((expected == null) && (actual == null)) {
            return;
        }
        if ((expected != null) && (expected.equals(actual))) {
            return;
        }
        log.error("test error {}", format(message, expected, actual));
        if (continueOnError) {
            System.out.println(new StringBuilder().append(format(message, expected, actual)).append("\nat ").append(Thread.currentThread().getStackTrace()[3]));
        } else {
            throw new AssertionFailedError(format(message, expected, actual));
        }
    }

    protected void assertEquals(String message, BigDecimal expected, BigDecimal actual) {
        if ((expected == null) && (actual == null)) {
            return;
        }
        if ((expected != null) && (expected.compareTo(actual) == 0)) {
            return;
        }
        log.error("test error {}", format(message, expected, actual));
        if (continueOnError) {
            System.out.println(new StringBuilder().append(format(message, expected, actual)).append("\nat ").append(Thread.currentThread().getStackTrace()[3]));
        } else {
            throw new AssertionFailedError(format(message, expected, actual));
        }
    }

    protected static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null)
            formatted = message + " ";
        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }

    public void assertTrue(String message, boolean condition) {
        if (!(condition)) {
            log.error("test error {}", message);
            if (continueOnError) {
                System.out.println(new StringBuilder().append(message).append("\nat ").append(Thread.currentThread().getStackTrace()[3]));
            } else {
                throw new AssertionFailedError(message);
            }
        }
    }

    public void assertEquals(String message, String expected, LogicalDate actual) {
        assertEquals(message, new LogicalDate(DateUtils.detectDateformat(expected)), actual);
    }

    public void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    protected static LogicalDate getDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return new LogicalDate(DateUtils.detectDateformat(date));
        } catch (Exception e) {
            throw new Error("Failed to parse date " + date);
        }
    }
}
