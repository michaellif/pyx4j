/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.server.common.reference.StreetTypeAbbreviations;
import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

public class CanadianStreetAddressParserTest extends TestCase {

    private final StreetAddressParser parser = new CanadianStreetAddressParser();

    public void testSanity() {
        StreetAddress a = parse("1065 Eglinton Avenue East", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertEquals(StreetDirection.east, a.streetDirection);

        a = parse("    1065      Eglinton   Avenue    E    ", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertEquals(StreetDirection.east, a.streetDirection);

        a = parse("    1065      Eglinton   Avenue    Northeast", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertEquals(StreetDirection.northEast, a.streetDirection);

        a = parse("    1065      Eglinton   Avenue  ", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertNull(a.streetDirection);

        a = parse("1065 Eglinton  CarambA", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton CarambA", a.streetName);
        Assert.assertEquals(StreetType.other, a.streetType);
        Assert.assertNull(a.streetDirection);
    }

    public void testStreetTypeAbbreviationReference() {
        Assert.assertEquals(StreetType.avenue, StreetTypeAbbreviations.getStreetType("Ave"));

        Assert.assertEquals(StreetType.arcade, StreetTypeAbbreviations.getStreetType("aRcade"));
        Assert.assertEquals(StreetType.arcade, StreetTypeAbbreviations.getStreetType("arc"));

    }

    public void testStreetTypeAbbreviation() {
        StreetAddress a;

        a = parse("1065 Eglinton Ave", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertNull(a.streetDirection);

        a = parse("1065 Eglinton Ave.", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertNull(a.streetDirection);

        a = parse("1065 Eglinton AVENU", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertNull(a.streetDirection);

        // Not supported by vista StreetTypes but present in refference
        a = parse("1065 Eglinton Plaza", null);
        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton Plaza", a.streetName);
        Assert.assertEquals(StreetType.other, a.streetType);
        Assert.assertNull(a.streetDirection);
    }

    public void testComplexStreetName() {
        StreetAddress a = parse("10-1065 Wild Wild West Street North", null);
        Assert.assertEquals("Wild Wild West", a.streetName);
    }

    public void testDirectionWithComplexStreetName() {
        StreetAddress a = parse("10-1065 Wild Wild West Street North", null);
        Assert.assertEquals(StreetDirection.north, a.streetDirection);
    }

    public void testComplexStreetNameNoDirection() {
        StreetAddress a = parse("10-1065 Wild Wild West Street", null);
        Assert.assertNull(a.streetDirection);
    }

    public void testUnitNumberParsingBeforeCivicNumber() {
        StreetAddress a = parse("10-1065      Eglinton   Avenue    Northeast", null);
        Assert.assertEquals("10", a.unitNumber);
    }

    public void testUnitNumberParsingAfterStreetType() {
        StreetAddress a = parse("1065      Eglinton   Avenue  Apt 10", null);
        Assert.assertEquals("10", a.unitNumber);
    }

    public void testUnitNumberParsingInAddressLine2() {
        StreetAddress a = parse("1065      Eglinton   Avenue  Northeast", "Apt 10");
        Assert.assertEquals("10", a.unitNumber);
    }

    public void testCivicNumberParsingWithUnit() {
        StreetAddress a = parse("10-1065      Eglinton   Avenue  Northeast", "Apt 10");
        Assert.assertEquals("1065", a.streetNumber);
    }

    public void testCivicNumberParsing() {
        StreetAddress a = parse("1065      Eglinton   Avenue  Northeast", "Apt 10");
        Assert.assertEquals("1065", a.streetNumber);
    }

    public void testCivicNumberParsingWithAlphaSuffix() {
        StreetAddress a = parse("1065a Eglinton Avenue Northeast", "Apt 10");
        Assert.assertEquals("1065a", a.streetNumber);
    }

    public void testCivicNumberParsingWithFractionSuffix() {
        StreetAddress a = parse("1065 1/2 Eglinton Avenue Northeast", "Apt 10");
        Assert.assertEquals("1065 1/2", a.streetNumber);
    }

    public void test4AvenueRoad() {
        StreetAddress a = parse("1065 Avenue Road", null);
        Assert.assertEquals("Avenue", a.streetName);
        Assert.assertEquals(StreetType.road, a.streetType);
    }

    public void testTheStreetName() {
        StreetAddress a = parse("1065 The Avenue", null);
        Assert.assertEquals("The Avenue", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);

        StreetAddress a2 = parse("1065 The Parkway", null);
        Assert.assertEquals("The Parkway", a2.streetName);
        Assert.assertEquals(StreetType.parkway, a2.streetType);

        StreetAddress a3 = parse("1065 The Chunga-Changa Street", null);
        Assert.assertEquals("The Chunga-Changa", a3.streetName);
        Assert.assertEquals(StreetType.street, a3.streetType);

        StreetAddress a4 = parse("1065 The Chunga-Changa", null);
        Assert.assertEquals("The Chunga-Changa", a4.streetName);
        Assert.assertEquals(StreetType.other, a4.streetType);

        StreetAddress a5 = parse("1065 The Chunga-Changa Street North", null);
        Assert.assertEquals("The Chunga-Changa", a5.streetName);
        Assert.assertEquals(StreetType.street, a5.streetType);
        Assert.assertEquals(StreetDirection.north, a5.streetDirection);

    }

    public void testParsingFailures() {
        StreetAddress a; // for debugging
        {
            Boolean parseFailed = false;
            try {
                a = parser.parse("457 - 499 Albert Street", null);
            } catch (ParseException e) {
                parseFailed = true;
            }

            Assert.assertTrue(parseFailed);
        }
        {
            Boolean parseFailed = false;
            try {
                a = parser.parse("211-Baseline Road W", "Unit 00211");
            } catch (ParseException e) {
                parseFailed = true;
            }

            Assert.assertTrue(parseFailed);
        }

        {
            Boolean parseFailed = false;
            try {
                a = parser.parse("3000,3015 and 3017 Baseline Road W", null);
            } catch (ParseException e) {
                parseFailed = true;
            }

            Assert.assertTrue(parseFailed);
        }
    }

    // TODO French Style Street Names
    public void TODO_testFrenchStyleStreetName() {
        StreetAddress a = parse("10-1065 Rue Wild Wild West", null);
        Assert.assertEquals("Rue Wild Wild", a.streetName); // because there's no enum for french stuff
        Assert.assertEquals(StreetType.other, a.streetType);
        Assert.assertEquals(StreetDirection.west, a.streetDirection);
    }

    private StreetAddress parse(String address1, String address2) {
        try {
            return parser.parse(address1, address2);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

}
