/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2011
 * @author dmitry
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.server.preloader;

import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Address.AddressType;

import com.pyx4j.entity.shared.EntityFactory;

public class PreloadUtil {
    public static Address createAddress(String line1, String zip) {
        Address address = EntityFactory.create(Address.class);

        address.addressType().setValue(AddressType.property);
        address.addressLine1().setValue(line1);
        address.city().setValue("Toronto");
        address.state().setValue("ON");
        address.country().name().setValue("Canada");
        address.zip().setValue(zip);

        return address;
    }
}
