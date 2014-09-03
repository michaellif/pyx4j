/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.UnitIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class RSPropertyServiceTest extends RSOapiTestBase {

    private Building building;

    private Floorplan floorplan;

    private AptUnit unit;

    @Override
    protected Class<? extends Application> getServiceApplication() {
        return OapiRsApplication.class;
    }

    @Override
    protected void preloadData() {
        super.preloadData();
        building = getBuilding();
        Persistence.ensureRetrieve(building.floorplans(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.units(), AttachLevel.Attached);
        if (building.floorplans().size() < 1) {
            // set building details
            floorplan = EntityFactory.create(Floorplan.class);
            floorplan.building().set(building);
            floorplan.name().setValue("2bdrm");
            Persistence.service().persist(floorplan);

            unit = EntityFactory.create(AptUnit.class);
            unit.building().set(building);
            unit.info().number().setValue("1");
            unit.floorplan().set(floorplan);
            Persistence.service().persist(unit);

            Persistence.service().commit();
        } else {
            floorplan = new ArrayList<Floorplan>(building.floorplans()).get(0);
            unit = new ArrayList<AptUnit>(building.units()).get(0);
        }
    }

    @Test
    public void testGetBuildings() {
        BuildingListIO response = target("buildings").queryParam("province", "Ontario").request().get(BuildingListIO.class);
        Assert.assertEquals(1, response.buildingList.size());
    }

    @Test
    public void testGetBuildingsByProvince_NonExistingProvince() {
        BuildingListIO buildings = target("buildings").queryParam("province", "NonExisting").request().get(BuildingListIO.class);
        Assert.assertTrue(buildings.buildingList.isEmpty());
    }

    @Test
    public void testGetBuildingByPropertyCode_NonExistingPropertyCode() {
        Response response = target("buildings/MockCode").request().get();
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllUnitsByPropertyCode_NonExistingPropertyCode() {
        GenericType<List<UnitIO>> gt = new GenericType<List<UnitIO>>() {
        };
        List<UnitIO> units = target("buildings/MockCode/units").request().get(gt);
        Assert.assertTrue(units.isEmpty());
    }

    @Test
    public void testGetUnitByNumber_NonExistingPropertyCodeAndUnitNumber() {
        Response response = target("buildings/MockCode/units/MockNumber").request().get(Response.class);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUnits() {
        UnitIO unit = new UnitIO();
        unit.propertyCode = building.propertyCode().getValue();
        unit.floorplanName = new StringIO(floorplan.name().getValue());
        unit.number = "2";
        unit.baths = new IntegerIO(1);
        unit.beds = new IntegerIO(2);

        Response response = target("buildings/MockCode/units/updateUnit").request(MediaType.APPLICATION_XML).post(Entity.xml(unit));
        // requires preloaded building
        //        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }
}
