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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockManager;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PADPolicyDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.yardi.mock.stub.YardiMockMaintenanceRequestsStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.mock.stub.YardiMockSystemBatchesStubImpl;
import com.propertyvista.yardi.stub.YardiMaintenanceRequestsStub;
import com.propertyvista.yardi.stub.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stub.YardiSystemBatchesStub;

public class YardiTestBase extends IntegrationTestBase {

    @Override
    protected void setUp() throws Exception {
        ServerSideFactory.register(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        ServerSideFactory.register(YardiSystemBatchesStub.class, YardiMockSystemBatchesStubImpl.class);
        ServerSideFactory.register(YardiMaintenanceRequestsStub.class, YardiMockMaintenanceRequestsStubImpl.class);

        super.setUp();

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
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(PADPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        return models;
    }

}
