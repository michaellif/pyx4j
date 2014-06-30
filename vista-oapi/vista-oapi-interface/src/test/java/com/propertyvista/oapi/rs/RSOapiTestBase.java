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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;

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

public abstract class RSOapiTestBase extends IntegrationTestBase {

    private Building building;

    protected final JerseyTest RSTestHelper = new JerseyTest() {

        @Override
        protected Application configure() {
            Class<?> serviceClass = getServiceClass();
            if (serviceClass != null) {
                return new ResourceConfig(serviceClass);
            } else {
                Class<?> appClass = getServiceApplication();
                if (appClass != null) {
                    try {
                        return (Application) appClass.newInstance();
                    } catch (Exception ignore) {
                    }
                }
            }
            return null;
        }

        @Override
        protected TestContainerFactory getTestContainerFactory() {
            // Use this factory to run RS server in the same thread as the test and preserve the pmc namespace
            return new InMemoryTestContainerFactory();
        }
    };

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RSTestHelper.setUp();
        init();
    }

    @Override
    protected void tearDown() throws Exception {
        RSTestHelper.tearDown();
        super.tearDown();
    }

    public void init() throws Exception {
        preloadData();
        // load building
        getBuilding();
    }

    protected Class<?> getServiceClass() {
        return null;
    }

    protected Class<? extends Application> getServiceApplication() {
        return null;
    }

    protected WebTarget target() {
        return RSTestHelper.target();
    }

    protected WebTarget target(String path) {
        return RSTestHelper.target().path(path);
    }

    protected Client client() {
        return RSTestHelper.client();
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
