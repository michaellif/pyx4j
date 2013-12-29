/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

@Category(FunctionalTests.class)
public class YardiLeaseLifecycleTest extends YardiTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater("prop123").
            set(PropertyUpdater.ADDRESS.Address1, "11 prop123 str").
            set(PropertyUpdater.ADDRESS.PostalCode, "A1B 2C3").
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
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1000.00")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2014-12-31")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).         
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1000.00")).
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
            set(LeaseChargeUpdater.Name.Amount, "1000.00").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

    }

    public void testInitialImport_CurrentResident() throws Exception {
        setSysDate("2010-11-01");

        // Import all 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // Verify Lease is imported
        Lease lease;
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Active, lease.status().getValue());
    }

    public void testInitialImport_FutureResident() throws Exception {
        setSysDate("2010-11-01");

        // Make default tenant in yardi as FUTURE
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FUTURE_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // Initial Import 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // Verify Lease is imported
        Lease lease;
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Active, lease.status().getValue());
    }

    public void testInitialImport_FormerResident() throws Exception {
        setSysDate("2010-11-01");

        // Make default tenant in yardi as FORMER
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FORMER_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // Initial Import 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        Lease lease;
        // Verify Lease is imported
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Completed, lease.status().getValue());
    }

    public void testTransition_Current2Former() throws Exception {
        setSysDate("2010-11-01");

        // Initial Import 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        Lease lease;
        // Verify Lease is imported
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Active, lease.status().getValue());

        // Make tenant in Yardi as FORMER
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FORMER_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        setSysDate("2010-12-01");

        // Update all 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // Verify Lease is updated
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease updated", lease);
        assertEquals("Lease Status", Lease.Status.Completed, lease.status().getValue());
    }

    public void testTransition_Former2Current() throws Exception {
        setSysDate("2010-11-01");

        // Make default tenant in yardi as FORMER
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FORMER_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // Initial Import 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        Lease lease;
        // Verify Lease is imported
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Completed, lease.status().getValue());

        // Make tenant in Yardi as CURRENT (cancel Yardi Move Out)
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        setSysDate("2010-12-01");

        // Update all 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // Verify Lease is updated
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease updated", lease);
        assertEquals("Lease Status", Lease.Status.Active, lease.status().getValue());
    }

    public void testTransition_Future2Former() throws Exception {
        setSysDate("2010-11-01");

        // Make default tenant in yardi as FUTURE
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FUTURE_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        // Initial Import 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        Lease lease;
        // Verify Lease is imported
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease imported", lease);
        assertEquals("Lease Status", Lease.Status.Active, lease.status().getValue());

        // Make tenant in Yardi as FORMER
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop123", "t000111")//
                    .set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.FORMER_RESIDENT);
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        setSysDate("2010-12-01");

        // Update all 
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // Verify Lease is updated
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), "t000111"));
            lease = Persistence.service().retrieve(criteria);
        }
        assertNotNull("Lease updated", lease);
        assertEquals("Lease Status", Lease.Status.Completed, lease.status().getValue());
    }
}
