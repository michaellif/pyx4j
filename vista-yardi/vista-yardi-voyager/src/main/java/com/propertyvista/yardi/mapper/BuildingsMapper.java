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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.bean.mits.Address;
import com.propertyvista.yardi.bean.mits.Identification;
import com.propertyvista.yardi.bean.resident.Property;

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
            try {
                Building building = map(property);
                buildings.add(building);
            } catch (Exception e) {
                log.error(String.format("Error during imported building %s mapping", getPropertyId(property)), e);
            }
        }

        return buildings;
    }

    /**
     * Fields that get mapped are:
     * 
     * building.info:
     * propertyCode
     * address
     * 
     * building.marketing:
     * name
     * 
     * @param property
     * @return
     */
    public Building map(Property property) {
        Building building = EntityFactory.create(Building.class);

        Identification identification = property.getPropertyId().getIdentification();
        building.propertyCode().setValue(identification.getPrimaryId());
        building.marketing().name().setValue(identification.getMarketingName());

        // address
        Address addressImported = property.getPropertyId().getAddress();
        String street = addressImported.getAddress1();
        String streetNumber = street.substring(0, street.indexOf(' '));
        String streetName = street.substring(streetNumber.length() + 1);

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);
        address.city().setValue(addressImported.getCity());

        //TODO fix me
        // address.province().set(findProvinceByCode(property.getState()));
        address.country().set(address.province().country());
        address.postalCode().setValue(addressImported.getPostalCode());

        building.info().address().set(address);

        return building;
    }

    private String getPropertyId(Property property) {
        return property.getPropertyId() != null ? getPropertyId(property.getPropertyId().getIdentification()) : null;
    }

    private String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryId()) : null;
    }
}
