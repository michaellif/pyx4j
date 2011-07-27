/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.domain.contact.Address;

public class AddressRS {

    private Address address;

    public AddressRS() {
    }

    public AddressRS(Address address) {
        this.address = address;
    }

    @XmlElement(name = "streetNumber")
    public String getStreetNumber() {
        return address.streetNumber().getStringView();
    }

    @XmlElement(name = "streetName")
    public String getStreetName() {
        return address.streetName().getStringView();
    }

    @XmlElement(name = "streetType")
    public String getStreetType() {
        return address.streetType().getStringView();
    }

    @XmlElement(name = "city")
    public String getCity() {
        return address.city().getStringView();
    }

    @XmlElement(name = "province")
    public String getProvince() {
        return address.province().getStringView();
    }

    @XmlElement(name = "postalCode")
    public String getPostalCode() {
        return address.postalCode().getStringView();
    }

    @XmlElement(name = "country")
    public String getCountry() {
        return address.country().getStringView();
    }

}
