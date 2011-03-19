/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-18
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.protal.domain.util;

import junit.framework.TestCase;

import com.propertyvista.portal.domain.util.ValidationUtils;

public class ValidationUtilsTest extends TestCase {

    public void testCC() {
        assertTrue(ValidationUtils.isCreditCardNumberValid("378282246310005"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("371449635398431"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("378734493671000"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("5610591081018250"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("30569309025904"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("38520000023237"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6011111111111117"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6011000990139424"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("3530111333300000"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("3566002020360505"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("5555555555554444"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("5105105105105100"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4111111111111111"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4012888888881881"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4222222222222"));

        assertTrue(ValidationUtils.isCreditCardNumberValid("4111 1111 1111 1111"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4012 8888 8888 1881  "));
        assertTrue(ValidationUtils.isCreditCardNumberValid("3852 0000  023  237"));

        assertTrue(ValidationUtils.isCreditCardNumberValid("4007 0000 0002 7"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4556 3818 1280 6"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("4012 8888 8888 1881"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("3056 9309 0259 04"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("3434 3434 3434 343"));

        assertTrue(ValidationUtils.isCreditCardNumberValid("6304 9000 1774 0292 441"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6333 3333 3333 3333 336"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6767 6222 2222 2222 222"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6767 6767 6767 6767 671"));

        //        assertTrue(ValidationUtils.isCreditCardNumberValid("76009244561")); // strange card - 11 digits???
        assertTrue(ValidationUtils.isCreditCardNumberValid("5019717010103742"));
        assertTrue(ValidationUtils.isCreditCardNumberValid("6331101999990016"));

        assertFalse(ValidationUtils.isCreditCardNumberValid("1234567890123456"));
        assertFalse(ValidationUtils.isCreditCardNumberValid("sertgrew34gsdgsd"));
        assertFalse(ValidationUtils.isCreditCardNumberValid("4012888898881881"));
        assertFalse(ValidationUtils.isCreditCardNumberValid("4012888808881881"));
        assertFalse(ValidationUtils.isCreditCardNumberValid("7012888888881881"));

    }

    public void testSIN() {
        assertTrue(ValidationUtils.isSinValid("046 454 286"));
        assertTrue(ValidationUtils.isSinValid("046454  286"));
        assertTrue(ValidationUtils.isSinValid("046454286"));

        assertFalse(ValidationUtils.isSinValid("123 456 789"));
    }
}
