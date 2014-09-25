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

import java.util.Iterator;
import java.util.List;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.LeaseTermTenantTester;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel.Code;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockILSGuestCardStubImpl;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stubs.YardiStubFactory;

/*
 * TODO - implement
 * - YardiMockServerFacade to set up the test data
 * - YardiMockILSGuestCardStubImpl
 */
public class YardiLeaseApplicationTest extends YardiTestBase {

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(CustomerDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        // managers
        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiLeaseManager.class);
        YardiMock.server().addManager(YardiGuestManager.class);
        YardiMock.server().addManager(YardiConfigurationManager.class);
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
        // 1. Setup
        // --------
        final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;
        final String UnitID = YardiBuildingManager.DEFAULT_UNIT_NO;
        final String lmrChargeCode = "rlmr";

        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();

        // 2. Execution: first import
        // -------------------------------
        yardiImportAll(getYardiCredential(BuildingID));

        // 3. Assertion: building, unit, product catalog
        // ---------------------------------------------
        Building building = getBuilding(BuildingID);
        assertNotNull(building);

        AptUnit unit = getUnit(building, UnitID);
        assertNotNull(unit);

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        ProductCatalog catalog = building.productCatalog();
        assertEquals(1, catalog.services().size());

        // 4. Execution: enable LMR
        // ------------------------
        Persistence.ensureRetrieve(catalog.services(), AttachLevel.Attached);
        catalog.services().get(0).version().depositLMR().enabled().setValue(true);
        Persistence.service().persist(catalog.services().get(0));
        // configure yardi deposit code
        ARCode deposit = getDataModel(ARCodeDataModel.class).getARCode(Code.depositLMR);
        getDataModel(ARCodeDataModel.class).addYardiCode(deposit, lmrChargeCode);
        // in yardi
        YardiMock.server().getManager(YardiConfigurationManager.class).addChargeCode(YardiILSGuestCardStub.class, lmrChargeCode);

        // 5. Execution: create and approve lease application
        // --------------------------------------------------
        // tenant and co-tenant
        getDataModel(CustomerDataModel.class).addCustomer();
        getDataModel(CustomerDataModel.class).addCustomer();
        // lease application
        List<Customer> customers = getDataModel(CustomerDataModel.class).getAllItems();
        Persistence.ensureRetrieve(catalog.services().get(0).version().items(), AttachLevel.Attached);
        ProductItem serviceItem = catalog.services().get(0).version().items().get(0);
        Lease lease = getDataModel(LeaseDataModel.class).addLease(building, "01-Jun-2012", "31-Jul-2014", null, null, customers, serviceItem);
        assertNotNull(lease);
        assertEquals(Lease.Status.Application, lease.status().getValue());

        // approve lease application
        ServerSideFactory.create(LeaseFacade.class).approve(lease, null, null);

        // 6. Assertion: status approved, participantId have been set
        lease = ServerSideFactory.create(LeaseFacade.class).load(lease, false);

        assertEquals(Lease.Status.Approved, lease.status().getValue());

        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            assertTrue(tenant.leaseParticipant().participantId().getValue().matches("^[rt].*"));
        }

        // compare customers
        assertEquals(customers.size(), lease.currentTerm().version().tenants().size());
        int idx = 0;
        for (Iterator<LeaseTermTenant> itTenant = lease.currentTerm().version().tenants().iterator(); itTenant.hasNext();) {
            assertEquals(customers.get(idx++).getPrimaryKey(), itTenant.next().leaseParticipant().customer().getPrimaryKey());
        }
    }

    public void testYardiImport() throws Exception {
        // 1. Test setup
        // -------------
        final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;
        final String UnitID_1 = YardiBuildingManager.DEFAULT_UNIT_NO;
        final String LeaseID_1 = "t_lease1";
        final String TenantID = "r_tenant";
        final String CoTenantID = "r_cotenant";

        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();

        YardiMock.server().getManager(YardiLeaseManager.class) //
                .addLease(LeaseID_1, BuildingID, UnitID_1) //
                .setRentAmount("1234.56") //
                .setLeaseFrom("01-Jun-2012").setLeaseTo("31-Jul-2014") //

                .addTenant(TenantID, "John Smith").setEmail("john@smith.ca").done() //
                .addTenant(CoTenantID, "Jane Doe").setEmail("jane@doe.ca").done() //

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

        // 4. Update lease
        YardiMock.server().getManager(YardiLeaseManager.class) //
                .getLease(LeaseID_1, BuildingID) //
                .getCharge("rent").setAmount("1250.00").done() //
                .getCharge("parkA").setDescription("Indoor Parking").done() //
                .getCharge("parkB").setChargeCode("rlock").setAmount("150.00").setDescription("Locker B").done() //
                .getTenant(CoTenantID).setName("Jane Smith").done();

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(BuildingID), new ExecutionMonitor());

        // 5. Assert changes
        lease = getCurrentLease(unit);
        Persistence.service().retrieveMember(lease.currentTerm().versions());
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        // @formatter:off
        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(0)).
        firstName("John").
        lastName("Smith").
        role(Role.Applicant);
        // @formatter:on

        // @formatter:off
        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(1)).
        firstName("Jane").
        lastName("Smith").
        role(Role.CoApplicant);
        // @formatter:on

        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).agreedPrice("1250.00");

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(0)).
        uid("rinpark:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Indoor Parking").
        agreedPrice("50.00");
        // @formatter:on

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(1)).
        uid("rlock:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Locker B").
        agreedPrice("150.00");
        // @formatter:on

        // 6. Test single lease import
        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getResidentTransactionsForTenant(
                getYardiCredential(BuildingID), BuildingID, LeaseID_1);
        assertNotNull(transactions);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals(LeaseID_1, transactions.getProperty().get(0).getRTCustomer().get(0).getCustomerID());

    }
}
