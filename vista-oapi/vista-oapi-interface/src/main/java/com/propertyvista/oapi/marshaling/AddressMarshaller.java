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

import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.xml.StringIO;

public class AddressMarshaller implements Marshaller<InternationalAddress, AddressIO> {

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

        addressIO.country = MarshallerUtils.createIo(StringIO.class, address.country().name());
        addressIO.province = MarshallerUtils.createIo(StringIO.class, address.province());
        addressIO.city = MarshallerUtils.createIo(StringIO.class, address.city());
        addressIO.postalCode = MarshallerUtils.createIo(StringIO.class, address.postalCode());
        addressIO.addressLine1 = MarshallerUtils.createIo(StringIO.class, address.addressLine1());
        addressIO.addressLine2 = MarshallerUtils.createIo(StringIO.class, address.addressLine2());
        return addressIO;
    }

    @Override
    public InternationalAddress unmarshal(AddressIO addressIO) {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);

        if (addressIO.country != null) {
            address.country().set(getCountry(addressIO.country.getValue()));
        }

        MarshallerUtils.setValue(address.province(), addressIO.province);
        MarshallerUtils.setValue(address.city(), addressIO.city);
        MarshallerUtils.setValue(address.postalCode(), addressIO.postalCode);
        MarshallerUtils.setValue(address.addressLine1(), addressIO.addressLine1);
        MarshallerUtils.setValue(address.addressLine2(), addressIO.addressLine2);

        return address;
    }

    private Country getCountry(String name) {
        EntityQueryCriteria<Country> countryCriteria = EntityQueryCriteria.create(Country.class);
        countryCriteria.add(PropertyCriterion.eq(countryCriteria.proto().name(), name));
        List<Country> countries = Persistence.service().query(countryCriteria);
        if (countries.size() > 0) {
            return countries.get(0);
        } else {
            return null;
        }
    }
}
