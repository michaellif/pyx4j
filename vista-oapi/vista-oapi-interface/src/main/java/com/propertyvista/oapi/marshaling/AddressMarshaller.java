/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.xml.StringIO;

public class AddressMarshaller extends AbstractMarshaller<InternationalAddress, AddressIO> {

    private static class SingletonHolder {
        public static final AddressMarshaller INSTANCE = new AddressMarshaller();
    }

    private AddressMarshaller() {
    }

    public static AddressMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public AddressIO marshal(InternationalAddress address) {
        if (address == null || address.isNull()) {
            return null;
        }
        AddressIO addressIO = new AddressIO();

        addressIO.country = createIo(StringIO.class, address.country().getValue().name);
        addressIO.province = createIo(StringIO.class, address.province());
        addressIO.city = createIo(StringIO.class, address.city());
        addressIO.postalCode = createIo(StringIO.class, address.postalCode());
        addressIO.streetNumber = createIo(StringIO.class, address.streetNumber());
        addressIO.streetName = createIo(StringIO.class, address.streetName());
        addressIO.unitNumber = createIo(StringIO.class, address.suiteNumber());
        return addressIO;
    }

    @Override
    public InternationalAddress unmarshal(AddressIO addressIO) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);

        if (addressIO.country != null) {
            address.country().setValue(ISOCountry.forName(addressIO.country.getValue()));
        }

        setValue(address.province(), addressIO.province);
        setValue(address.city(), addressIO.city);
        setValue(address.postalCode(), addressIO.postalCode);
        setValue(address.streetNumber(), addressIO.streetNumber);
        setValue(address.streetName(), addressIO.streetName);
        setValue(address.suiteNumber(), addressIO.unitNumber);

        return address;
    }
}
