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
package com.propertyvista.oapi.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
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
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;

public class RSOapiTestBase extends JerseyTest {

    private static final Logger log = LoggerFactory.getLogger(RSOapiTestBase.class);

    protected MockManager mockManager;

    @Override
    protected Application configure() {
        Class<?> serviceClass = getServiceClass();
        return serviceClass == null ? new OpenApiRsApplication() : new ResourceConfig(serviceClass);
    }

    protected Class<?> getServiceClass() {
        return null;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Before
    public void initDB() throws Exception {
        VistaTestDBSetup.init();
        preloadData();
    }

//    @Override
    protected int getPort(int defaultPort) {
        // See README-ports.txt
        return 7771;
    }

    protected void preloadData() {
        preloadData(new MockConfig());
    }

    protected void preloadData(final MockConfig config) {

        mockManager = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<MockManager, RuntimeException>() {

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
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        return models;
    }

}
