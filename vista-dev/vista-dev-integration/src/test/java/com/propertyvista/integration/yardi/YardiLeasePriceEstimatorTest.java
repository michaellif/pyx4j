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
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.InvoiceProductChargeTester;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.ProductTaxPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;

@Category(FunctionalTests.class)
public class YardiLeasePriceEstimatorTest extends YardiTestBase {

    public static final String PROPERTY_CODE = "prop123";

    public static final String UNIT_NUMBER = "0111";

    public static final String CUSTOOMER_ID = "t000111";

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(TaxesDataModel.class);
        models.add(ProductTaxPolicyDataModel.class);
        return models;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater(PROPERTY_CODE).
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
            RtCustomerUpdater updater = new RtCustomerUpdater(PROPERTY_CODE, CUSTOOMER_ID).
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, CUSTOOMER_ID).
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

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "rent").
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
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA").
            set(LeaseChargeUpdater.Name.Description, "Parking A").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rpark").
            set(LeaseChargeUpdater.Name.Amount, "50.00").
            set(LeaseChargeUpdater.Name.Comment, "Parking A");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        setSysDate("25-May-2013");

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        // @formatter:off
        new BillableItemTester(getLease().currentTerm().version().leaseProducts().serviceItem()).
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Regular Residential Unit").
        agreedPrice("1234.56");
        // @formatter:on
    }

    @Test
    public void testProductCharges() throws Exception {
        Lease lease = getLease();
        LogicalDate date = new LogicalDate(getSysDate());
        BillingCycle cycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease, date);
        List<InvoiceProductCharge> charges = ServerSideFactory.create(ARFacade.class).estimateLeaseCharges(cycle, lease);

        assertEquals("Number of charges", 2, charges.size());

        // @formatter:off
        new InvoiceProductChargeTester(charges.get(0)).
        amount("1234.56").
        taxTotal("0.00");
        // @formatter:on

        // @formatter:off
        new InvoiceProductChargeTester(charges.get(1)).
        amount("50.00").
        taxTotal("0.00");
        // @formatter:on
    }

    // ---------- private -----------------

    private Lease getLease() {
        Building building = getBuilding(PROPERTY_CODE);
        AptUnit unit = getUnit(building, UNIT_NUMBER);
        return getCurrentLease(unit);
    }
}
