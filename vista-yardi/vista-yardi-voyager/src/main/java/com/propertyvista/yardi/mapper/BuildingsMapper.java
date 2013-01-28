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

import java.util.ArrayList;
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

/**
 * Maps buildings information from YARDI System to domain entities.
 * 
 * @author Mykola
 * 
 */
public class BuildingsMapper {

    private final static Logger log = LoggerFactory.getLogger(BuildingsMapper.class);

    /**
     * Maps properties from YARDI System to building
     * 
     * @param provinces
     *            the existing provinces
     * @param properties
     *            the properties to map
     * @return the properties list
     */
    public List<Building> map(List<Province> provinces, List<Property> properties) {
        List<Building> buildings = new ArrayList<Building>();
        for (Property property : properties) {
            PropertyID currentPropertyID = null;
            try {
                for (PropertyID propertyID : property.getPropertyID()) {
                    currentPropertyID = propertyID;
                    Building building = map(provinces, propertyID);
                    buildings.add(building);
                }
            } catch (Exception e) {
                log.error("Error during imported building '{}' mapping", getPropertyId(currentPropertyID), e);
            }
        }

        return buildings;
    }

    private Building map(List<Province> provinces, PropertyID propertyID) {
        Building building = EntityFactory.create(Building.class);

        Identification identification = propertyID.getIdentification();
        building.propertyCode().setValue(identification.getPrimaryID());
        building.marketing().name().setValue(identification.getMarketingName());

        // address
        Address addressImported = propertyID.getAddress().get(0);
        String street = StringUtils.isNotEmpty(addressImported.getAddress1()) ? addressImported.getAddress1() : StringUtils.EMPTY;

        String streetName = street;
        String streetNumber = StringUtils.EMPTY;
        StreetType streetType = StreetType.other;

        String[] streetTokens = street.split("\\s+", 2);
        if (streetTokens.length == 2) {
            streetNumber = streetTokens[0];
            streetName = streetTokens[1];

            //extract street type information
            String[] tkns = streetName.split("\\s+");
            if (tkns.length > 1) {
                String type = tkns[tkns.length - 1];
                streetType = getStreetType(type);
                if (streetType != StreetType.other) {
                    streetName = StringUtils.substringBeforeLast(streetName, type).trim();
                }
            }
        }

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);
        address.streetType().setValue(streetType);
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

    private String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification().getPrimaryID();
    }

}
