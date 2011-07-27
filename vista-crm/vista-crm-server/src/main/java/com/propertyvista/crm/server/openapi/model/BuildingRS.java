/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.domain.property.asset.building.Building;

/**
 * @author michaellif
 * 
 */

@XmlRootElement(name = "building")
public class BuildingRS {

    private Building building;

    public BuildingRS() {
    }

    public BuildingRS(Building building) {
        this.building = building;
    }

    @XmlElement(name = "address")
    public AddressRS getAddress() {
        return new AddressRS(building.info().address());
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return building.marketing().description().getStringView();
    }

//    @XmlElement(name = "amenity")
//    public List<String> getAmenities() {
//        return building.marketing().getStringView();
//    }

    @Override
    public String toString() {
        return building.getStringView();
    }
}
