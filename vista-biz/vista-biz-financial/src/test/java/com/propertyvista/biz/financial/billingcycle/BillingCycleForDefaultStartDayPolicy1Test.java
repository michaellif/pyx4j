/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import java.text.ParseException;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.test.preloader.PreloadConfig;

public class BillingCycleForDefaultStartDayPolicy1Test extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 1;
        preloadData(config);
    }

    public void testBillingCycleCreation() throws ParseException {
        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "01-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "02-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "05-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "22-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "28-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(getBuilding(),BillingPeriod.Monthly, "29-Feb-2012").
        billingCycleStartDate("01-Feb-2012").
        billingCycleEndDate("29-Feb-2012");
        // @formatter:on

    }

}
