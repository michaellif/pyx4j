/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Address.AddressType;

abstract class BaseVistaDataPreloader extends AbstractDataPreloader {

    protected BaseVistaDataPreloader() {
        DataGenerator.setRandomSeed(100);
    }

    public Address createAddress(String line1, String zip) {
        Address address = EntityFactory.create(Address.class);

        address.addressType().setValue(AddressType.property);
        address.street1().setValue(line1);
        address.city().setValue("Toronto");
        address.province().set(retrieveByMemeber(Province.class, address.province().code(), "ON"));
        address.country().set(retrieveNamed(Country.class, "Canada"));
        address.postalCode().setValue(zip);

        return address;
    }
}
