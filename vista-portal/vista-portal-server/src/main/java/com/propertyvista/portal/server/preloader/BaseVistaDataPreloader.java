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

import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Address.AddressType;
import com.propertyvista.portal.domain.ref.Country;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

abstract class BaseVistaDataPreloader extends AbstractDataPreloader {

    public Address createAddress(String line1, String zip) {
        Address address = EntityFactory.create(Address.class);

        address.addressType().setValue(AddressType.property);
        address.addressLine1().setValue(line1);
        address.city().setValue("Toronto");
        address.state().setValue("ON");
        address.country().set(retrieveNamed(Country.class, "Canada"));
        address.zip().setValue(zip);

        return address;
    }

}
