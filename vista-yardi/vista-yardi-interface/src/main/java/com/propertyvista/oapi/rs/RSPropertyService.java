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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.propertyvista.oapi.PropertyService;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;

//http://localhost:8888/vista/interfaces/oapi/rs/buildings
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/rs/buildings

/**
 * 
 * interfaces/oapi/rs/buildings/<buildingCode>/units/<unitId>
 * 
 * interfaces/oapi/rs/buildings/ - all buildings
 * 
 * interfaces/oapi/rs/buildings?city=Toronto - all buildings in Toronto
 * 
 * interfaces/oapi/rs/buildings/<buildingCode>/units - all units
 * 
 * interfaces/oapi/rs/buildings/<buildingCode>/units?floorplan=3bdrm - all units for which floorplanName = 3bdrm
 * 
 * 
 * 
 */
@Path("/buildings")
public class RSPropertyService {

    public RSPropertyService() {
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public BuildingsIO getBuildings(@QueryParam("province") String province) {
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

    @GET
    @Path("/{propertyCode}")
    @Produces({ MediaType.APPLICATION_XML })
    public BuildingIO getBuildingByPropertyCode(@PathParam("propertyCode") String propertyCode) {

        return PropertyService.getBuildingByPropertyCode(propertyCode);
    }

    @GET
    @Path("/{propertyCode}/units")
    @Produces({ MediaType.APPLICATION_XML })
    public List<UnitIO> getAllUnitsByPropertyCode(@PathParam("propertyCode") String propertyCode, @QueryParam("floorplan") String floorplan) {

        List<UnitIO> allUnits = PropertyService.getUnitsByPropertyCode(propertyCode);
        List<UnitIO> filteredUnits = new ArrayList<UnitIO>();
        for (UnitIO unit : allUnits) {
            if (unit.floorplanName.equals(floorplan)) {
                filteredUnits.add(unit);
            }
        }
        return filteredUnits;
    }

    @GET
    @Path("/{propertyCode}/units/{unitNumber}")
    @Produces({ MediaType.APPLICATION_XML })
    public UnitIO getUnitByNumber(@PathParam("propertyCode") String propertyCode, @PathParam("unitNumber") String unitNumber) {

        return PropertyService.getUnitByNumber(propertyCode, unitNumber);

    }
}
