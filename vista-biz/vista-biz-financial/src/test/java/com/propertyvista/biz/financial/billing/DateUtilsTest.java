/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

@Category(FunctionalTests.class)
public class DateUtilsTest extends VistaDBTestBase {

    public void testOverlappingRange() {
        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("1-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")),
                    new DateRange(FinancialTestsUtils.getDate("1-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("1-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("5-Jul-2011"), FinancialTestsUtils.getDate("4-Aug-2011")),
                    new DateRange(FinancialTestsUtils.getDate("5-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("8-Jul-2011"), FinancialTestsUtils.getDate("15-Jul-2011")),
                    new DateRange(FinancialTestsUtils.getDate("5-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("8-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("8-Jul-2011"), FinancialTestsUtils.getDate("15-Jul-2011")),
                    new DateRange(FinancialTestsUtils.getDate("5-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("8-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(null, FinancialTestsUtils.getDate("15-Jul-2011")), new DateRange(
                    FinancialTestsUtils.getDate("5-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(null, FinancialTestsUtils.getDate("15-Jul-2011")), new DateRange(
                    FinancialTestsUtils.getDate("5-Jul-2011"), null));
            assertEquals(FinancialTestsUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("8-Jul-2011"), FinancialTestsUtils.getDate("15-Jul-2011")),
                    new DateRange(FinancialTestsUtils.getDate("25-Jul-2011"), FinancialTestsUtils.getDate("31-Jul-2011")));
            assertEquals(null, dateRange);
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(
                    new DateRange(FinancialTestsUtils.getDate("8-Jul-2011"), FinancialTestsUtils.getDate("15-Jul-2011")),
                    new DateRange(FinancialTestsUtils.getDate("12-Jul-2011"), FinancialTestsUtils.getDate("12-Jul-2011")));
            assertEquals(FinancialTestsUtils.getDate("12-Jul-2011"), dateRange.getFromDate());
            assertEquals(FinancialTestsUtils.getDate("12-Jul-2011"), dateRange.getToDate());
        }

    }

    public void testInitialBillingCycleStartDatecalculation() {
        BillingType billingType = EntityFactory.create(BillingType.class);
        billingType.paymentFrequency().setValue(PaymentFrequency.Monthly);

        {
            billingType.billingCycleStartDay().setValue(1);
            LogicalDate date = BillDateUtils.calculateInitialBillingCycleStartDate(billingType, FinancialTestsUtils.getDate("8-Jul-2011"));
            assertEquals(FinancialTestsUtils.getDate("01-Jul-2011"), date);
        }

        {
            billingType.billingCycleStartDay().setValue(2);
            LogicalDate date = BillDateUtils.calculateInitialBillingCycleStartDate(billingType, FinancialTestsUtils.getDate("8-Jul-2011"));
            assertEquals(FinancialTestsUtils.getDate("02-Jul-2011"), date);
        }

        {
            billingType.billingCycleStartDay().setValue(2);
            LogicalDate date = BillDateUtils.calculateInitialBillingCycleStartDate(billingType, FinancialTestsUtils.getDate("30-Jul-2011"));
            assertEquals(FinancialTestsUtils.getDate("02-Jul-2011"), date);
        }

    }
}
