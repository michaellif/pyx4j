/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2013
 * @author vlads
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
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.CoTenantUpdater;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;
import com.propertyvista.yardi.mock.UnitTransferSimulator;
import com.propertyvista.yardi.mock.UnitTransferSimulatorEvent;

public class YardiUnitTransferTest extends YardiTestBase {

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

        // Add first Lease
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000111").
            set(RtCustomerUpdater.YCUSTOMER.Description, "1").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "John@Smith.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).     
            set(RtCustomerUpdater.UNITINFO.UnitID, "10").
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1001.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

    }

    private Lease getLeaseById(String leaseId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), leaseId));
        Lease lease = Persistence.service().retrieve(criteria);
        return lease;
    }

    public void testUnitTransfer() throws Exception {
        setSysDate("2010-11-01");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1v1 = getLeaseById("t000111");
        // Verify Leases is imported:
        assertNotNull("Lease imported", lease1v1);
        assertEquals("Proper Unit", "10", lease1v1.unit().info().number().getValue());

        {
            UnitTransferSimulator updater = new UnitTransferSimulator("prop123", "t000111", "t000222", "2");
            MockEventBus.fireEvent(new UnitTransferSimulatorEvent(updater));
        }
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.UNITINFO.UnitID, "20");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        yardiImportAll(getYardiCredential("prop123"));

        Lease leaseMoved = getLeaseById("t000111");
        assertEquals("Proper Unit", "20", leaseMoved.unit().info().number().getValue());

        Lease leaseTemporary = getLeaseById("t000222");
        assertEquals("Proper Unit", "10", leaseTemporary.unit().info().number().getValue());
    }

    public void testUnitTransferWithCoTenant() throws Exception {
        setSysDate("2010-11-01");

        {
            // @formatter:off
            CoTenantUpdater updater = new CoTenantUpdater("prop123", "t000111", "r000222").
            set(CoTenantUpdater.YCUSTOMER.Type, Customerinfo.CUSTOMER).
            set(CoTenantUpdater.YCUSTOMER.CustomerID, "r000222").
            set(CoTenantUpdater.YCUSTOMERNAME.FirstName, "Jane").
            set(CoTenantUpdater.YCUSTOMERNAME.LastName, "Doe").
            set(CoTenantUpdater.YLEASE.ResponsibleForLease, true);
            // @formatter:on
            MockEventBus.fireEvent(new CoTenantUpdateEvent(updater));
        }

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        Lease lease1v1 = getLeaseById("t000111");
        // Verify Leases is imported:
        assertNotNull("Lease imported", lease1v1);

        {
            UnitTransferSimulator updater = new UnitTransferSimulator("prop123", "t000111", "t000222", "2");
            MockEventBus.fireEvent(new UnitTransferSimulatorEvent(updater));
        }

        yardiImportAll(getYardiCredential("prop123"));

        Lease lease = getLeaseById("t000111");
        Lease leaseTemporary = getLeaseById("t000222");
    }

}
