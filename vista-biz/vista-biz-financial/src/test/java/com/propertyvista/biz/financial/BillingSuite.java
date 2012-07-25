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
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.biz.financial.billing.BillableItemAdjustmentTest;
import com.propertyvista.biz.financial.billing.BillingAllFeaturesAdjustmentsFirstMonthProrationTest;
import com.propertyvista.biz.financial.billing.BillingBulkRunTest;
import com.propertyvista.biz.financial.billing.BillingCycleTest;
import com.propertyvista.biz.financial.billing.BillingExistingLeaseSunnyDayScenarioTest;
import com.propertyvista.biz.financial.billing.BillingFeatureEagerScenarioTest;
import com.propertyvista.biz.financial.billing.BillingFirstMonthProrationScenarioTest;
import com.propertyvista.biz.financial.billing.BillingFullCycleScenarioTest;
import com.propertyvista.biz.financial.billing.BillingFullCycleServAdjMonScenarioTest;
import com.propertyvista.biz.financial.billing.BillingLatePaymentScenarioTest;
import com.propertyvista.biz.financial.billing.BillingLeaseOnlyAgingScenarioTest;
import com.propertyvista.biz.financial.billing.BillingLeaseOnlyScenarioTest;
import com.propertyvista.biz.financial.billing.BillingModelTest;
import com.propertyvista.biz.financial.billing.BillingPeriodsTest;
import com.propertyvista.biz.financial.billing.BillingRejectedPaymentScenarioTest;
import com.propertyvista.biz.financial.billing.BillingSunnyDayScenarioTest;
import com.propertyvista.biz.financial.billing.BillingZeroCycleScenarioTest;
import com.propertyvista.biz.financial.billing.DateUtilsTest;
import com.propertyvista.biz.financial.billing.LatePaymentCalculationTest;
import com.propertyvista.biz.financial.billing.ProrationTest;
import com.propertyvista.biz.financial.billing.print.BillPrintTest;
import com.propertyvista.biz.financial.deposit.DepositFullCycleScenarioTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
//@formatter:off
        BillableItemAdjustmentTest.class, 
        BillingAllFeaturesAdjustmentsFirstMonthProrationTest.class, 
        BillingBulkRunTest.class, 
        BillingCycleTest.class,
        BillingExistingLeaseSunnyDayScenarioTest.class, 
        BillingFeatureEagerScenarioTest.class,
        BillingFirstMonthProrationScenarioTest.class, 
        BillingFullCycleScenarioTest.class, 
        BillingFullCycleServAdjMonScenarioTest.class,
        BillingLatePaymentScenarioTest.class, 
        BillingLeaseOnlyAgingScenarioTest.class, 
        BillingLeaseOnlyScenarioTest.class, 
        BillingModelTest.class,
        BillingPeriodsTest.class, 
        BillingRejectedPaymentScenarioTest.class, 
        BillingSunnyDayScenarioTest.class, 
        BillingZeroCycleScenarioTest.class,
        BillTester.class, 
        DateUtilsTest.class, 
        LatePaymentCalculationTest.class, 
        ProrationTest.class, 
        BillPrintTest.class, 
        DepositFullCycleScenarioTest.class,
      //@formatter:on

})
public class BillingSuite {

}
