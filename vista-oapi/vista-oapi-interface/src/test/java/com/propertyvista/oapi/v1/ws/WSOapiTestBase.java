/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 */
package com.propertyvista.oapi.v1.ws;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;

public abstract class WSOapiTestBase extends IntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(WSOapiTestBase.class);

    private int port;

    private Class<?> serviceClass;

    Endpoint endpoint = null;

    private Building building;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (endpoint != null) {
            endpoint.stop();
        }
        endpoint = null;
    }

    public int getPort() {
        return port;
    }

    private String getAddress(int port) {
        return "http://localhost:" + port + "/WS/" + serviceClass.getSimpleName();
    }

    protected String getAddress() {
        return getAddress(port);
    }

    protected Endpoint publish(Class<?> serviceClass) throws Exception {
        return publish(serviceClass, null);
    }

    protected Endpoint publish(Class<?> serviceClass, Handler<?> handler) throws Exception {
        assert endpoint == null;
        this.serviceClass = serviceClass;
        port = 7771;
        if (System.getProperty("bamboo.agentOffsetNo") == null) {
        	port += Integer.valueOf(System.getProperty("bamboo.agentOffsetNo"));
        }
        
        int monitorPort = port;
        //For TCP/IP monitor
        if (false) {
            port = 9999;
            monitorPort = 9090;
        }
        endpoint = Endpoint.create(serviceClass.newInstance());
        if (handler != null) {
            Binding binding = endpoint.getBinding();
            List<Handler> handlerList = binding.getHandlerChain();
            handlerList.add(handler);
            binding.setHandlerChain(handlerList);
        }
        String address = getAddress(monitorPort);
        log.debug("Address: {}", address);
        endpoint.publish(address);

        return endpoint;
    }

    protected int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        return models;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = Persistence.service().retrieve(EntityQueryCriteria.create(Building.class));
        }
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
        }
        return building;
    }
}
