/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 29, 2014
 * @author stanp
 */
package com.propertyvista.yardi;

import java.util.List;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.LeaseTermTenantTester;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
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

/** YardiMock validation - Functionally identical to YardiImportTest */
public class YardiMockTest extends YardiTestBase {

    final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;

    final String UnitID = YardiBuildingManager.DEFAULT_UNIT_NO;

    final String TenantID = "t200";

    final String CoTenantID = "r201";

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

        YardiMock.server().reset();
        // managers
        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiLeaseManager.class);
        YardiMock.server().addManager(YardiGuestManager.class);
        YardiMock.server().addManager(YardiConfigurationManager.class);
        // stubs
        YardiMock.addStub(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        YardiMock.addStub(YardiILSGuestCardStub.class, YardiMockILSGuestCardStubImpl.class);
    }

    private void leaseSetup() {
        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();

        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiILSGuestCardStub.class, BuildingID);
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiResidentTransactionsStub.class, BuildingID);

        YardiMock.server().getManager(YardiLeaseManager.class) //
                .addLease(TenantID, BuildingID, UnitID) //
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
                .setDescription("Parking B").setComment("Parking B").done() //

                .addTransaction("rent", "rrent", "987.65") //
                .setTransactionDate("01-May-2013") //
                .setGlAccountNumber("40000301") //
                .setAmountPaid("1.00").setBalanceDue("986.65") //
                .setComment("Rent (05/2013)").done();
    }

    public void testYardiImport() throws Exception {
        // 1. Test setup
        // -------------
        setSysDate("25-May-2013");

        leaseSetup();

        // 2. Test execution
        // -----------------
        yardiImportAll(getYardiCredential(BuildingID));

        // 3. Test assertion
        // -----------------
        Lease lease = getLeaseById(TenantID);
        assertNotNull("Lease not imported", lease);
        assertEquals("Invalid Lease Status", Lease.Status.Active, lease.status().getValue());

        Building building = getBuilding(BuildingID);
        assertNotNull(building);

        AptUnit unit = getUnit(building, UnitID);
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
                .getLease(TenantID, BuildingID) //
                .getCharge("rent").setAmount("1250.00").done() //
                .getCharge("parkA").setDescription("Indoor Parking").done() //
                .getCharge("parkB").setChargeCode("rlock").setAmount("150.00").setDescription("Locker B").done() //
                .getTenant(CoTenantID).setName("Jane Smith").done();

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(BuildingID), new ExecutionMonitor());

        // 5. Assert changes
        lease = getCurrentLease(unit);
        Persistence.service().retrieveMember(lease.currentTerm().versions());
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(0)) //
                .firstName("John") //
                .lastName("Smith") //
                .role(Role.Applicant);

        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(1)) //
                .firstName("Jane") //
                .lastName("Smith") //
                .role(Role.CoApplicant);

        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).agreedPrice("1250.00");

        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(0)) //
                .uid("rinpark:1") //
                .effectiveDate("01-Jun-2012") //
                .expirationDate("31-Jul-2014") //
                .description("Indoor Parking") //
                .agreedPrice("50.00");

        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(1)) //
                .uid("rlock:1") //
                .effectiveDate("01-Jun-2012") //
                .expirationDate("31-Jul-2014") //
                .description("Locker B") //
                .agreedPrice("150.00");

        // 6. Test single lease import
        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getResidentTransactionsForTenant(
                getYardiCredential(BuildingID), BuildingID, TenantID);
        assertNotNull(transactions);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals(TenantID, transactions.getProperty().get(0).getRTCustomer().get(0).getCustomerID());

    }

    public void testGetResidentTransactionsForTenant() throws Exception {
        // 1. Test setup
        // -------------
        leaseSetup();

        // 2. Test execution
        // -----------------
        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getResidentTransactionsForTenant(
                getYardiCredential(BuildingID), BuildingID, TenantID);

        // 3. Test assertion
        // -----------------
        assertNotNull(transactions);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals(TenantID, transactions.getProperty().get(0).getRTCustomer().get(0).getCustomerID());
        assertNotNull(transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions());
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());
    }

    public void testGetLeaseChargesForTenant() throws Exception {
        // 1. Test setup
        // -------------
        leaseSetup();

        // 2. Test execution
        // -----------------
        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getLeaseChargesForTenant(
                getYardiCredential(BuildingID), BuildingID, TenantID, null);

        // 3. Test assertion
        // -----------------
        assertNull("Has LeaseCharges", transactions);

        setSysDate("01-Jun-2012");

        transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getLeaseChargesForTenant(getYardiCredential(BuildingID), BuildingID,
                TenantID, null);
        assertEquals("Has LeaseCharges", 3, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());

        setSysDate("01-Aug-2014");

        transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getLeaseChargesForTenant(getYardiCredential(BuildingID), BuildingID,
                TenantID, null);
        assertNull("Has LeaseCharges", transactions);
    }

    public void testGetAllLeaseCharges() throws Exception {
        // 1. Test setup
        // -------------
        leaseSetup();

        // 2. Test execution
        // -----------------
        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getAllLeaseCharges(getYardiCredential(BuildingID),
                BuildingID, null);

        // 3. Test assertion
        // -----------------
        assertNull("Has LeaseCharges", transactions);

        setSysDate("01-Jun-2012");

        transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getAllLeaseCharges(getYardiCredential(BuildingID), BuildingID, null);
        assertEquals("Has LeaseCharges", 3, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());

        setSysDate("01-Aug-2014");

        transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getAllLeaseCharges(getYardiCredential(BuildingID), BuildingID, null);
        assertNull("Has LeaseCharges", transactions);
    }

    public void testRentableItemsPreload() throws Exception {
        // 1. Test setup: configure rentable items
        // ---------------------------------------
        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding() //
                .addRentableItem("OutdoorParking", "15.00", "routpark").setDescription("Outdoor Parking rent").done() //
                .addRentableItem("IndoorParking", "25.00", "rinpark").setDescription("Indoor Parking rent").done() //
                .addRentableItem("SmallLocker", "10.00", "rslocker").setDescription("Small Locker rent").done() //
                .addRentableItem("MediumLocker", "15.00", "rmlocker").setDescription("Medium Locker rent").done();

        // enable property access
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiILSGuestCardStub.class, BuildingID);
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiResidentTransactionsStub.class, BuildingID);

        // Initial Import
        yardiImportAll(getYardiCredential(BuildingID));

        Building building = getBuilding(BuildingID);
        assertNotNull("Building imported", building);

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        assertTrue("Catalog Services", !building.productCatalog().services().isEmpty());
        assertTrue("Catalog Features", !building.productCatalog().features().isEmpty());

        {
            EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().code().type(), ARCode.Type.Residential);
            criteria.eq(criteria.proto().code().yardiChargeCodes().$().yardiChargeCode(), "rrent");
            Service service = Persistence.service().retrieve(criteria);

            assertNotNull("Unit Rent Service", service);
            Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);
            assertTrue("Unit Rent Service items", !service.version().items().isEmpty());
        }

        {
            EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().version().name(), "IndoorParking");
            Feature feature = Persistence.service().retrieve(criteria);

            assertNotNull("Indoor Parking Feature", feature);
            Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
            assertTrue("Indoor Parking Feature items", !feature.version().items().isEmpty());
        }

        {
            EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().version().name(), "OutdoorParking");
            Feature feature = Persistence.service().retrieve(criteria);

            assertNotNull("Outdoor Parking Feature", feature);
            Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
            assertTrue("Outdoor Parking Feature items", !feature.version().items().isEmpty());
        }

        {
            EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().version().name(), "SmallLocker");
            Feature feature = Persistence.service().retrieve(criteria);

            assertNotNull("Samll Locker Feature", feature);
            Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
            assertTrue("Samll Locker Feature items", !feature.version().items().isEmpty());
        }

        {
            EntityQueryCriteria<Feature> criteria = EntityQueryCriteria.create(Feature.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().version().name(), "MediumLocker");
            Feature feature = Persistence.service().retrieve(criteria);

            assertNotNull("Medium Locker Feature", feature);
            Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
            assertTrue("Medium Locker Feature items", !feature.version().items().isEmpty());
        }
    }
}
