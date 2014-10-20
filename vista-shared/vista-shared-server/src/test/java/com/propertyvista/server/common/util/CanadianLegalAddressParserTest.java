/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;

import junit.framework.TestCase;

import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

public class CanadianLegalAddressParserTest extends TestCase {

    private final StreetAddressParser parser = new CanadianLegalAddressParser();

    private StreetAddress a;

    public void testSanity() {
        StreetAddress a = parse("1065 Eglinton Avenue East", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Avenue", a.streetType);
        assertEquals("East", a.streetDirection);

        a = parse("    1065      Eglinton   Avenue    E    ", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Avenue", a.streetType);
        assertEquals("E", a.streetDirection);

        a = parse("    1065      Eglinton   Avenue    Northeast", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Avenue", a.streetType);
        assertEquals("Northeast", a.streetDirection);

        a = parse("    1065      Eglinton   Avenue  ", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Avenue", a.streetType);
        assertNull(a.streetDirection);
    }

    public void testStreetTypeUnknownSuffixDropped() {
        a = parse("123 Main Avenue xyz", null);
        assertNull(a.unitNumber);
        assertEquals("123", a.streetNumber);
        assertEquals("Main", a.streetName);
        assertEquals("Avenue", a.streetType);
        assertNull(a.streetDirection);
    }

    public void testStreetTypeAbbreviation() {

        a = parse("1065 Eglinton Ave", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Ave", a.streetType);
        assertNull(a.streetDirection);

        a = parse("1065 Eglinton Ave.", null);
        assertNull(a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Eglinton", a.streetName);
        assertEquals("Ave", a.streetType);
        assertNull(a.streetDirection);
    }

    public void testComplexStreetName() {
        a = parse("10-1065 Wild Wild West Street North", null);
        assertEquals("Wild Wild West", a.streetName);
    }

    public void testDirectionWithComplexStreetName() {
        a = parse("10-1065 Wild Wild West Street North", null);
        assertEquals("North", a.streetDirection);
    }

    public void testComplexStreetNameNoDirection() {
        a = parse("10-1065 Wild Wild West Street", null);
        assertNull(a.streetDirection);
    }

    public void testUnitNumberParsingBeforeCivicNumber() {
        a = parse("10-1065      Eglinton   Avenue    Northeast", null);
        assertEquals("10", a.unitNumber);
    }

    public void testUnitNumberParsingAfterStreetType() {
        a = parse("1065      Eglinton   Avenue  Apt 10", null);
        assertEquals("10", a.unitNumber);
    }

    public void testUnitNumberParsingInAddressLine2() {
        a = parse("1065      Eglinton   Avenue  Northeast", "Apt 10");
        assertEquals("10", a.unitNumber);
        a = parse("1065      Eglinton   Avenue  Northeast", "10");
        assertEquals("10", a.unitNumber);
    }

    public void testCivicNumberParsingWithUnit() {
        a = parse("10-1065      Eglinton   Avenue  Northeast", "Apt 10");
        assertEquals("1065", a.streetNumber);
    }

    public void testCivicNumberParsing() {
        a = parse("1065      Eglinton   Avenue  Northeast", "Apt 10");
        assertEquals("1065", a.streetNumber);
    }

    public void testCivicNumberParsingWithAlphaSuffix() {
        a = parse("1065a Eglinton Avenue Northeast", "Apt 10");
        assertEquals("1065a", a.streetNumber);
    }

    public void testCivicNumberParsingWithFractionSuffix() {
        a = parse("1065 1/2 Eglinton Avenue Northeast", "Apt 10");
        assertEquals("1065 1/2", a.streetNumber);
    }

    public void test4AvenueRoad() {
        a = parse("1065 Avenue Road", null);
        assertEquals("Avenue", a.streetName);
        assertEquals("Road", a.streetType);
    }

    public void testTheStreetName() {
        a = parse("1065 The Avenue", null);
        assertEquals("The Avenue", a.streetName);
        assertEquals("Avenue", a.streetType);

        a = parse("1065 The Parkway", null);
        assertEquals("The Parkway", a.streetName);
        assertEquals("Parkway", a.streetType);

        a = parse("1065 The Chunga-Changa Street", null);
        assertEquals("The Chunga-Changa", a.streetName);
        assertEquals("Street", a.streetType);

        a = parse("1065 The Chunga-Changa Street North", null);
        assertEquals("The Chunga-Changa", a.streetName);
        assertEquals("Street", a.streetType);
        assertEquals("North", a.streetDirection);
    }

    public void testParsingFailures() {
        try {
            // No Street Type
            a = parser.parse("1065 The Chunga-Changa", null);
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Cannot extract street type");
        }

        try {
            // Misspelled Street Type
            a = parser.parse("1065 Eglinton AVENU", null);
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Cannot extract street type");
        }

        try {
            // Not supported by vista StreetTypes but present in reference
            a = parser.parse("1065 Eglinton Plaza", null);
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Cannot extract street type");
        }

        try {
            a = parser.parse("457 - 499 Albert Street", null);
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Parsed address validation failed");
        }

        try {
            a = parser.parse("211-Baseline Road W", "Unit 00211");
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Parsed address validation failed");
        }

        try {
            a = parser.parse("3000,3015 and 3017 Baseline Road W", null);
            assertTrue("Parsing failure expected", false);
        } catch (ParseException e) {
            assertEquals(e.getMessage(), "Parsed address validation failed");
        }
    }

    // TODO - add more French Style cases
    public void testFrenchStyleStreetName() {
        a = parse("10-1065 Rue Wild Wild Est", null);
        assertEquals("10", a.unitNumber);
        assertEquals("1065", a.streetNumber);
        assertEquals("Wild Wild", a.streetName);
        assertEquals("Rue", a.streetType);
        assertEquals("Est", a.streetDirection);

        a = parse("123 Sainte-Catherine Rue NO", null);
        assertEquals("123", a.streetNumber);
        assertEquals("Sainte-Catherine", a.streetName);
        assertEquals("Rue", a.streetType);
        assertEquals("NO", a.streetDirection);
    }

    private StreetAddress parse(String address1, String address2) {
        try {
            return parser.parse(address1, address2);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

}
