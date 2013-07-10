/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;

public interface StreetAddressParser {

    public final class StreetAddress {

        public final String unitNumber;

        public final String streetName;

        public final StreetType streetType;

        public final StreetDirection streetDirection;

        private final String streetNumber;

        StreetAddress(String unitNumber, String streetNumber, String streetName, StreetType streetType, StreetDirection streetDirection) {
            this.unitNumber = unitNumber;
            this.streetNumber = streetNumber;
            this.streetName = streetName;
            this.streetType = streetType;
            this.streetDirection = streetDirection;
        };

    }

    StreetAddress parse(String address1, String address2) throws ParseException;

}
