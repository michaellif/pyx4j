/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.util;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class Converter {

    public static FloorplanDTO convert(Floorplan from) {
        FloorplanDTO to = EntityFactory.create(FloorplanDTO.class);

        to.name().setValue(from.name().getValue());
        to.area().setValue(from.minArea().getValue());

        return to;
    }

    public static AptUnitDTO convert(AptUnit from) {
        AptUnitDTO to = EntityFactory.create(AptUnitDTO.class);

        to.id().set(from.id());

        // iEntity
        to.floorplan().set(convert(from.marketing().floorplan()));
        to.infoDetails().setValue(from.info().details().getStringView());
        to.amenities().setValue(from.amenities().getStringView());
        to.concessions().setValue(from.concessions().getStringView());
        to.utilities().setValue(from.info().utilities().getStringView());
        to.addOns().setValue(from.addOns().getStringView());

        //TODO VS
        //to.status().set(from.status());

        // primitives
        to.unitType().setValue(from.info().type().getStringView());
        to.area().setValue(from.info().area().getValue());
        to.bathrooms().setValue(from.info().bathrooms().getValue());
        to.bedrooms().set(from.info().bedrooms());

        to.unitRent().set(from.financial().unitRent());

// TODO calculate somehow (!?) from current Unit data those values:          
        to.requiredDeposit().setValue(-77.0);

        to.avalableForRent().set(from.avalableForRent());

        return to;
    }

    public static PropertyDTO convert(Building from) {
        PropertyDTO to = EntityFactory.create(PropertyDTO.class);
        to.id().set(from.id());
        to.address().street1().set(from.info().address().streetName());
        //to.location().setValue(value);

        // List of Floorplans
        //TODO get this from building
        to.size().add("3 Bedroom");
        to.size().add("Bachelor");

        // List of amenities
        for (BuildingAmenity ba : from.amenities()) {
            to.details().add(ba.type().getStringView());
        }

        return to;
    }
}
