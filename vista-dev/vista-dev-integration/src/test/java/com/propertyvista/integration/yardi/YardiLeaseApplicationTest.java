/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AgreementLegalPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.RestrictionsPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

/*
 * TODO - implement
 * - YardiMockServerFacade to set up the test data
 * - YardiMockILSGuestCardStubImpl
 */
//@Ignore
public class YardiLeaseApplicationTest extends YardiTestBase {

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        if (false) {
            models.add(PmcDataModel.class);
            models.add(CustomerDataModel.class);
            models.add(LocationsDataModel.class);
            models.add(TaxesDataModel.class);
            models.add(GLCodeDataModel.class);
            models.add(ARCodeDataModel.class);
            models.add(BuildingDataModel.class);
            models.add(IdAssignmentPolicyDataModel.class);
            models.add(DepositPolicyDataModel.class);
            models.add(ARPolicyDataModel.class);
            models.add(LeaseBillingPolicyDataModel.class);
            models.add(LeaseDataModel.class);
            models.add(AgreementLegalPolicyDataModel.class);
            models.add(RestrictionsPolicyDataModel.class);
        }
        return models;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiLeaseManager.class);
        YardiMock.server().addStub(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
    }

    /*
     * TODO - implement
     * - Create yardi building with a unit
     * - Do import, check product catalog, add deposits for service and features
     * - Create and approve Lease Application
     * - Do import, check lease, ensure deposit charges
     */
    public void testLeaseApplication() {
        YardiMock.server().getManager(YardiBuildingManager.class)//
                .addDefaultBuilding().setAddress("100 Avenue Rd");

        assertTrue("prop123".equals(YardiMock.server().getModel().getBuildings().get(0).buildingId().getValue()));
        assertTrue("Toronto".equals(YardiMock.server().getModel().getBuildings().get(0).address().city().getValue()));

        YardiMock.server().getManager(YardiLeaseManager.class) //
                .addLease(YardiBuildingManager.DEFAULT_PROPERTY_CODE, YardiBuildingManager.DEFAULT_UNIT_NO, "lease1") //
                .addTenant("tenant", "John Smith").done() //
                .addCharge("parking", "50.00");
        assertTrue("lease1".equals(YardiMock.server().getModel().getBuildings().get(0).leases().get(0).leaseId().getValue()));
        assertTrue("parking".equals(YardiMock.server().getModel().getBuildings().get(0).leases().get(0).charges().get(0).chargeId().getValue()));
    }
}
