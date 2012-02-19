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
package com.propertyvista.server.billing;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingRunTest extends BillingTestBase {

    public void testSequentialBillingRun() {

        //==================== RUN 1 ======================//

        Bill bill = runBilling(1, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 1, (int) bill.billingPeriodNumber().getValue());

        //==================== RUN 2 ======================//

        bill = runBilling(2, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 4, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 2, (int) bill.billingPeriodNumber().getValue());

        //==================== RUN 3 ======================//

        bill = runBilling(3, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 3, (int) bill.billingPeriodNumber().getValue());

        bill = runBilling(4, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 3, (int) bill.billingPeriodNumber().getValue());

        //==================== RUN 4 ======================//

        bill = runBilling(5, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 4, (int) bill.billingPeriodNumber().getValue());

        //==================== RUN 5 ======================//

        bill = runBilling(6, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        bill = runBilling(7, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        bill = runBilling(8, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        //==================== RUN 6 ======================//

        bill = runBilling(9, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 6, (int) bill.billingPeriodNumber().getValue());

    }

    private Bill runBilling(int billNumber, boolean confirm) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Lease lease = Persistence.service().query(criteria).get(0);
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getBill(lease.leaseFinancial().billingAccount(), lease.leaseFinancial().billingAccount().currentBillingRun());
        if (confirm) {
            BillingFacade.confirmBill(bill);
        } else {
            BillingFacade.rejectBill(bill);
        }

        Persistence.service().retrieve(bill.charges());
        Persistence.service().retrieve(bill.chargeAdjustments());
        Persistence.service().retrieve(bill.leaseAdjustments());

        DataDump.dump("bill", bill);
        DataDump.dump("lease", lease);

        assertEquals("Bill Sequence Number", billNumber, (int) bill.billSequenceNumber().getValue());
        assertEquals("Bill Confirmation Status", confirm ? BillStatus.Confirmed : BillStatus.Rejected, bill.billStatus().getValue());

        assertEquals("ServiceCharge", new BigDecimal("930.30"), bill.serviceCharge().getValue());
        assertEquals("RecurringFeatureCharges", new BigDecimal("78.38"), bill.recurringFeatureCharges().getValue());

        return bill;
    }
}
