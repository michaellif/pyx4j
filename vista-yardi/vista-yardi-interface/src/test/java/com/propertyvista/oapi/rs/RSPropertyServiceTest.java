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

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class RSPropertyServiceTest extends RSOapiTestBase {

    public RSPropertyServiceTest() throws Exception {
        super("com.propertyvista.oapi.rs");
    }

    @Test
    public void testGetBuildings() {
        WebResource webResource = resource();
        BuildingsIO buildings = webResource.path("buildings").get(BuildingsIO.class);
    }

    @Test
    public void testGetBuildingsByProvince_NonExistingProvince() {
        WebResource webResource = resource();
        BuildingsIO buildings = webResource.path("buildings;province=MockProvince").get(BuildingsIO.class);
        Assert.assertTrue(buildings.buildings.isEmpty());
    }

    @Test
    public void testGetBuildingByPropertyCode_NonExistingPropertyCode() {
        WebResource webResource = resource();
        ClientResponse response = webResource.path("buildings/MockCode").get(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.INTERNAL_SERVER_ERROR, response.getClientResponseStatus());
    }

    @Test
    public void testGetAllUnitsByPropertyCode_NonExistingPropertyCode() {
        WebResource webResource = resource();
        GenericType<List<UnitIO>> gt = new GenericType<List<UnitIO>>() {
        };
        List<UnitIO> units = webResource.path("buildings/MockCode/units").get(gt);
        Assert.assertTrue(units.isEmpty());
    }

    @Test
    public void testGetUnitByNumber_NonExistingPropertyCodeAndUnitNumber() {
        WebResource webResource = resource();
        ClientResponse response = webResource.path("buildings/MockCode/units/MockNumber").get(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.INTERNAL_SERVER_ERROR, response.getClientResponseStatus());
    }

    //@Test
    public void testUpdateBuilding() {
        preloadBuilding("testCode");
        WebResource webResource = resource();

        BuildingIO building = new BuildingIO("testCode");
        ClientResponse response = webResource.path("buildings/updateBuilding").accept(MediaType.APPLICATION_XML).post(ClientResponse.class, building);
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    @Test
    public void testUpdateUnits() {
        WebResource webResource = resource();

        UnitIO unit = new UnitIO();
        unit.number = "1";
        unit.propertyCode = "testCode";
        unit.baths = new IntegerIO(1);
        unit.beds = new IntegerIO(2);
        unit.floorplanName = new StringIO("2bdrm");

        ClientResponse response = webResource.path("buildings/MockCode/units/updateUnit").accept(MediaType.APPLICATION_XML).post(ClientResponse.class, unit);
        // requires preloaded building
        //        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }
}
