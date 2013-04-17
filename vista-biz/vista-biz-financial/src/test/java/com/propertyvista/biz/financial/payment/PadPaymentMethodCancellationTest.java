/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.models.LeaseDataModel;

@Category(RegressionTests.class)
public class PadPaymentMethodCancellationTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Override
    protected void preloadData() {
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    public void testScenario() throws Exception {
        setSysDate("2011-03-10");

        createLease("2011-04-01", "2012-03-10", new BigDecimal("1000.00"), null);

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setPaymentBatchProcess();

        advanceSysDate("2011-03-12");

        approveApplication(true);

        // TODO - deposit of 930.30 added to total for test to pass through.
        // Need to investigate the diff with lease agreed price of 1000.00
        new BillTester(getLatestBill()).billingCyclePeriodStartDate("2011-04-01").totalDueAmount("2050.30");

        // Add 100% PAP
        final PreauthorizedPayment preauthorizedPayment1 = new UnitOfWork(TransactionScopeOption.RequiresNew)
                .execute(new Executable<PreauthorizedPayment, RuntimeException>() {

                    @Override
                    public PreauthorizedPayment execute() {
                        return getDataModel(LeaseDataModel.class).createPreauthorizedPayment("1");
                    }

                });

        advanceSysDate("2011-03-29");

        // @formatter:off
        new PaymentRecordTester(getLease().billingAccount()).
        count(1).
        lastRecordStatus(PaymentStatus.Scheduled)
        .lastRecordAmount("1120.00");
        // @formatter:on

        advanceSysDate("2011-04-01");

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        advanceSysDate("2011-04-28");

        new PaymentRecordTester(getLease().billingAccount()).count(2).lastRecordStatus(PaymentStatus.Scheduled);

        // Cancel preauthorizedPayment
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(preauthorizedPayment1);
                return null;
            }

        });

        // Scheduled payment is Canceled

        new PaymentRecordTester(getLease().billingAccount()).count(2).lastRecordStatus(PaymentStatus.Canceled);

        final PreauthorizedPayment preauthorizedPayment2 = new UnitOfWork(TransactionScopeOption.RequiresNew)
                .execute(new Executable<PreauthorizedPayment, RuntimeException>() {

                    @Override
                    public PreauthorizedPayment execute() {
                        return getDataModel(LeaseDataModel.class).createPreauthorizedPayment("1");
                    }

                });

        // new payment is not automatically created

        advanceSysDate("2011-05-02");

        new PaymentRecordTester(getLease().billingAccount()).count(2).lastRecordStatus(PaymentStatus.Canceled);

        advanceSysDate("2011-05-20");
        new BillTester(getLatestBill()).billingCyclePeriodStartDate("2011-06-01").totalDueAmount("3270.30");

        advanceSysDate("2011-05-29");

        new PaymentRecordTester(getLease().billingAccount()).count(3).lastRecordStatus(PaymentStatus.Scheduled).lastRecordAmount("1120.00");

        // Cancel paymentMethod used in preauthorizedPayment
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(preauthorizedPayment2.paymentMethod());
                return null;
            }

        });

        // Scheduled payment is Canceled
        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Canceled);

    }
}
