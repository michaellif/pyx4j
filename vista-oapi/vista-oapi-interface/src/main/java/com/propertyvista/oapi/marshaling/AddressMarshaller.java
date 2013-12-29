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

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.types.StreetTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class AddressMarshaller implements Marshaller<AddressStructured, AddressIO> {

    private static class SingletonHolder {
        public static final AddressMarshaller INSTANCE = new AddressMarshaller();
    }

    private AddressMarshaller() {
    }

    public static AddressMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public AddressIO marshal(AddressStructured address) {
        if (address == null || address.isNull()) {
            return null;
        }
        AddressIO addressIO = new AddressIO();

        addressIO.country = MarshallerUtils.createIo(StringIO.class, address.country().name());
        addressIO.province = MarshallerUtils.createIo(StringIO.class, address.province().name());
        addressIO.city = MarshallerUtils.createIo(StringIO.class, address.city());
        addressIO.postalCode = MarshallerUtils.createIo(StringIO.class, address.postalCode());
        addressIO.streetNumber = MarshallerUtils.createIo(StringIO.class, address.streetNumber());
        addressIO.streetName = MarshallerUtils.createIo(StringIO.class, address.streetName());
        addressIO.streetType = MarshallerUtils.createIo(StreetTypeIO.class, address.streetType());
        return addressIO;
    }

    @Override
    public AddressStructured unmarshal(AddressIO addressIO) {
        AddressStructured address = EntityFactory.create(AddressStructured.class);

        if (addressIO.country != null) {
            address.country().set(getCountry(addressIO.country.getValue()));
        }
        if (addressIO.province != null) {
            address.province().set(getProvince(addressIO.province.getValue()));
        }

        MarshallerUtils.setValue(address.city(), addressIO.city);
        MarshallerUtils.setValue(address.postalCode(), addressIO.postalCode);
        MarshallerUtils.setValue(address.streetNumber(), addressIO.streetNumber);
        MarshallerUtils.setValue(address.streetName(), addressIO.streetName);
        MarshallerUtils.setValue(address.streetType(), addressIO.streetType);

        return address;
    }

    private Province getProvince(String name) {
        EntityQueryCriteria<Province> provinceCriteria = EntityQueryCriteria.create(Province.class);
        provinceCriteria.add(PropertyCriterion.eq(provinceCriteria.proto().name(), name));
        List<Province> provinces = Persistence.service().query(provinceCriteria);
        if (provinces.size() > 0) {
            return provinces.get(0);
        } else {
            return null;
        }
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
