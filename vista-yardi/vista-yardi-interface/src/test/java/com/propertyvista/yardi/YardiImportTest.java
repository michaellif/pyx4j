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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.LeaseTermTenantTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.mock.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.CoTenantUpdater;
import com.propertyvista.yardi.mock.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;
import com.propertyvista.yardi.mock.TransactionChargeUpdateEvent;
import com.propertyvista.yardi.mock.TransactionChargeUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.stub.YardiResidentTransactionsStub;

public class YardiImportTest extends YardiTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater("prop123").
            set(PropertyUpdater.ADDRESS.Address1, "11 prop123 str").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        //Add RtCustomer, main tenant and Unit
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000111").
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
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "rent").
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
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "parkA").
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
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "parkB").
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
            CoTenantUpdater updater = new CoTenantUpdater("prop123", "t000111", "r000222").
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, "r000222").
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YCUSTOMERADDRESS.Email, "Jane@Doe.ca").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        {
            // @formatter:off
            TransactionChargeUpdater updater = new TransactionChargeUpdater("prop123", "t000111").
            set(TransactionChargeUpdater.Name.Description, "Rent").
            set(TransactionChargeUpdater.Name.TransactionDate, DateUtils.detectDateformat("01-May-2013")).
            set(TransactionChargeUpdater.Name.TransactionID, "700324302").
            set(TransactionChargeUpdater.Name.ChargeCode, "rrent").
            set(TransactionChargeUpdater.Name.GLAccountNumber, "40000301").
            set(TransactionChargeUpdater.Name.CustomerID, "t000111").
            set(TransactionChargeUpdater.Name.AmountPaid, "1.00").
            set(TransactionChargeUpdater.Name.BalanceDue, "1234.56").
            set(TransactionChargeUpdater.Name.Amount, "1234.56").
            set(TransactionChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new TransactionChargeUpdateEvent(updater));
        }
    }

    @Test
    public void testImport() throws Exception {

        setSysDate("25-May-2013");

        String propertyCode = "prop123";

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(propertyCode), new ExecutionMonitor());

        Building building = getBuilding(propertyCode);
        assertEquals(propertyCode, building.propertyCode().getValue());

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
        uid("rpark:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Parking B").
        agreedPrice("60.00");  
        // @formatter:on

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(1)).
        uid("rinpark:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Indoor Parking").
        agreedPrice("50.00");  
        // @formatter:on

        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1250.00"));
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "rent").
            set(LeaseChargeUpdater.Name.Amount, "1250.00");
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        { // item will be removed from next term
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "parkB").remove();
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "lockerA").
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
            CoTenantUpdater updater = new CoTenantUpdater("prop123", "t000111", "r000222").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Smith");
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(propertyCode), new ExecutionMonitor());

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
        uid("rlock:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Locker A").
        agreedPrice("150.00");  
        // @formatter:on

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().featureItems().get(1)).
        uid("rinpark:1").
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Indoor Parking").
        agreedPrice("50.00");  
        // @formatter:on
    }

    @Test
    public void testGetResidentTransactionsForTenant() throws Exception {

        String propertyCode = "prop123";
        String tenantId = "t000111";

        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        ResidentTransactions transactions = stub.getResidentTransactionsForTenant(getYardiCredential(propertyCode), propertyCode, tenantId);
        assertNotNull(transactions);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertEquals(tenantId, transactions.getProperty().get(0).getRTCustomer().get(0).getCustomerID());
    }

    @Test
    public void testGetLeaseChargesForTenant() throws Exception {

        String propertyCode = "prop123";
        String tenantId = "t000111";

        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        ResidentTransactions transactions = stub.getLeaseChargesForTenant(getYardiCredential(propertyCode), propertyCode, tenantId, null);
        assertEquals(1, transactions.getProperty().get(0).getRTCustomer().size());
        assertTrue(transactions.getProperty().get(0).getRTCustomer().get(0).getRTServiceTransactions().getTransactions().size() > 0);
    }
}
