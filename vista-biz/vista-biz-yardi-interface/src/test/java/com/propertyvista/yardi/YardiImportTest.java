/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.math.BigDecimal;

import org.junit.Test;

import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.LeaseTermTenantTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.mock.updater.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.updater.CoTenantUpdater;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RentableItemTypeUpdateEvent;
import com.propertyvista.yardi.mock.updater.RentableItemTypeUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;
import com.propertyvista.yardi.mock.updater.TransactionChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.TransactionChargeUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiImportTest extends YardiTestBase {

    private static String PROPERTYID = "prop123";

    private static String TENANTID = "t000111";

    private static String COTENANTID = "r000222";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater(PROPERTYID).
            set(PropertyUpdater.ADDRESS.Address1, "11 prop123 str").
            set(PropertyUpdater.ADDRESS.PostalCode, "A1B 2C3").
            set(PropertyUpdater.ADDRESS.State, "ON").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        //Add RtCustomer, main tenant and Unit
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater(PROPERTYID, TENANTID).
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, TENANTID).
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1234.56")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "John@Smith.ca").
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1300.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.GLAccountNumber, "40000301").
            set(LeaseChargeUpdater.Name.Amount, "1234.56").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "parkA").
            set(LeaseChargeUpdater.Name.Description, "Parking A").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rinpark").
            set(LeaseChargeUpdater.Name.Amount, "50.00").
            set(LeaseChargeUpdater.Name.Comment, "Parking A");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "parkB").
            set(LeaseChargeUpdater.Name.Description, "Parking B").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rpark").
            set(LeaseChargeUpdater.Name.Amount, "60.00").
            set(LeaseChargeUpdater.Name.Comment, "Parking B");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {

            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater(PROPERTYID, TENANTID, COTENANTID).
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, COTENANTID).
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YCUSTOMERADDRESS.Email, "Jane@Doe.ca").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        {
            // @formatter:off
            TransactionChargeUpdater updater = new TransactionChargeUpdater(PROPERTYID, TENANTID).
            set(TransactionChargeUpdater.Name.Description, "Rent").
            set(TransactionChargeUpdater.Name.TransactionDate, DateUtils.detectDateformat("01-May-2013")).
            set(TransactionChargeUpdater.Name.TransactionID, "700324302").
            set(TransactionChargeUpdater.Name.ChargeCode, "rrent").
            set(TransactionChargeUpdater.Name.GLAccountNumber, "40000301").
            set(TransactionChargeUpdater.Name.CustomerID, TENANTID).
            set(TransactionChargeUpdater.Name.AmountPaid, "1.00").
            set(TransactionChargeUpdater.Name.BalanceDue, "1234.56").
            set(TransactionChargeUpdater.Name.Amount, "1234.56").
            set(TransactionChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new TransactionChargeUpdateEvent(updater));
        }

// this an example of rentable items test preload:
//             setupRentableItem();
    }

    protected void setupRentableItem() {
        {
            // @formatter:off
            RentableItemTypeUpdater updater = new RentableItemTypeUpdater(PROPERTYID, "UnitRent").
            set(RentableItemTypeUpdater.Name.Description, "Unit rent").
            set(RentableItemTypeUpdater.Name.ChargeCode, "rrent").
            set(RentableItemTypeUpdater.Name.Rent, "1000.00");
            // @formatter:on
            MockEventBus.fireEvent(new RentableItemTypeUpdateEvent(updater));
        }

        {
            // @formatter:off
            RentableItemTypeUpdater updater = new RentableItemTypeUpdater(PROPERTYID, "OutdoorParking").
            set(RentableItemTypeUpdater.Name.Description, "Outdoor Parking rent").
            set(RentableItemTypeUpdater.Name.ChargeCode, "routpark").
            set(RentableItemTypeUpdater.Name.Rent, "15.00");
            // @formatter:on
            MockEventBus.fireEvent(new RentableItemTypeUpdateEvent(updater));
        }

        {
            // @formatter:off
            RentableItemTypeUpdater updater = new RentableItemTypeUpdater(PROPERTYID, "IndoorParking").
            set(RentableItemTypeUpdater.Name.Description, "Indoor Parking rent").
            set(RentableItemTypeUpdater.Name.ChargeCode, "rinpark").
            set(RentableItemTypeUpdater.Name.Rent, "25.00");
            // @formatter:on
            MockEventBus.fireEvent(new RentableItemTypeUpdateEvent(updater));
        }

        {
            // @formatter:off
            RentableItemTypeUpdater updater = new RentableItemTypeUpdater(PROPERTYID, "SmallLocker").
            set(RentableItemTypeUpdater.Name.Description, "Small Locker rent").
            set(RentableItemTypeUpdater.Name.ChargeCode, "rslocker").
            set(RentableItemTypeUpdater.Name.Rent, "10.00");
            // @formatter:on
            MockEventBus.fireEvent(new RentableItemTypeUpdateEvent(updater));
        }

        {
            // @formatter:off
            RentableItemTypeUpdater updater = new RentableItemTypeUpdater(PROPERTYID, "MediumLocker").
            set(RentableItemTypeUpdater.Name.Description, "Medium Locker rent").
            set(RentableItemTypeUpdater.Name.ChargeCode, "rmlocker").
            set(RentableItemTypeUpdater.Name.Rent, "15.00");
            // @formatter:on
            MockEventBus.fireEvent(new RentableItemTypeUpdateEvent(updater));
        }
    }

    @Test
    public void testImport() throws Exception {

        setSysDate("25-May-2013");

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(PROPERTYID), new ExecutionMonitor());

        Building building = getBuilding(PROPERTYID);
        assertEquals(PROPERTYID, building.propertyCode().getValue());

        AptUnit unit = getUnit(building, "0111");
        assertNotNull(unit);

        Lease lease = getCurrentLease(unit);
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        // @formatter:off
        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(0)).
        firstName("John").
        lastName("Smith").
        role(Role.Applicant).
        email("john@smith.ca");
        // @formatter:on

        // @formatter:off
        new LeaseTermTenantTester(lease.currentTerm().version().tenants().get(1)).
        firstName("Jane").
        lastName("Doe").
        role(Role.CoApplicant).
        email("jane@doe.ca");
        // @formatter:on

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Regular Residential Unit").
        agreedPrice("1234.56");
        // @formatter:on

        assertEquals(2, lease.currentTerm().version().leaseProducts().featureItems().size());

        //TODO When uid will be unique use uid instead of index in list

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
        uid("rpark:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Parking B").
        agreedPrice("60.00");  
        // @formatter:on

        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater(PROPERTYID, TENANTID).
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1250.00"));
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "rent").
            set(LeaseChargeUpdater.Name.Amount, "1250.00");
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        { // item will be removed from next term
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "parkB").remove();
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTYID, TENANTID, "lockerA").
            set(LeaseChargeUpdater.Name.Description, "Locker A").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rlock").
            set(LeaseChargeUpdater.Name.Amount, "150.00").
            set(LeaseChargeUpdater.Name.Comment, "Locker A");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {
            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater(PROPERTYID, TENANTID, COTENANTID).
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Smith");
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(PROPERTYID), new ExecutionMonitor());

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

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).
        agreedPrice("1250.00");

        assertEquals(2, lease.currentTerm().version().leaseProducts().featureItems().size());

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
        description("Locker A").
        agreedPrice("150.00");  
        // @formatter:on
    }

    @Test
    public void testGetResidentTransactionsForTenant() throws Exception {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        ResidentTransactions transactions = stub.getResidentTransactionsForTenant(getYardiCredential(PROPERTYID), PROPERTYID, TENANTID);
        assertNotNull(transactions);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals(TENANTID, transactions.getProperty().get(0).getRTCustomer().get(0).getCustomerID());
    }

    @Test
    public void testGetLeaseChargesForTenant() throws Exception {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        ResidentTransactions transactions = stub.getLeaseChargesForTenant(getYardiCredential(PROPERTYID), PROPERTYID, TENANTID, null);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals("Has LeaseCharges", 0, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());

        setSysDate("01-Jun-2012");

        transactions = stub.getLeaseChargesForTenant(getYardiCredential(PROPERTYID), PROPERTYID, TENANTID, null);
        assertEquals("Has LeaseCharges", 3, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());

        setSysDate("01-Aug-2014");

        transactions = stub.getLeaseChargesForTenant(getYardiCredential(PROPERTYID), PROPERTYID, TENANTID, null);
        assertEquals("Has LeaseCharges", 0, transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size());
    }

    public void testRentableItemsPreload() throws Exception {
        setupRentableItem();

        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential(PROPERTYID));

        Building building = getBuilding(PROPERTYID);
        assertNotNull("Building imported", building);

        Persistence.ensureRetrieve(building.productCatalog(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().services(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.productCatalog().features(), AttachLevel.Attached);

        assertTrue("Catalog Services", !building.productCatalog().services().isEmpty());
        assertTrue("Catalog Features", !building.productCatalog().features().isEmpty());

        {
            EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
            criteria.eq(criteria.proto().catalog(), building.productCatalog());
            criteria.eq(criteria.proto().version().name(), "UnitRent");
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
