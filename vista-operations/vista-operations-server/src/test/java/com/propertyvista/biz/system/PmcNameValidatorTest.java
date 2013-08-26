/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.system;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class PmcNameValidatorTest {

    /**
     *
     */
    @Test
    public void testWildcardReservedNamesMatching() {
        PmcNameValidator.setReservedWords(new String[] { "x*", "a*c", "b??c", "?*a" });

        assertTrue(PmcNameValidator.isDnsReserved("xa"));
        assertTrue(PmcNameValidator.isDnsReserved("xb"));
        assertTrue(PmcNameValidator.isDnsReserved("xabcdefgh"));

        assertTrue(PmcNameValidator.isDnsReserved("ac"));
        assertTrue(PmcNameValidator.isDnsReserved("abc"));
        assertTrue(PmcNameValidator.isDnsReserved("abbbc"));
        assertTrue(PmcNameValidator.isDnsReserved("adafdfkjhfadfdafdsfhlakc"));

        assertTrue(PmcNameValidator.isDnsReserved("bxxc"));
        assertTrue(PmcNameValidator.isDnsReserved("bAAc"));
        assertTrue(PmcNameValidator.isDnsReserved("byzc"));
        assertFalse(PmcNameValidator.isDnsReserved("bxc"));
        assertFalse(PmcNameValidator.isDnsReserved("bxfsc"));

        assertTrue(PmcNameValidator.isDnsReserved("za"));
        assertTrue(PmcNameValidator.isDnsReserved("fa"));
        assertTrue(PmcNameValidator.isDnsReserved("ba"));
        assertTrue(PmcNameValidator.isDnsReserved("zbcdaa"));
        assertTrue(PmcNameValidator.isDnsReserved("zabcda"));
        assertTrue(PmcNameValidator.isDnsReserved("yabcda"));
        assertFalse(PmcNameValidator.isDnsReserved("yabcdar"));

        assertTrue(PmcNameValidator.isDnsNameValid("abcd"));
        assertTrue(PmcNameValidator.isDnsNameValid("abc123"));
        assertTrue(PmcNameValidator.isDnsNameValid("123abc"));
        assertFalse(PmcNameValidator.isDnsNameValid("-123abc"));
        assertFalse(PmcNameValidator.isDnsNameValid("_123abc"));
        assertTrue(PmcNameValidator.isDnsNameValid("1abc"));
        assertTrue(PmcNameValidator.isDnsNameValid("Abcd"));
        assertTrue(PmcNameValidator.isDnsNameValid("AbCdE"));
        assertTrue(PmcNameValidator.isDnsNameValid("ABCDE"));
        assertFalse(PmcNameValidator.isDnsNameValid("%abc"));
        assertFalse(PmcNameValidator.isDnsNameValid("ab%c"));

        assertFalse("DNS name must not contain only numbers", PmcNameValidator.isDnsNameValid("1234"));
    }

    @Test
    public void testProdReservedNamesMatching() {
        PmcNameValidator.loadReservedWords();

        assertTrue(PmcNameValidator.isDnsReserved("vv_prod01"));
        assertTrue(PmcNameValidator.isDnsReserved("vv-prod01"));
        assertTrue(PmcNameValidator.isDnsReserved("vv-prod1"));
        assertTrue(PmcNameValidator.isDnsReserved("vv_prod1"));
        assertTrue(PmcNameValidator.isDnsReserved("vv_staging1"));
        assertTrue(PmcNameValidator.isDnsReserved("vv_staging01"));
        assertTrue(PmcNameValidator.isDnsReserved("vv-staging1"));
        assertTrue(PmcNameValidator.isDnsReserved("vv-staging01"));
        assertFalse(PmcNameValidator.isDnsReserved("vv-staging01a"));

    }
}
