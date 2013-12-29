/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class CaledonPadUtilsTest extends TestCase {

    public void testParser() {
        assertEquals(0, new BigDecimal(0).compareTo(CaledonPadUtils.parsAmount("0")));
        assertEquals(0, new BigDecimal("0.05").compareTo(CaledonPadUtils.parsAmount("5")));
        assertEquals(0, new BigDecimal("0.51").compareTo(CaledonPadUtils.parsAmount("51")));
        assertEquals(0, new BigDecimal("1.54").compareTo(CaledonPadUtils.parsAmount("154")));
        assertEquals(0, new BigDecimal("1548.97").compareTo(CaledonPadUtils.parsAmount("154897")));
    }
}
