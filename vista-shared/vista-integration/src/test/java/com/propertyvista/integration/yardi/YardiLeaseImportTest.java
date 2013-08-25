/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.test.integration.LeaseTermTenantTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.CoTenantUpdater;
import com.propertyvista.yardi.mock.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;

public class YardiLeaseImportTest extends YardiTestBase {

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

        setupCustomerFirstLease();
    }

    protected void setupCustomerFirstLease() {
        // Add first Lease
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000111").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "John@Smith.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2014-12-31")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.GLAccountNumber, "40000301").
            set(LeaseChargeUpdater.Name.Amount, "1001.00").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
    }

    protected void setupCustomerSecondLease() {
        // Add second Lease (same customer)
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000222").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000222").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "john@smith.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1002.00")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1002.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000222", "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2014-12-31")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.GLAccountNumber, "40000301").
            set(LeaseChargeUpdater.Name.Amount, "1002.00").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
    }

    private Lease getLeaseById(String leaseId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), leaseId));
        Lease lease = Persistence.service().retrieve(criteria);
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        return lease;
    }

    public void testOneCustomerMultipleLeases_InitialImport() throws Exception {

        setupCustomerSecondLease();

        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1, lease2;

        // Verify Leases is imported:
        {
            lease1 = getLeaseById("t000111");
        }

        assertNotNull("Lease imported", lease1);
        assertEquals("Lease Status", Lease.Status.Active, lease1.status().getValue());
        {
            lease2 = getLeaseById("t000222");
        }
        assertNotNull("Lease imported", lease2);
        assertEquals("Lease Status", Lease.Status.Active, lease2.status().getValue());

        // verify Customer is the same:

        assertEquals("Customer", lease1.currentTerm().version().tenants().get(0).leaseParticipant().customer().getValue(), lease2.currentTerm().version()
                .tenants().get(0).leaseParticipant().customer().getValue());
    }

    public void testOneCustomerMultipleLeases_AddLeaseInSecondUpdate() throws Exception {
        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1, lease2;

        // Verify Leases is imported:
        {
            lease1 = getLeaseById("t000111");
        }

        assertNotNull("Lease imported", lease1);
        assertEquals("Lease Status", Lease.Status.Active, lease1.status().getValue());

        setSysDate("2010-11-02");

        setupCustomerSecondLease();

        //Run second update from yardi
        yardiImportAll(getYardiCredential("prop123"));

        {
            lease2 = getLeaseById("t000222");
        }
        assertNotNull("Lease imported", lease2);
        assertEquals("Lease Status", Lease.Status.Active, lease2.status().getValue());

        // verify Customer is the same:

        assertEquals("Customer", lease1.currentTerm().version().tenants().get(0).leaseParticipant().customer().getValue(), lease2.currentTerm().version()
                .tenants().get(0).leaseParticipant().customer().getValue());
    }

    public void testDuplicateEmailOnSingleLease_InitialImport() throws Exception {
        {
            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater("prop123", "t000111", "r000222").
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, "r000222").
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YCUSTOMERADDRESS.Email, "john@smith.ca").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1;

        // Verify Leases is imported:
        {
            lease1 = getLeaseById("t000111");
        }

        assertNotNull("Lease imported", lease1);

        // verify Customer is the same:

        new LeaseTermTenantTester(lease1.currentTerm().version().tenants().get(0)). //
                firstName("John").//
                lastName("Smith").//
                role(Role.Applicant) //
                .email("john@smith.ca");

        new LeaseTermTenantTester(lease1.currentTerm().version().tenants().get(1)).//
                firstName("Jane"). //
                lastName("Doe"). //
                role(Role.CoApplicant). //
                email(null);
    }

    // See the bug  VISTA-3365  
    public void testDuplicateEmailOnSingleLease_Update() throws Exception {
        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        {
            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater("prop123", "t000111", "r000222").
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, "r000222").
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YCUSTOMERADDRESS.Email, "john@smith.ca").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        setSysDate("2010-11-02");

        setupCustomerSecondLease();

        //Run second update from yardi
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1;

        // Verify Leases is imported:
        {
            lease1 = getLeaseById("t000111");
        }

        assertNotNull("Lease imported", lease1);

        // verify Customer is the same:

        new LeaseTermTenantTester(lease1.currentTerm().version().tenants().get(0)). //
                firstName("John").//
                lastName("Smith").//
                role(Role.Applicant) //
                .email("john@smith.ca");

        new LeaseTermTenantTester(lease1.currentTerm().version().tenants().get(1)).//
                firstName("Jane"). //
                lastName("Doe"). //
                role(Role.CoApplicant). //
                email(null);
    }
}
