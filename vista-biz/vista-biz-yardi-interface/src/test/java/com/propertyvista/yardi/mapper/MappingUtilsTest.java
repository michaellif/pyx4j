/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Address;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.yardi.mappers.MappingUtils;

public class MappingUtilsTest extends TestCase {

    private static Logger log = LoggerFactory.getLogger(MappingUtilsTest.class);

    private final StringBuilder error = new StringBuilder();

    private InternationalAddress addr;

    @Override
    public void tearDown() {
        if (error.length() > 0) {
            log.info("Parsing Errors:{}", error);
            error.setLength(0);
        }
    }

    // street number parsing
    public void testAddressParser() {
        addr = MappingUtils.getAddress(getMitsAddress("1 Main St"), error);
        assertEquals("1", addr.streetNumber().getValue());
        assertEquals("Main St", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("457\\499 Albert Street"), error);
        assertEquals("457\\499", addr.streetNumber().getValue());
        assertEquals("Albert Street", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("2069 - 2077 Prospect Street"), error);
        assertEquals("2069 - 2077", addr.streetNumber().getValue());
        assertEquals("Prospect Street", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("4,8,12, 14, 16 Cartier & 1390-1410 Kensington"), error);
        assertNull(addr.streetNumber().getValue());
        assertEquals("4,8,12, 14, 16 Cartier & 1390-1410 Kensington", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("715 & 735 Laurier Street"), error);
        assertEquals("715 & 735", addr.streetNumber().getValue());
        assertEquals("Laurier Street", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("1, 3-5, 9-11, 13 Ivan Court"), error);
        assertEquals("1, 3-5, 9-11, 13", addr.streetNumber().getValue());
        assertEquals("Ivan Court", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("25-44 1/2 Oakland Avenue, 130 First Avenue"), error);
        assertNull(addr.streetNumber().getValue());
        assertEquals("25-44 1/2 Oakland Avenue, 130 First Avenue", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("50 & 60 MacAlesse Lane"), error);
        assertEquals("50 & 60", addr.streetNumber().getValue());
        assertEquals("MacAlesse Lane", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("10, 11 & 20 Charlie Grace Terrace"), error);
        assertEquals("10, 11 & 20", addr.streetNumber().getValue());
        assertEquals("Charlie Grace Terrace", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("211, 221 Glenforest Drive"), error);
        assertEquals("211, 221", addr.streetNumber().getValue());
        assertEquals("Glenforest Drive", addr.streetName().getValue());

        addr = MappingUtils.getAddress(getMitsAddress("3000,3015 and 3017 Queen Street East"), error);
        assertEquals("3000,3015 and 3017", addr.streetNumber().getValue());
        assertEquals("Queen Street East", addr.streetName().getValue());
    }

    private Address getMitsAddress(String... parts) {
        Address addr = getDefaultAddress();
        for (int idx = 0; idx < parts.length; idx++) {
            if (parts[idx] == null) {
                continue;
            }
            switch (idx) {
            case 0:
                addr.setAddress1(parts[idx]);
                break;
            case 1:
                addr.getAddress2().add(parts[idx]);
                break;
            case 2:
                addr.setCity(parts[idx]);
                break;
            case 3:
                addr.setState(parts[idx]);
                break;
            case 4:
                addr.setCountry(parts[idx]);
                break;
            case 5:
                addr.setPostalCode(parts[idx]);
                break;
            }
        }
        return addr;
    }

    private Address getDefaultAddress() {
        Address addr = new Address();
        addr.setAddress1("123 Main St");
        addr.setCity("Hometown");
        addr.setState("ON");
        addr.setCountry("Canada");
        addr.setPostalCode("A1B2C3");
        return addr;
    }
}
