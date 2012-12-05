/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.propertyvista.oapi.PropertyService;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;

/**
 * Implementation of {@link RSPropertyService}
 * 
 */
@Path("/buildings")
public class RSPropertyServiceImpl implements RSPropertyService {

    @Override
    public BuildingsIO getBuildings(String province) {
        BuildingsIO allBuildings = PropertyService.getBuildings();
        if (province == null) {
            return allBuildings;
        }
        BuildingsIO filteredBuildings = new BuildingsIO();

        for (BuildingIO building : allBuildings.buildings) {
            if (building.info.address.province.equals(province)) {
                filteredBuildings.buildings.add(building);
            }
        }
        return filteredBuildings;
    }

    @Override
    public BuildingIO getBuildingByPropertyCode(String propertyCode) {
        return PropertyService.getBuildingByPropertyCode(propertyCode);
    }

    @Override
    public List<UnitIO> getAllUnitsByPropertyCode(String propertyCode, String floorplan) {
        List<UnitIO> allUnits = PropertyService.getUnitsByPropertyCode(propertyCode);
        List<UnitIO> filteredUnits = new ArrayList<UnitIO>();
        for (UnitIO unit : allUnits) {
            if (unit.floorplanName.equals(floorplan)) {
                filteredUnits.add(unit);
            }
        }
        return filteredUnits;
    }

    @Override
    public UnitIO getUnitByNumber(String propertyCode, String unitNumber) {
        return PropertyService.getUnitByNumber(propertyCode, unitNumber);
    }

    @Override
    public void updateBuilding(BuildingIO buildingIO) {
        try {
            PropertyService.updateBuilding(buildingIO);
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUnit(String propertyCode, UnitIO unitIO) {
        // TODO Auto-generated method stub
    }

}
