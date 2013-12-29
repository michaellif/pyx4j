/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mappers;

import org.apache.commons.lang.StringUtils;

import com.yardi.entity.mits.Address;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.PriorAddress;

public class AddressMapper {

    public PriorAddress map(Address mitsAddress) {

        PriorAddress address = EntityFactory.create(PriorAddress.class);

        String street = StringUtils.isNotEmpty(mitsAddress.getAddress1()) ? mitsAddress.getAddress1() : StringUtils.EMPTY;
        String streetName = street;
        String streetNumber = StringUtils.EMPTY;

        String[] streetTokens = street.split("\\s+", 2);
        if (streetTokens.length == 2) {
            streetNumber = streetTokens[0];
            streetName = streetTokens[1];
        }

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);

        address.city().setValue(mitsAddress.getCity());
        address.province().code().setValue(mitsAddress.getState());
        address.postalCode().setValue(mitsAddress.getPostalCode());

        return address;
    }
}
