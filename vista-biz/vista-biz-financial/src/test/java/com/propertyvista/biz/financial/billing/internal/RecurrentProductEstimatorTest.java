/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.internal;

import java.util.List;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.InvoiceProductChargeTester;
import com.propertyvista.biz.financial.billing.LeaseProductsPriceEstimator;
import com.propertyvista.biz.financial.billingcycle.BillingCycleTester;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class RecurrentProductEstimatorTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");
        addServiceAdjustment("-25", ValueType.Monetary);

        BillableItem parking1 = addOutdoorParking();
        addFeatureAdjustment(parking1.uuid().getValue(), "-10", ValueType.Monetary);

        BillableItem parking2 = addOutdoorParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uuid().getValue(), "-10", ValueType.Monetary);

        BillableItem locker1 = addLargeLocker();
        addFeatureAdjustment(locker1.uuid().getValue(), "-0.2", ValueType.Percentage);

        addBooking("01-Apr-2011");

        {
            BillingCycle billingCycle = BillingCycleTester.ensureBillingCycleForDate(getLease(), "23-Mar-2011");
            List<InvoiceProductCharge> charges = new LeaseProductsPriceEstimator(billingCycle, retrieveLease()).calculateCharges();
            assertEquals("Number of charges", 0, charges.size());
        }

        {
            BillingCycle billingCycle = BillingCycleTester.ensureBillingCycleForDate(getLease(), "01-Apr-2011");
            List<InvoiceProductCharge> charges = new LeaseProductsPriceEstimator(billingCycle, retrieveLease()).calculateCharges();
            assertEquals("Number of charges", 3, charges.size());

            new InvoiceProductChargeTester(charges.get(0)). //
                    amount("905.30"). //
                    taxTotal("108.64");

            new InvoiceProductChargeTester(charges.get(1)). //
                    amount("70.00"). //
                    taxTotal("8.40");

            new InvoiceProductChargeTester(charges.get(2)). //
                    amount("48.00"). //
                    taxTotal("5.76");
        }

        {
            BillingCycle billingCycle = BillingCycleTester.ensureBillingCycleForDate(getLease(), "01-May-2011");
            List<InvoiceProductCharge> charges = new LeaseProductsPriceEstimator(billingCycle, retrieveLease()).calculateCharges();
            assertEquals("Number of charges", 4, charges.size());

            new InvoiceProductChargeTester(charges.get(0)). //
                    amount("905.30"). //
                    taxTotal("108.64");

            new InvoiceProductChargeTester(charges.get(1)). //
                    amount("70.00"). //
                    taxTotal("8.40");

            new InvoiceProductChargeTester(charges.get(2)). //
                    amount("70.00"). //
                    taxTotal("8.40");

            new InvoiceProductChargeTester(charges.get(3)). //
                    amount("48.00"). //
                    taxTotal("5.76");
        }

        {
            BillingCycle billingCycle = BillingCycleTester.ensureBillingCycleForDate(getLease(), "01-Aug-2011");
            List<InvoiceProductCharge> charges = new LeaseProductsPriceEstimator(billingCycle, retrieveLease()).calculateCharges();
            assertEquals("Number of charges", 0, charges.size());
        }

    }
}
