/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.bean.Property;

public class GetPropertyConfigurationsMapper {

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
            Building building = map(property);
            buildings.add(building);
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

        building.propertyCode().setValue(property.getCode());
        building.marketing().name().setValue(property.getMarketingName());

        // address
        String street = property.getAddressLine1();
        String streetNumber = street.substring(0, street.indexOf(' '));
        String streetName = street.substring(streetNumber.length() + 1);

        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);
        address.city().setValue(property.getCity());
        //TODO fix me
        // address.province().set(findProvinceByCode(property.getState()));
        address.country().set(address.province().country());
        address.postalCode().setValue(property.getPostalCode());

        building.info().address().set(address);

        return building;
    }

}
