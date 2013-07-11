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

    public void testFrenchStyleStreetName() {
        StreetAddress a = parse("10-1065 Rue Wild Wild West", null);
        Assert.assertEquals("Rue Wild Wild", a.streetName); // because there's no enum for french stuff
        Assert.assertEquals(StreetType.other, a.streetType);
        Assert.assertEquals(StreetDirection.west, a.streetDirection);
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

    private StreetAddress parse(String address1, String address2) {
        try {
            return parser.parse(address1, address2);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }
}
