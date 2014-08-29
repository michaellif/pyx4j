/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.processing.PropertyServiceProcessor;
import com.propertyvista.oapi.xml.StringIO;

@WebService(endpointInterface = "com.propertyvista.oapi.ws.WSPropertyService")
public class WSPropertyServiceImpl implements WSPropertyService {

    static private Map<String, BuildingIO> buildings = new HashMap<String, BuildingIO>();

    @Override
    public void createBuilding(BuildingIO building) {
        buildings.put(building.propertyCode, building);
    }

    @Override
    public BuildingIO getBuildingByPropertyCode(String propertyCode) {
        return buildings.get(propertyCode);
    }

    @Override
    public BuildingsIO listAllBuildings() {
        return PropertyServiceProcessor.getBuildings();
    }

    @Override
    public void createUnit(String unitNumber, String floorplanName, String propertyCode) {
        BuildingIO building = buildings.get(propertyCode);
        if (building != null) {
            UnitIO newUnit = new UnitIO();
            newUnit.number = unitNumber;
            newUnit.floorplanName = new StringIO(floorplanName);
            newUnit.propertyCode = propertyCode;
            building.units.add(newUnit);
        } else {
            //TODO error, create building before putting units in it
        }

    }

    @Override
    public List<UnitIO> listAllBuildingUnits(String buildingCode) {
        return PropertyServiceProcessor.getUnitsByPropertyCode(buildingCode);
    }

}
