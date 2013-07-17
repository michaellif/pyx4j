/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.util.CanadianStreetAddressParser;
import com.propertyvista.server.common.util.StreetAddressParser;
import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

/**
 * Maps buildings information from YARDI System to domain entities.
 * 
 * @author Mykola
 * 
 */
public class BuildingsMapper {

    private final static Logger log = LoggerFactory.getLogger(BuildingsMapper.class);

    /**
     * Maps property from YARDI System to building
     * 
     * @param provinces
     *            the existing provinces
     * @param property
     *            the property to map
     * @return the building
     */
    public Building map(List<Province> provinces, Property property) {
        PropertyID propertyID = property.getPropertyID().get(0);
        Building building = EntityFactory.create(Building.class);

        Identification identification = propertyID.getIdentification();
        building.propertyCode().setValue(identification.getPrimaryID());
        building.marketing().name().setValue(identification.getMarketingName());

        // address
        Address addressImported = propertyID.getAddress().get(0);

        StreetAddress streetAddress = null;
        try {
            if (StringUtils.isEmpty(addressImported.getAddress1())) {
                throw new IllegalArgumentException("imported address for property '" + propertyID.getIdentification().getPrimaryID()
                        + "' has no street address");
            }

            StreetAddressParser streetAddressParser = null;
            // TODO instantiate address parser according to the building country
            streetAddressParser = new CanadianStreetAddressParser();
            streetAddress = streetAddressParser.parse(addressImported.getAddress1(), null);
        } catch (Throwable e) {
            log.warn("failed to parse street address for property '" + propertyID.getIdentification().getPrimaryID() + "'", e);
            streetAddress = new StreetAddress(null, StringUtils.EMPTY, addressImported.getAddress1(), StreetType.other, null);
        }

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetAddress.streetNumber);
        address.streetName().setValue(streetAddress.streetName);
        address.streetType().setValue(streetAddress.streetType);
        address.streetDirection().setValue(streetAddress.streetDirection);
        address.city().setValue(addressImported.getCity());

        address.province().code().setValue(addressImported.getState());

        String importedCountry = addressImported.getCountry();
        address.country().name().setValue(StringUtils.isEmpty(importedCountry) ? getCountry(provinces, addressImported.getState()) : importedCountry);

        address.postalCode().setValue(addressImported.getPostalCode());

        building.info().address().set(address);

        return building;
    }

    private String getCountry(List<Province> provinces, String stateCode) {
        for (Province province : provinces) {
            if (StringUtils.equals(province.code().getValue(), stateCode)) {
                return province.country().name().getValue();
            }
        }
        return null;
    }

    private StreetType getStreetType(String typeName) {
        for (StreetType type : StreetType.values()) {
            if (type.toString().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return StreetType.other;
    }

}
