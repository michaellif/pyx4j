/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-29
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.print;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.ProductType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;

public class BillPrintTest extends FinancialTestBase {

    public void testBillPrint() throws FileNotFoundException {

        Bill bill = EntityFactory.create(Bill.class);
        bill.billSequenceNumber().setValue(1);

        bill.totalDueAmount().setValue(new BigDecimal("1000.00"));
        bill.billingPeriodStartDate().setValue(new LogicalDate(112, 03, 01));
        bill.billingRun().executionDate().setValue(new LogicalDate(112, 02, 15));

        bill.balanceForwardAmount().setValue(new BigDecimal("1000.00"));

        {
            InvoiceDepositRefund depositRefund = EntityFactory.create(InvoiceDepositRefund.class);
            depositRefund.amount().setValue(new BigDecimal("-150.00"));
            depositRefund.description().setValue("Deposit Refund 1");
            bill.lineItems().add(depositRefund);

            depositRefund = EntityFactory.create(InvoiceDepositRefund.class);
            depositRefund.amount().setValue(new BigDecimal("-50.00"));
            depositRefund.description().setValue("Deposit Refund 2");
            bill.lineItems().add(depositRefund);

            bill.depositRefundAmount().setValue(new BigDecimal("-200.00"));
        }

        {
            InvoiceAccountCharge accountCharge = EntityFactory.create(InvoiceAccountCharge.class);
            accountCharge.adjustment().executionType().setValue(ExecutionType.immediate);
            accountCharge.amount().setValue(new BigDecimal("250.00"));
            accountCharge.description().setValue("Account Charge");
            bill.lineItems().add(accountCharge);

            InvoiceAccountCredit accountCredit = EntityFactory.create(InvoiceAccountCredit.class);
            accountCredit.adjustment().executionType().setValue(ExecutionType.immediate);
            accountCredit.amount().setValue(new BigDecimal("-50.00"));
            accountCredit.description().setValue("Account Credit");
            bill.lineItems().add(accountCredit);

            bill.immediateAccountAdjustments().setValue(new BigDecimal("200.00"));
        }

        {
            InvoiceWithdrawal withdrawal = EntityFactory.create(InvoiceWithdrawal.class);
            withdrawal.amount().setValue(new BigDecimal("100.00"));
            withdrawal.description().setValue("Withdrawal 1");
            bill.lineItems().add(withdrawal);

            withdrawal = EntityFactory.create(InvoiceWithdrawal.class);
            withdrawal.amount().setValue(new BigDecimal("50.00"));
            withdrawal.description().setValue("Withdrawal 2");
            bill.lineItems().add(withdrawal);

            bill.withdrawalAmount().setValue(new BigDecimal("150.00"));
        }

        {
            InvoicePaymentBackOut paymentBackOut = EntityFactory.create(InvoicePaymentBackOut.class);
            paymentBackOut.amount().setValue(new BigDecimal("200.00"));
            paymentBackOut.description().setValue("Rejected Payment 1");
            bill.lineItems().add(paymentBackOut);

            paymentBackOut = EntityFactory.create(InvoicePaymentBackOut.class);
            paymentBackOut.amount().setValue(new BigDecimal("500.00"));
            paymentBackOut.description().setValue("Rejected Payment 2");
            bill.lineItems().add(paymentBackOut);

            bill.paymentRejectedAmount().setValue(new BigDecimal("500.00"));
        }

        {
            InvoicePayment payment = EntityFactory.create(InvoicePayment.class);
            payment.amount().setValue(new BigDecimal("-300.00"));
            payment.description().setValue("Payment 1");
            bill.lineItems().add(payment);

            payment = EntityFactory.create(InvoicePayment.class);
            payment.amount().setValue(new BigDecimal("-400.00"));
            payment.description().setValue("Payment 2");
            bill.lineItems().add(payment);

            bill.paymentReceivedAmount().setValue(new BigDecimal("-700.00"));
        }

        {
            InvoiceProductCharge productCharge = EntityFactory.create(InvoiceProductCharge.class);
            productCharge.productType().setValue(ProductType.service);
            productCharge.amount().setValue(new BigDecimal("850.00"));
            productCharge.description().setValue("Lease");
            bill.lineItems().add(productCharge);

            bill.serviceCharge().setValue(new BigDecimal("850.00"));
        }

        {
            InvoiceAccountCharge accountCharge = EntityFactory.create(InvoiceAccountCharge.class);
            accountCharge.adjustment().executionType().setValue(ExecutionType.pending);
            accountCharge.amount().setValue(new BigDecimal("150.00"));
            accountCharge.description().setValue("Account Charge");
            bill.lineItems().add(accountCharge);

            InvoiceAccountCredit accountCredit = EntityFactory.create(InvoiceAccountCredit.class);
            accountCredit.adjustment().executionType().setValue(ExecutionType.pending);
            accountCredit.amount().setValue(new BigDecimal("-100.00"));
            accountCredit.description().setValue("Account Credit");
            bill.lineItems().add(accountCredit);

            bill.immediateAccountAdjustments().setValue(new BigDecimal("50.00"));
        }

        bill.pastDueAmount().setValue(new BigDecimal("950.00"));

        bill.currentAmount().setValue(new BigDecimal("1150.00"));

        bill.taxes().setValue(new BigDecimal("150.00"));

        bill.totalDueAmount().setValue(new BigDecimal("1300.00"));

        BillPrint.printBill(BillingUtils.createBillDto(bill), new FileOutputStream(billFileName(bill, "DemoBill")));

    }
}
