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

import org.junit.experimental.categories.Category;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;

@Category(RegressionTests.class)
public class YardiSubsequentLeasesTest extends YardiTestBase {

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
    }

    protected void setupFormerLease() {
        // Add first Lease
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FORMER_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000111").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "John@Smith.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1111.11")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2012-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitID, "111").
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1111.11")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
    }

    protected void setupCurrentLease() {
        // Add second Lease (same customer)
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000222").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000222").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "Vanya").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Kuznetsov").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "vanya@kuznetsov.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1222.22")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2013-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitID, "111").
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1222.22")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
    }

    protected void setupFutureLease() {
        // Add second Lease (same customer)
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000333").
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FUTURE_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000222").
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "Vanya").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Kuznetsov").
            set(RtCustomerUpdater.YCUSTOMERADDRESS.Email, "vanya@kuznetsov.ca").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1333.33")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2015-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2016-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitID, "111").
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1333.33")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
    }

    public void testFormer_CurrentLeasesImport() throws Exception {

        setupFormerLease();
        setupCurrentLease();

        setSysDate("2013-05-05");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        // Verify Leases are imported:

        Lease lease1 = getLeaseById("t000111");
        assertNotNull("Lease imported", lease1);
        assertEquals("Lease Status", Lease.Status.Completed, lease1.status().getValue());

        Lease lease2 = getLeaseById("t000222");
        assertNotNull("Lease imported", lease2);
        assertEquals("Lease Status", Lease.Status.Active, lease2.status().getValue());

        // Verify Unit Availability:

        AptUnit unit = getUnit(getBuilding("prop123"), "111");
        assertNotNull(unit);
        assertTrue("Unit Availability", unit._availableForRent().isNull());
    }

    public void testCurrent_FormerLeasesImport() throws Exception {

        setupCurrentLease();
        setupFormerLease();

        setSysDate("2013-05-05");

        // Initial Import 
        yardiImportAll(getYardiCredential("prop123"));

        // Verify Leases are imported:

        Lease lease1 = getLeaseById("t000111");
        assertNotNull("Lease imported", lease1);
        assertEquals("Lease Status", Lease.Status.Completed, lease1.status().getValue());

        Lease lease2 = getLeaseById("t000222");
        assertNotNull("Lease imported", lease2);
        assertEquals("Lease Status", Lease.Status.Active, lease2.status().getValue());

        // Verify Unit Availability:

        AptUnit unit = getUnit(getBuilding("prop123"), "111");
        assertNotNull(unit);
        assertTrue("Unit Availability", unit._availableForRent().isNull());
    }
}
