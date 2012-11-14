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
package com.propertyvista.oapi.rest;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.Before;
import org.junit.Test;

public class PropertyServiceTest extends OapiRsTest {

    @Before
    public void init() throws Exception {
        publish(PropertyService.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testCreateBuilding() throws Exception {
//
//        Document resultDOM = Utils.downloadDOM(addNumbersEndpointUrl + URIUtil.encodeQuery("?num1=" + num1 + "&num2=" + num2, "UTF-8"));
//
//        String resultStr = resultDOM.getDocumentElement().getFirstChild().getNodeValue();
//        
//        PropertyServiceStub stub = new PropertyServiceStub(new URL(getAddress()));
//
//        PropertyService service = stub.getPropertyServicePort();
//
//        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
//        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");
//
//        service.createBuilding(new BuildingRS("b1"));
//
//        BuildingRS building = service.getBuildingByPropertyCode("b1");
//
//        assertEquals("b1", building.propertyCode);
//
    }
//
//    @Test
//    public void testGetAllBuildings() throws Exception {
//
//        PropertyServiceStub stub = new PropertyServiceStub(new URL(getAddress()));
//
//        PropertyService service = stub.getPropertyServicePort();
//
//        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
//        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");
//
//        service.createBuilding(new BuildingRS("b1"));
//        service.createBuilding(new BuildingRS("b2"));
//
//        BuildingsRS buildings = service.listAllBuildings();
//
//        assertEquals(2, buildings.buildings.size());
//
//    }

}