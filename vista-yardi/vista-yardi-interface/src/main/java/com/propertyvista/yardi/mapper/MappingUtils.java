/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yardi.entity.mits.Address;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.util.CanadianStreetAddressParser;
import com.propertyvista.server.common.util.StreetAddressParser;
import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

public class MappingUtils {

    public static Building getBuilding(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);
        List<Building> buildings = Persistence.service().query(criteria);
        return !buildings.isEmpty() ? buildings.get(0) : null;
    }

    public static List<Province> getProvinces() {
        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);
        criteria.asc(criteria.proto().name());
        return Persistence.service().query(criteria);
    }

    public static String getCountry(String stateCode) {
        for (Province province : getProvinces()) {
            if (StringUtils.equals(province.code().getValue(), stateCode)) {
                return province.country().name().getValue();
            }
        }
        return null;
    }

    public static void ensureCountryOfOperation(Building building) throws YardiServiceException {
        Pmc pmc = VistaDeployment.getCurrentPmc();
        String yardiCountry = building.info().address().country().name().getValue();
        String countryOfOperation = pmc.features().countryOfOperation().getValue().toString();
        if (yardiCountry == null) {
            throw new YardiServiceException("Country not set for this building. Building not imported.");
        } else if (!yardiCountry.equals(countryOfOperation)) {
            throw new YardiServiceException("Country for this building does not match country of operation");
        }
    }

    public static AddressStructured getAddress(Address mitsAddress) {
        StreetAddress streetAddress = null;
        // TODO instantiate address parser according to the building country
        try {
            StreetAddressParser streetAddressParser = new CanadianStreetAddressParser();
            streetAddress = streetAddressParser.parse(mitsAddress.getAddress1(), null);
        } catch (Throwable e) {
            streetAddress = new StreetAddress(null, StringUtils.EMPTY, mitsAddress.getAddress1(), StreetType.other, null);
        }

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetAddress.streetNumber);
        address.streetName().setValue(streetAddress.streetName);
        address.streetType().setValue(streetAddress.streetType);
        address.streetDirection().setValue(streetAddress.streetDirection);
        address.city().setValue(mitsAddress.getCity());

        address.province().code().setValue(mitsAddress.getState());

        String importedCountry = mitsAddress.getCountry();
        address.country().name().setValue(StringUtils.isEmpty(importedCountry) ? MappingUtils.getCountry(mitsAddress.getState()) : importedCountry);

        address.postalCode().setValue(mitsAddress.getPostalCode());

        return address;
    }
}
