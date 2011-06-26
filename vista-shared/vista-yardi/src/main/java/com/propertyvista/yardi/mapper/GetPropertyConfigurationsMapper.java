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

import com.propertyvista.domain.Address;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;

public class GetPropertyConfigurationsMapper {

    // TODO this later will go into the Model
    private List<Building> buildings = new ArrayList<Building>();

    public void map(Properties properties) {
        for (Property property : properties.getProperties()) {
            Building building = map(property);
            buildings.add(building);
        }
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

        building.info().propertyCode().setValue(property.getCode());
        building.marketing().name().setValue(property.getMarketingName());

        // address
        String street = property.getAddressLine1();
        String streetNumber = street.substring(0, street.indexOf(' '));
        String streetName = street.substring(streetNumber.length() + 1);

        Address address = EntityFactory.create(Address.class);

        address.streetNumber().setValue(streetNumber);
        address.streetName().setValue(streetName);
        address.city().setValue(property.getCity());
        address.province().set(SharedData.findProvinceByCode(property.getState()));
        address.country().set(address.province().country());
        address.postalCode().setValue(property.getPostalCode());

        address.addressType().setValue(Address.AddressType.property);

        building.info().address().set(address);

        return building;
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
