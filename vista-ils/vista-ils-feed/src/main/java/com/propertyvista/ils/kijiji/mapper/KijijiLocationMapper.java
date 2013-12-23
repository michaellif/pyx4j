/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.kijiji.mapper;

import com.kijiji.pint.rs.ILSLocation;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.ils.kijiji.mapper.dto.ILSBuildingDTO;

public class KijijiLocationMapper {

    private final static String SALES_FORCE_ID = "0014000000cnJLf";

    public void convert(ILSBuildingDTO from, ILSLocation to) {
        Persistence.ensureRetrieve(from, AttachLevel.Attached);
        Marketing info = from.building().marketing();

        // identification
        to.setClientLocationId((int) from.building().getPrimaryKey().asLong());
        to.setSalesforceId(SALES_FORCE_ID);
        to.setBuildingName(from.profile().listingTitle().isNull() ? info.name().getValue() : from.profile().listingTitle().getValue());
        // address
        AddressStructured address = info.marketingAddress();
        if (address.isEmpty()) {
            address = from.building().info().address();
        }
        to.setStreetAddress(formatStreetAddress(address));
        to.setCity(address.city().getStringView());
        to.setProvince(address.province().getStringView());
        to.setPostalCode(address.postalCode().getStringView());
        // contacts
        String phone, email, url;
        if (!from.profile().preferredContacts().isEmpty()) {
            phone = from.profile().preferredContacts().phone().value().getValue();
            email = from.profile().preferredContacts().email().value().getValue();
            url = from.profile().preferredContacts().url().value().getValue();
        } else {
            phone = info.marketingContacts().phone().value().getValue();
            email = info.marketingContacts().email().value().getValue();
            url = info.marketingContacts().url().value().getValue();
            if (email == null || phone == null) {
                // check main office contact
                Persistence.service().retrieveMember(from.building().contacts().propertyContacts(), AttachLevel.Attached);
                for (PropertyContact contact : from.building().contacts().propertyContacts()) {
                    if (contact.type().getValue() == PropertyContactType.mainOffice) {
                        if (email == null) {
                            email = contact.email().getValue();
                        }
                        if (phone == null) {
                            phone = contact.phone().getValue();
                        }
                    }
                }
            }
            if (url == null) {
                url = from.building().contacts().website().getStringView();
            }
        }
        to.setEmail(email);
        to.setPhoneNumber(phone);
        to.setWebSite(url);
    }

    private String formatStreetAddress(AddressStructured address) {
        Object[] args = new Object[] {
                // @formatter:off
                address.suiteNumber().getValue(),
                address.streetNumber().getValue(),
                address.streetNumberSuffix().getValue(),
                address.streetName().getValue(),
                address.streetType().getValue(),
                address.streetDirection().getValue()
        }; // @formatter:on
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,null#|!null# {4}}{5,choice,null#|!null# {5}}", args);
    }

}
