/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.junit.Before;
import org.junit.Test;

import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;

public class WSPropertyServiceTest extends WSOapiTestBase {

    @Before
    public void init() throws Exception {
        publish(WSPropertyService.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testCreateBuilding() throws Exception {

        WSPropertyServiceStub stub = new WSPropertyServiceStub(new URL(getAddress()));

        WSPropertyService service = stub.getPropertyServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");

        service.createBuilding(new BuildingIO("b1"));

        BuildingIO building = service.getBuildingByPropertyCode("b1");

        assertEquals("b1", building.propertyCode);

    }

    @Test
    public void testGetAllBuildings() throws Exception {

        WSPropertyServiceStub stub = new WSPropertyServiceStub(new URL(getAddress()));

        WSPropertyService service = stub.getPropertyServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");

        service.createBuilding(new BuildingIO("b1"));
        service.createBuilding(new BuildingIO("b2"));

        BuildingsIO buildings = service.listAllBuildings();

//        assertEquals(0, buildings.buildings.size());

    }

}