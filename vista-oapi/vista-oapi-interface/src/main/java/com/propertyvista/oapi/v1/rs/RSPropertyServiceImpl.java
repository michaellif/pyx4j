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
package com.propertyvista.oapi.v1.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.UnitIO;
import com.propertyvista.oapi.v1.model.UnitListIO;
import com.propertyvista.oapi.v1.processing.PropertyServiceProcessor;
import com.propertyvista.oapi.v1.service.PropertyService;

//http://localhost:8888/vista/interfaces/oapi/v1/rs/application.wadl
//http://localhost:8888/vista/interfaces/oapi/v1/rs/buildings
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/v1/rs/buildings

/**
 * 
 * interfaces/oapi/v1/rs/buildings/<buildingCode>/units/<unitId>
 * 
 * interfaces/oapi/v1/rs/buildings/ - all buildings
 * 
 * interfaces/oapi/v1/rs/buildings?city=Toronto - all buildings in Toronto
 * 
 * interfaces/oapi/v1/rs/buildings/<buildingCode>/units - all units
 * 
 * interfaces/oapi/v1/rs/buildings/<buildingCode>/units?floorplan=3bdrm - all units for which floorplanName = 3bdrm
 * 
 * interfaces/oapi/v1/rs/buildings/updateBuilding - updates/creates building
 * 
 * interfaces/oapi/v1/rs/buildings/<buildingCode>/units/updateUnit - updates/creates unit for corresponding building
 * 
 */

@Path("/buildings")
public class RSPropertyServiceImpl implements PropertyService {

    private static I18n i18n = I18n.get(RSPropertyServiceImpl.class);

    @Override
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public BuildingListIO getBuildingList(@QueryParam("province") String province) {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.List);
        try {
            BuildingListIO allBuildings = processor.getBuildings();
            if (province == null) {
                return allBuildings;
            }
            BuildingListIO filteredBuildings = new BuildingListIO();

            for (BuildingIO building : allBuildings.buildingList) {
                if (building.address.province.getValue().equals(province)) {
                    filteredBuildings.buildingList.add(building);
                }
            }
            return filteredBuildings;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @GET
    @Path("/{propertyCode}")
    @Produces({ MediaType.APPLICATION_XML })
    public BuildingIO getBuilding(@PathParam("propertyCode") String propertyCode) {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.Read);
        try {
            BuildingIO buildingIO = processor.getBuildingByPropertyCode(propertyCode);
            if (buildingIO == null) {
                throw new RuntimeException(i18n.tr("Building with propertyCode={0} not found", propertyCode));
            }
            return buildingIO;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @GET
    @Path("/{propertyCode}/units")
    @Produces({ MediaType.APPLICATION_XML })
    public UnitListIO getUnitList(@PathParam("propertyCode") String propertyCode, @QueryParam("floorplan") String floorplan) {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.List);
        try {
            UnitListIO allUnits = processor.getUnitsByPropertyCode(propertyCode);
            UnitListIO filteredUnits = new UnitListIO();
            for (UnitIO unit : allUnits.getList()) {
                if (unit.floorplanName.equals(floorplan)) {
                    filteredUnits.add(unit);
                }
            }
            return filteredUnits;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @GET
    @Path("/{propertyCode}/units/{unitNumber}")
    @Produces({ MediaType.APPLICATION_XML })
    public UnitIO getUnitByNumber(@PathParam("propertyCode") String propertyCode, @PathParam("unitNumber") String unitNumber) {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.Read);
        try {
            UnitIO unitIO = processor.getUnitByNumber(propertyCode, unitNumber);
            if (unitIO == null) {
                throw new RuntimeException(i18n.tr("Unit with propertyCode={0} and unitNumber={1} not found", propertyCode, unitNumber));
            }
            return unitIO;
        } finally {
            processor.destroy();
        }
    }

    @Override
    @POST
    @Path("/updateBuilding")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateBuilding(BuildingIO buildingIO) throws Exception {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.Write);
        try {
            processor.updateBuilding(buildingIO);
        } finally {
            processor.destroy();
        }
    }

    @Override
    @POST
    @Path("/{propertyCode}/units/updateUnit")
    @Consumes({ MediaType.APPLICATION_XML })
    public void updateUnit(@PathParam("propertyCode") String propertyCode, UnitIO unitIO) throws Exception {
        PropertyServiceProcessor processor = new PropertyServiceProcessor(ServiceType.Write);
        try {
            processor.updateUnit(unitIO);
        } finally {
            processor.destroy();
        }
    }

}
