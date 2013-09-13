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
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockManager;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;

public class WSOapiTestBase {

    private static final Logger log = LoggerFactory.getLogger(WSOapiTestBase.class);

    private int port;

    private Class<?> serviceClass;

    Endpoint endpoint = null;

    @Before
    public void initDB() throws Exception {
        VistaTestDBSetup.init();
    }

    @After
    public void stop() {
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

    String getAddress() {
        return getAddress(port);
    }

    void publish(Class<?> serviceClass) throws Exception {
        assert endpoint == null;
        this.serviceClass = serviceClass;
        port = 7771;
        int monitorPort = port;
        //For TCP/IP monitor
        if (false) {
            port = 9999;
            monitorPort = 9090;
        }
        String address = getAddress(monitorPort);
        log.debug("Address: {}", address);
        endpoint = Endpoint.publish(address, Class.forName(serviceClass.getName() + "Impl").newInstance());
    }

    int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    protected void preloadData() {
        preloadData(new MockConfig());
    }

    protected void preloadData(final MockConfig config) {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<MockManager, RuntimeException>() {

            @Override
            public MockManager execute() {

                MockManager mockManager = new MockManager(config);
                for (Class<? extends MockDataModel<?>> modelType : getMockModelTypes()) {
                    mockManager.addModel(modelType);
                }

                return mockManager;
            }
        });
    }

    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(LocationsDataModel.class);
        return models;
    }
}
