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
package com.propertyvista.yardi;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockManager;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PADPolicyDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.ProductTaxPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;

public class YardiTestBase {

    @Before
    public void init() throws Exception {
        VistaTestDBSetup.init();

        ServerSideFactory.register(YardiClient.class, YardiTestClientImpl.class);

        Persistence.service().startBackgroundProcessTransaction();

        Lifecycle.startElevatedUserContext();

        NamespaceManager.setNamespace("t" + System.currentTimeMillis());

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.testNamespace(NamespaceManager.getNamespace());
        TestLifecycle.beginRequest();

    }

    @After
    public void end() {
        Persistence.service().commit();
    }

    protected void preloadData() {
        preloadData(new MockConfig());
    }

    protected void preloadData(final MockConfig config) {

        config.yardiIntegration = true;

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
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(ProductTaxPolicyDataModel.class);
        models.add(DepositPolicyDataModel.class);
        models.add(LeaseAdjustmentPolicyDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(PADPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        return models;
    }

}
