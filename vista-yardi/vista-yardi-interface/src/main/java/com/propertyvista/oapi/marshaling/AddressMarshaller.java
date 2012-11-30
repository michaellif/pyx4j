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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.types.StreetTypeIO;
import com.propertyvista.oapi.xml.Action;
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
    public AddressIO unmarshal(AddressStructured address) {
        AddressIO addressIO = new AddressIO();
        addressIO.country = new StringIO(address.country().name().getValue());
        addressIO.province = new StringIO(address.province().name().getValue());
        addressIO.city = new StringIO(address.city().getValue());
        addressIO.postalCode = new StringIO(address.postalCode().getValue());
        addressIO.streetNumber = new StringIO(address.streetNumber().getValue());
        addressIO.streetName = new StringIO(address.streetName().getValue());
        addressIO.streetType = new StreetTypeIO(address.streetType().getValue());
        return addressIO;
    }

    @Override
    public AddressStructured marshal(AddressIO addressIO) throws Exception {
        if (addressIO == null) {
            return null;
        }
        AddressStructured address = EntityFactory.create(AddressStructured.class);
        if (addressIO.getAction() == Action.nil) {
            address.set(null);
        } else {
            address.country().set(getCountry(addressIO.country.value));
            address.province().set(getProvince(addressIO.province.value));
            address.city().setValue(addressIO.city.value);
            address.postalCode().setValue(addressIO.postalCode.value);
            address.streetNumber().setValue(addressIO.streetNumber.value);
            address.streetName().setValue(addressIO.streetName.value);
            address.streetType().setValue(addressIO.streetType.value);
        }
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
