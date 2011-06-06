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
package com.propertyvista.portal.server.importer;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.AptUnitDTO;

public class Model {

    private final List<Building> buildings = new ArrayList<Building>();

    private final List<AptUnitDTO> units = new ArrayList<AptUnitDTO>();

    private final List<Floorplan> floorplans = new ArrayList<Floorplan>();

    private final List<BuildingAmenity> buildingAmenities = new ArrayList<BuildingAmenity>();

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<AptUnitDTO> getUnits() {
        return units;
    }

    public List<Floorplan> getFloorplans() {
        return floorplans;
    }

    public List<BuildingAmenity> getBuildingAmenities() {
        return buildingAmenities;
    }
}
