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
package com.propertyvista.biz.financial;

import junit.framework.AssertionFailedError;

public class Tester {

    @SuppressWarnings("unused")
    protected static void assertEquals(String message, Object expected, Object actual) {
        if ((expected == null) && (actual == null)) {
            return;
        }
        if ((expected != null) && (expected.equals(actual))) {
            return;
        }
        if (false) {
            System.out.println(format(message, expected, actual));
            Thread.dumpStack();
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
}
