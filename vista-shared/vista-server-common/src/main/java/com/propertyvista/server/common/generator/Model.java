/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.common.generator;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.FloorplanDTO;

public class Model {

    private final List<Building> buildings = new ArrayList<Building>();

    private final List<UnitRelatedData> units = new ArrayList<UnitRelatedData>();

    private final List<FloorplanDTO> floorplans = new ArrayList<FloorplanDTO>();

    private final List<BuildingAmenity> buildingAmenities = new ArrayList<BuildingAmenity>();

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<UnitRelatedData> getUnits() {
        return units;
    }

    public List<FloorplanDTO> getFloorplans() {
        return floorplans;
    }

    public List<BuildingAmenity> getBuildingAmenities() {
        return buildingAmenities;
    }
}
