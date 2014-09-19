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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.LeaseTermTenantTester;
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
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockILSGuestCardStubImpl;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

/*
 * TODO - implement
 * - YardiMockServerFacade to set up the test data
 * - YardiMockILSGuestCardStubImpl
 */
public class YardiLeaseApplicationTest extends YardiTestBase {

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        if (false) {
            List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
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
            return models;
        } else {
            return super.getMockModelTypes();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        // managers
        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiLeaseManager.class);
        // stubs
        YardiMock.server().addStub(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        YardiMock.server().addStub(YardiILSGuestCardStub.class, YardiMockILSGuestCardStubImpl.class);
    }

    /*
     * TODO - implement
     * - Create yardi building with a unit
     * - Do import, check product catalog, add deposits for service and features
     * - Create and approve Lease Application
     * - Do import, check lease, ensure deposit charges
     */
    public void testLeaseApplication() throws Exception {
        // 1. Test setup
        // -------------
        final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;
        final String UnitID_1 = YardiBuildingManager.DEFAULT_UNIT_NO;
        final String LeaseID_1 = "lease1";

        YardiMock.server().getManager(YardiBuildingManager.class) //
                .addDefaultBuilding().setAddress("100 Avenue Rd");

        YardiMock.server().getManager(YardiLeaseManager.class) //
                .addLease(BuildingID, UnitID_1, LeaseID_1) //
                .setRentAmount("1234.56") //
                .setLeaseFrom("01-Jun-2012").setLeaseTo("31-Jul-2014") //

                .addTenant("tenant", "John Smith").setEmail("john@smith.ca").done() //
                .addTenant("co-tenant", "Jane Doe").setEmail("jane@doe.ca").done() //

                .addRentCharge("rent", "rrent").setGlAccountNumber("40000301").done() //

                .addCharge("parkA", "rinpark", "50.00") //
                .setFromDate("01-Jun-2012").setToDate("31-Jul-2014") //
                .setDescription("Parking A").setComment("Parking A").done() //

                .addCharge("parkB", "rpark", "60.00") //
                .setFromDate("01-Jun-2012").setToDate("31-Jul-2014") //
                .setDescription("Parking B").setComment("Parking A").done();

        // 2. Test execution
        // -----------------
        yardiImportAll(getYardiCredential(BuildingID));

        // 3. Test assertion
        // -----------------
        Lease lease = getLeaseById(LeaseID_1);
        assertNotNull("Lease not imported", lease);
        assertEquals("Invalid Lease Status", Lease.Status.Active, lease.status().getValue());

        Building building = getBuilding(BuildingID);
        assertNotNull(building);

        AptUnit unit = getUnit(building, UnitID_1);
        assertNotNull(unit);

        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(0)). //
                firstName("John"). //
                lastName("Smith"). //
                role(Role.Applicant). //
                email("john@smith.ca");

        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(1)). //
                firstName("Jane"). //
                lastName("Doe"). //
                role(Role.CoApplicant). //
                email("jane@doe.ca");

        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()). //
                effectiveDate("01-Jun-2012"). //
                expirationDate("31-Jul-2014"). //
                description("Regular Residential Unit"). //
                agreedPrice("1234.56");

        assertEquals(2, lease.currentTerm().version().leaseProducts().featureItems().size());

        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(0)). //
                uid("rinpark:1"). //
                effectiveDate("01-Jun-2012"). //
                expirationDate("31-Jul-2014"). //
                description("Indoor Parking"). //
                agreedPrice("50.00");

        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(1)). //
                uid("rpark:1"). //
                effectiveDate("01-Jun-2012"). //
                expirationDate("31-Jul-2014"). //
                description("Parking B"). //
                agreedPrice("60.00");
    }
}
