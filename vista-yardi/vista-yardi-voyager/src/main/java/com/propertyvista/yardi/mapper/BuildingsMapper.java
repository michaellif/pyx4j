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
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.YardiConstants;

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
     * @param properties
     *            the properties to map
     * @return the properties list
     */
    public List<Building> map(List<Property> properties) {
        List<Building> buildings = new ArrayList<Building>();
        for (Property property : properties) {
            PropertyID currentPropertyID = null;
            try {
                for (PropertyID propertyID : property.getPropertyID()) {
                    currentPropertyID = propertyID;
                    Building building = map(propertyID);
                    buildings.add(building);
                }
            } catch (Exception e) {
                log.error(String.format("Error during imported building %s mapping", getPropertyId(currentPropertyID)), e);
            }
        }

        return buildings;
    }

    private Building map(PropertyID propertyID) {
        Building building = EntityFactory.create(Building.class);

        Identification identification = propertyID.getIdentification();
        building.propertyCode().setValue(identification.getPrimaryID());
        building.marketing().name().setValue(identification.getMarketingName());

        // address
        Address addressImported = propertyID.getAddress().get(0);
        String street = addressImported.getAddress1();
        String streetNumber = street.substring(0, street.indexOf(' '));
        String streetName = street.substring(streetNumber.length() + 1);

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);
        address.city().setValue(addressImported.getCity());

        address.province().code().setValue(addressImported.getState());
        address.country().name()
                .setValue(StringUtils.isEmpty(addressImported.getCountry()) ? YardiConstants.YARDI_DEFAULT_COUNTRY : addressImported.getCountry());
        address.postalCode().setValue(addressImported.getPostalCode());

        building.info().address().set(address);

        return building;
    }

    private String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification().getPrimaryID();
    }

}
