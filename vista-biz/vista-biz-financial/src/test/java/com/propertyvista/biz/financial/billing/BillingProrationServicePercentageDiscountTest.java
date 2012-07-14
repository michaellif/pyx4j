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
package com.propertyvista.biz.financial.billing;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;

@Ignore
public class BillingProrationServicePercentageDiscountTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario1VISTA1715() {

        setLeaseTerms("05-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);
        addServiceAdjustment("-0.15", AdjustmentType.percentage, ExecutionType.inLease);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", AdjustmentType.monetary, ExecutionType.inLease);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", AdjustmentType.monetary, ExecutionType.inLease);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", AdjustmentType.percentage, ExecutionType.inLease);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", AdjustmentType.percentage, ExecutionType.inLease);

        //==================== RUN 1 ======================

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("05-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        // serviceCharge("237.16").
        serviceCharge("237.17").
        recurringFeatureCharges("60.90").
        depositAmount("1350.30").
        taxes("35.77").
        totalDueAmount("1684.13");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
