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
 * @author dmitry
 */
package com.propertyvista.crm.server.openapi.model.util;

import java.util.List;

import com.propertyvista.crm.server.openapi.model.AddressRS;
import com.propertyvista.crm.server.openapi.model.BuildingInfoRS;
import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.FloorplansRS;
import com.propertyvista.crm.server.openapi.model.MarketingRS;
import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class Converter {
    public static BuildingsRS convertBuildings(List<Building> from) {
        BuildingsRS to = new BuildingsRS();
        for (Building building : from) {
            to.buildings.add(convertBuilding(building));
        }
        return to;
    }

    public static BuildingRS convertBuilding(Building from) {
        BuildingRS to = new BuildingRS();

        to.info = convertBuildingInfo(from.info());
        to.marketing = convertMarketing(from.marketing());

        return to;
    }

    public static MarketingRS convertMarketing(Marketing from) {
        MarketingRS to = new MarketingRS();

        to.description = from.description().getStringView();

        return to;
    }

    public static BuildingInfoRS convertBuildingInfo(BuildingInfo from) {
        BuildingInfoRS to = new BuildingInfoRS();

        to.address = convertAddress(from.address());

        return to;
    }

    public static AddressRS convertAddress(Address from) {
        AddressRS to = new AddressRS();

        to.streetName = from.streetName().getStringView();
        to.streetNumber = from.streetNumber().getStringView();
        to.streetType = from.streetType().getStringView();
        to.city = from.city().getStringView();
        to.province = from.province().getStringView();
        to.postalCode = from.postalCode().getStringView();
        to.country = from.country().getStringView();

        return to;
    }

    public static FloorplansRS convertFloorplans(List<Floorplan> from) {
        FloorplansRS to = new FloorplansRS();

        for (Floorplan floorplan : from) {
            to.floorplans.add(convertFloorplan(floorplan));
        }

        return to;
    }

    public static FloorplanRS convertFloorplan(Floorplan from) {
        FloorplanRS to = new FloorplanRS();

        to.name = from.name().getStringView();
        to.description = from.description().getStringView();
        to.floorCount = from.floorCount().getValue();

        return to;
    }
}
