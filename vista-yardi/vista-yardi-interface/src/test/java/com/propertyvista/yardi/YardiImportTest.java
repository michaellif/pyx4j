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

import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.mock.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.RtCustomerUpdater;
import com.propertyvista.yardi.mock.TransactionChargeUpdateEvent;
import com.propertyvista.yardi.mock.TransactionChargeUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class YardiImportTest extends YardiTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater("prop123");        
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
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1300.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        //Add second Customer
        {
            //TODO Mykola
            //CustomerUpdater updater = new CustomerUpdater("prop123", "rtcust id", "custid"); 
            //MockEventBus.fireEvent(new CustomerUpdateEvent(updater));
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

        String propertyCode = "prop123";

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(propertyCode), new ExecutionMonitor());

        Building building = getBuilding(propertyCode);
        assertEquals(propertyCode, building.propertyCode().getValue());

        AptUnit unit = getUnit(building, "0111");
        assertNotNull(unit);

        Lease lease = getCurrentLease(unit);
        assertNotNull(lease);

        // @formatter:off
        new BillableItemTester(lease.currentTerm().version().leaseProducts().serviceItem()).
        agreedPrice("1234.56");
        //.
        //effectiveDate("1-May-2011").
        //expirationDate("31-May-2011");
        // @formatter:on
    }
}
