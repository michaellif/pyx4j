/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 26, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.gwt.server.DateUtils;

public class N4GenerationUtilsTest {

    @Test
    public void testSplitCurrency() {

        {
            String[] splitted1 = N4GenerationUtils.splitCurrency(new BigDecimal("1000.00"), false);
            Assert.assertArrayEquals(new String[] { " 1", "000", "00" }, splitted1);
        }

        {
            String[] splitted1 = N4GenerationUtils.splitCurrency(new BigDecimal("1000.00"), true);
            Assert.assertArrayEquals(new String[] { "1", "000", "00" }, splitted1);
        }

        {
            String[] splitted2 = N4GenerationUtils.splitCurrency(new BigDecimal("10000.00"), false);
            Assert.assertArrayEquals(new String[] { "10", "000", "00" }, splitted2);
        }

        {
            String[] splitted3 = N4GenerationUtils.splitCurrency(new BigDecimal("100.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", "100", "00" }, splitted3);
        }

        {
            String[] splitted3 = N4GenerationUtils.splitCurrency(new BigDecimal("100.00"), true);
            Assert.assertArrayEquals(new String[] { " ", "100", "00" }, splitted3);
        }

        {
            String[] splitted4 = N4GenerationUtils.splitCurrency(new BigDecimal("10.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", " 10", "00" }, splitted4);
        }

        {
            String[] splitted4 = N4GenerationUtils.splitCurrency(new BigDecimal("10.00"), true);
            Assert.assertArrayEquals(new String[] { " ", " 10", "00" }, splitted4);
        }

        {
            String[] splitted4 = N4GenerationUtils.splitCurrency(new BigDecimal("1.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", "  1", "00" }, splitted4);
        }
        {
            String[] splitted4 = N4GenerationUtils.splitCurrency(new BigDecimal("1.00"), true);
            Assert.assertArrayEquals(new String[] { " ", "  1", "00" }, splitted4);
        }
    }

    @Test
    public void testSplitDate() {
        String[] splitted = N4GenerationUtils.splitDate(DateUtils.detectDateformat("2013-04-01"));
        Assert.assertArrayEquals(new String[] { "01", "04", "2013" }, splitted);
    }

    @Test
    public void testSplitPhoneNumber() {
        String[] splitted = N4GenerationUtils.splitPhoneNumber("(647) 555-5555");
        Assert.assertArrayEquals(new String[] { "647", "555", "5555" }, splitted);
    }
}
