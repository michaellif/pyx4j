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
package com.propertyvista.oapi.rs;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;
import com.propertyvista.test.mock.models.BuildingDataModel;

@Ignore
public class RSPropertyServiceTest extends RSOapiTestBase {

    @Override
    protected Class<?> getServiceClass() {
        return RSPropertyService.class;
    }

    @Override
    protected void preloadData() {
        super.preloadData();
        getDataModel(BuildingDataModel.class).addBuilding();
    }

    @Test
    public void testGetBuildings() {
        BuildingsIO buildings = target().path("buildings").queryParam("province", "Ontario").request().get(BuildingsIO.class);
        Assert.assertEquals(buildings.buildings.size(), 1);
    }

    @Test
    public void testGetBuildingsByProvince_NonExistingProvince() {
        BuildingsIO buildings = target().path("buildings;province=MockProvince").request().get(BuildingsIO.class);
        Assert.assertTrue(buildings.buildings.isEmpty());
    }

    @Test
    public void testGetBuildingByPropertyCode_NonExistingPropertyCode() {
        Response response = target().path("buildings/MockCode").request().get();
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllUnitsByPropertyCode_NonExistingPropertyCode() {
        GenericType<List<UnitIO>> gt = new GenericType<List<UnitIO>>() {
        };
        List<UnitIO> units = target().path("buildings/MockCode/units").request().get(gt);
        Assert.assertTrue(units.isEmpty());
    }

    @Test
    public void testGetUnitByNumber_NonExistingPropertyCodeAndUnitNumber() {
        Response response = target().path("buildings/MockCode/units/MockNumber").request().get(Response.class);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUnits() {
        UnitIO unit = new UnitIO();
        unit.number = "1";
        unit.propertyCode = "testCode";
        unit.baths = new IntegerIO(1);
        unit.beds = new IntegerIO(2);
        unit.floorplanName = new StringIO("2bdrm");

        Response response = target("buildings/MockCode/units/updateUnit").request(MediaType.APPLICATION_XML).post(Entity.xml(unit));
        // requires preloaded building
        //        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }
}
