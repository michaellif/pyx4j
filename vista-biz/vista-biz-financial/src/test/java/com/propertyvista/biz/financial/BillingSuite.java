/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 21, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.propertyvista.biz.financial.billing.internal.BillExecutionWithAutoApprovalTest;
import com.propertyvista.biz.financial.billing.internal.BillExecutionWithManualApprovalTest;
import com.propertyvista.biz.financial.billing.internal.BillableItemAdjustmentTest;
import com.propertyvista.biz.financial.billing.internal.BillingAllFeaturesAdjustmentsFirstMonthProrationTest;
import com.propertyvista.biz.financial.billing.internal.BillingBulkRunTest;
import com.propertyvista.biz.financial.billing.internal.BillingExistingLeaseSunnyDayScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingFeatureEagerScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingFirstMonthProrationScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingFullCycleScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingFullCycleServAdjMonScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingLatePaymentScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingLeaseOnlyAgingScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingModelTest;
import com.propertyvista.biz.financial.billing.internal.BillingPeriodsTest;
import com.propertyvista.biz.financial.billing.internal.BillingRejectedPaymentScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingSunnyDayScenarioTest;
import com.propertyvista.biz.financial.billing.internal.BillingZeroCycleScenarioTest;
import com.propertyvista.biz.financial.billing.internal.DateUtilsTest;
import com.propertyvista.biz.financial.billing.internal.LatePaymentCalculationTest;
import com.propertyvista.biz.financial.billing.internal.ProrationTest;
import com.propertyvista.biz.financial.billing.print.BillPrintTest;
import com.propertyvista.biz.financial.deposit.DepositFullCycleScenarioTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
//@formatter:off
        BillableItemAdjustmentTest.class, 
        BillingAllFeaturesAdjustmentsFirstMonthProrationTest.class, 
        BillingBulkRunTest.class, 
        BillingExistingLeaseSunnyDayScenarioTest.class, 
        BillingFeatureEagerScenarioTest.class,
        BillingFirstMonthProrationScenarioTest.class, 
        BillingFullCycleScenarioTest.class, 
        BillingFullCycleServAdjMonScenarioTest.class,
        BillingLatePaymentScenarioTest.class, 
        BillingLeaseOnlyAgingScenarioTest.class, 
        BillExecutionWithManualApprovalTest.class, 
        BillExecutionWithAutoApprovalTest.class, 
        BillingModelTest.class,
        BillingPeriodsTest.class, 
        BillingRejectedPaymentScenarioTest.class, 
        BillingSunnyDayScenarioTest.class, 
        BillingZeroCycleScenarioTest.class,
        DateUtilsTest.class, 
        LatePaymentCalculationTest.class, 
        ProrationTest.class, 
        BillPrintTest.class, 
        DepositFullCycleScenarioTest.class,
      //@formatter:on

})
public class BillingSuite {

}
