/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.biz.financial.deposit;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;

@Category(FunctionalTests.class)
public class DepositFullCycleScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setDate("17-Feb-2012");

        setLeaseTerms("01-Mar-2012", "31-May-2012");
        addParking(SaveAction.saveAsDraft);
        addParking("01-Apr-2012", "30-Apr-2012", SaveAction.saveAsDraft);
        addLocker(SaveAction.saveAsDraft);
        addPet("01-Mar-2012", "31-Mar-2012", SaveAction.saveAsDraft);

        setDepositBatchProcess(retrieveLease().unit().building());

        //==================== RUN 1 - SERVICE AND FEATURE DEPOSITS TAKEN ======================//

        advanceDate("18-Feb-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Mar-2012").
        billingPeriodEndDate("31-Mar-2012").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("160.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("1270.30").
        depositRefundAmount("0.00").
        latePaymentFees("0.00").
        taxes("130.84").
        totalDueAmount("2491.44");
        // @formatter:on

        //==================== RUN 2 - SECOND PARKING DEPOSIT TAKEN ======================//

        activateLease();

        advanceDate("01-Mar-2012");
        receiveAndPostPayment("01-Mar-2012", "2491.44");

        advanceDate("18-Mar-2012");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2012").
        billingPeriodEndDate("30-Apr-2012").
        numOfProductCharges(4).
        serviceCharge("930.30").
        recurringFeatureCharges("220.00").
        depositRefundAmount("0.00").
        latePaymentFees("0.00").
        taxes("138.04").
        totalDueAmount("1368.34");
        // @formatter:on

        //==================== RUN 3 - PET DEPOSIT REFUND WITH ONE INTEREST ADJ ======================//

        advanceDate("01-Apr-2012");
        receiveAndPostPayment("01-Apr-2012", "1288.34");

        advanceDate("18-Apr-2012");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-May-2012").
        billingPeriodEndDate("31-May-2012").
        numOfProductCharges(3).
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        depositRefundAmount("-1151.00").
        latePaymentFees("0.00").
        taxes("128.44").
        totalDueAmount("127.74");
        // @formatter:on

        //==================== RUN final - SVC AND FEATURE REFUNDS (see history dump) ======================//

        advanceDate("01-May-2012");
        receiveAndPostPayment("01-May-2012", "127.74");

        advanceDate("18-May-2012");

        completeLease();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(4).
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null).
        depositRefundAmount("0.00").
        latePaymentFees("0.00").
        totalDueAmount("0.00");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}
