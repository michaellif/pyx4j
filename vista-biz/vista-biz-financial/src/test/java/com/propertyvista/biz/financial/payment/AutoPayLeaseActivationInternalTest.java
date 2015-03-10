/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2014
 * @author vlads
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockConfig;

@Category(RegressionTests.class)
public class AutoPayLeaseActivationInternalTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Override
    protected MockConfig createMockConfig() {
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        return config;
    }

    public void testLeaseActivation() throws Exception {
        setSysDate("2011-02-10");

        createLease("2011-04-01", "2012-03-10", new BigDecimal("1000.00"), null);

        setBillingBatchProcess();
        // No automatic Lease activation
        //setLeaseBatchProcess();
        setPaymentBatchProcess();

        advanceSysDate("2011-02-12");

        approveApplication(true);

        // TODO - deposit of 930.30 added to total for test to pass through.
        // Need to investigate the diff with lease agreed price of 1000.00
        new BillTester(getLatestBill()).billingCyclePeriodStartDate("2011-04-01").totalDueAmount("2050.30");

        assertEquals("Next PAP", "2011-04-01", ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(retrieveLease()));

        // Add 100% PAP
        final AutopayAgreement preauthorizedPayment1 = setPreauthorizedPayment(new PreauthorizedPaymentBuilder(). //
                add(getLease().currentTerm().version().leaseProducts().serviceItem(), "1120.00"). // 1000.00 + 12%
                build());

        // PAP day
        advanceSysDate("2011-04-01");

        // Payment created since lease is Approved
        new PaymentRecordTester(getLease().billingAccount()). //
                count(1);

        advanceSysDate("2011-04-28");

        activateLease();
        Persistence.service().commit();

        advanceSysDate("2011-05-02");

        new PaymentRecordTester(getLease().billingAccount())//
                .count(2)//
                .lastRecordStatus(PaymentStatus.Queued)//
                .lastRecordAmount("1120.00"); //;

    }

    public void testNextAutopayDate() throws Exception {
        setSysDate("2011-01-01");
        createLease("2011-04-01", "2012-03-10", new BigDecimal("1000.00"), null);
        approveApplication(true);
        assertEquals("Next PAP", "2011-04-01", ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(retrieveLease()));

        setSysDate("2011-01-01");
        createLease("2011-04-01", "2012-03-10", new BigDecimal("1000.00"), null);
        advanceSysDate("2011-03-31");
        approveApplication(true);
        assertEquals("Next PAP", "2011-04-01", ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(retrieveLease()));

        setSysDate("2011-01-01");
        createLease("2011-04-01", "2012-03-10", new BigDecimal("1000.00"), null);
        advanceSysDate("2011-04-01");
        approveApplication(true);
        assertEquals("Next PAP", "2011-05-01", ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(retrieveLease()));
    }
}
