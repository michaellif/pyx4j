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

    public void test1() {
        StreetAddress a = parse("1065 Eglinton Avenue East", null);

        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertEquals(StreetDirection.east, a.streetDirection);
    }

    public void test2() {
        StreetAddress a = parse("    1065      Eglinton   Avenue    E    ", null);

        Assert.assertNull(a.unitNumber);
        Assert.assertEquals("1065", a.streetNumber);
        Assert.assertEquals("Eglinton", a.streetName);
        Assert.assertEquals(StreetType.avenue, a.streetType);
        Assert.assertEquals(StreetDirection.east, a.streetDirection);
    }

    private StreetAddress parse(String address1, String address2) {
        try {
            return parser.parse(address1, address2);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }
}
