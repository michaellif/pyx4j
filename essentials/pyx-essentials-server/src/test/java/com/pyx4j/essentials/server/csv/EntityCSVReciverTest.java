/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-07-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.csv;

import junit.framework.TestCase;

import com.pyx4j.essentials.server.csv.model.TestHeaderModel;

public class EntityCSVReciverTest extends TestCase {

    public static void testMatchHeaderOneLine() {
        EntityCSVReciver<TestHeaderModel> reciver = EntityCSVReciver.create(TestHeaderModel.class);
        assertFalse("Has no header", reciver.onHeader(new String[] { "", "Chamber", "Location" }));
        assertTrue("Header found", reciver.onHeader(new String[] { "Apartment", "Floor", "House" }));

        reciver.onRow(new String[] { "10", "2", "1" });
        assertEquals("Value parsed", "10", reciver.getEntities().get(0).apartment().getValue());
    }

    public static void testMatchHeaderTwoLinesOneMatch() {
        EntityCSVReciver<TestHeaderModel> reciver = EntityCSVReciver.create(TestHeaderModel.class);
        reciver.setHeaderLinesCount(1, 2);
        reciver.setHeadersMatchMinimum(2);

        assertFalse("Has no header", reciver.onHeader(new String[] { "", "Chamber", "Location" }));
        assertFalse("Has no header", reciver.onHeader(new String[] { "Apartment", "Floor", "House" }));

        assertTrue("Header found", reciver.onHeader(new String[] { "Street", "Apartment", "Floor" }));

        reciver.onRow(new String[] { "Burr", "10", "1" });
        assertEquals("Value parsed", "10", reciver.getEntities().get(0).apartment().getValue());
        assertEquals("Value parsed", "Burr", reciver.getEntities().get(0).street().getValue());
    }

    public static void testMatchHeaderTwoLinesTwoMatch() {
        EntityCSVReciver<TestHeaderModel> reciver = EntityCSVReciver.create(TestHeaderModel.class);
        reciver.setHeaderLinesCount(1, 2);
        reciver.setHeadersMatchMinimum(2);

        assertFalse("Has no header", reciver.onHeader(new String[] { "", "Chamber", "Location" }));
        assertFalse("Has no header", reciver.onHeader(new String[] { "Apartment", "Floor", "House" }));

        assertFalse("Header first", reciver.onHeader(new String[] { "Street", "", "" }));
        assertTrue("Header found", reciver.onHeader(new String[] { "Name", "Apartment", "Floor" }));

        reciver.onRow(new String[] { "Burr", "10", "1" });
        assertEquals("Value parsed", "10", reciver.getEntities().get(0).apartment().getValue());
        assertEquals("Value parsed", "Burr", reciver.getEntities().get(0).street().getValue());
    }

    public static void testMatchHeaderTwoLinesBestMatchInclude() {
        EntityCSVReciver<TestHeaderModel> reciver = EntityCSVReciver.create(TestHeaderModel.class);
        reciver.setHeaderLinesCount(1, 2);
        reciver.setHeadersMatchMinimum(2);

        assertFalse("Header first", reciver.onHeader(new String[] { "Street", "", "" }));
        assertTrue("Header found", reciver.onHeader(new String[] { "Name", "Apartment", "Postal Code" }));

        reciver.onRow(new String[] { "Burr", "10", "A1B" });
        assertEquals("Value parsed", "Burr", reciver.getEntities().get(0).street().getValue());
        assertEquals("Value parsed", "10", reciver.getEntities().get(0).apartment().getValue());
        assertEquals("Value parsed", "A1B", reciver.getEntities().get(0).postalCode().getValue());
    }

    public static void testMatchHeaderTwoLinesBestMatchExclude() {
        EntityCSVReciver<TestHeaderModel> reciver = EntityCSVReciver.create(TestHeaderModel.class);
        reciver.setHeaderLinesCount(1, 2);
        reciver.setHeadersMatchMinimum(2);

        assertFalse("Header first", reciver.onHeader(new String[] { "Address Listing", "", "" }));
        assertTrue("Header found", reciver.onHeader(new String[] { "Street", "Apartment", "Postal Code" }));

        reciver.onRow(new String[] { "Burr", "10", "A1B" });
        assertEquals("Value parsed", "Burr", reciver.getEntities().get(0).street().getValue());
        assertEquals("Value parsed", "10", reciver.getEntities().get(0).apartment().getValue());
        assertEquals("Value parsed", "A1B", reciver.getEntities().get(0).postalCode().getValue());
    }
}
