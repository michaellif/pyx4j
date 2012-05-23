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
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;
import com.propertyvista.domain.financial.billing.InvoiceNSF;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.ProductType;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.ExecutionType;

public class BillPrintTest extends FinancialTestBase {

    public void testBillPrint() throws FileNotFoundException {

        Bill bill = EntityFactory.create(Bill.class);
        bill.billSequenceNumber().setValue(1);

        bill.billingPeriodStartDate().setValue(new LogicalDate(112, 03, 01));
        bill.dueDate().setValue(new LogicalDate(112, 03, 02));
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

            InvoiceNSF nsf = EntityFactory.create(InvoiceNSF.class);
            nsf.amount().setValue(new BigDecimal("50.00"));
            nsf.description().setValue("NSF");
            bill.lineItems().add(nsf);

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

            productCharge.chargeSubLineItem().amount().setValue(new BigDecimal("800.00"));
            productCharge.chargeSubLineItem().description().setValue("Lease");

            InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);
            adjustment.amount().setValue(new BigDecimal("50.00"));
            adjustment.description().setValue("Price adjustment");
            productCharge.adjustmentSubLineItems().add(adjustment);

            productCharge.productType().setValue(ProductType.service);
            productCharge.amount().setValue(new BigDecimal("850.00"));
            productCharge.description().setValue("Lease");
            bill.lineItems().add(productCharge);

            bill.serviceCharge().setValue(new BigDecimal("850.00"));
        }

        {
            InvoiceProductCharge featureCharge = EntityFactory.create(InvoiceProductCharge.class);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("120.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature A");

            InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);
            adjustment.amount().setValue(new BigDecimal("-20.00"));
            adjustment.description().setValue("Price adjustment");
            featureCharge.adjustmentSubLineItems().add(adjustment);

            featureCharge.productType().setValue(ProductType.recurringFeature);
            featureCharge.amount().setValue(new BigDecimal("100.00"));
            featureCharge.description().setValue("Feature A");
            bill.lineItems().add(featureCharge);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("120.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature A");

            featureCharge = EntityFactory.create(InvoiceProductCharge.class);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("70.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature B");

            adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);
            adjustment.amount().setValue(new BigDecimal("-20.00"));
            adjustment.description().setValue("Price adjustment");
            featureCharge.adjustmentSubLineItems().add(adjustment);

            featureCharge.productType().setValue(ProductType.recurringFeature);
            featureCharge.amount().setValue(new BigDecimal("50.00"));
            featureCharge.description().setValue("Feature B");
            bill.lineItems().add(featureCharge);

            bill.recurringFeatureCharges().setValue(new BigDecimal("150.00"));
        }

        {
            InvoiceProductCharge featureCharge = EntityFactory.create(InvoiceProductCharge.class);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("120.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature A");

            InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);
            adjustment.amount().setValue(new BigDecimal("-20.00"));
            adjustment.description().setValue("Price adjustment");
            featureCharge.adjustmentSubLineItems().add(adjustment);

            featureCharge.productType().setValue(ProductType.oneTimeFeature);
            featureCharge.amount().setValue(new BigDecimal("100.00"));
            featureCharge.description().setValue("Feature A");
            bill.lineItems().add(featureCharge);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("120.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature A");

            featureCharge = EntityFactory.create(InvoiceProductCharge.class);

            featureCharge.chargeSubLineItem().amount().setValue(new BigDecimal("100.00"));
            featureCharge.chargeSubLineItem().description().setValue("Feature B");

            featureCharge.productType().setValue(ProductType.oneTimeFeature);
            featureCharge.amount().setValue(new BigDecimal("50.00"));
            featureCharge.description().setValue("Feature B");
            bill.lineItems().add(featureCharge);

            bill.oneTimeFeatureCharges().setValue(new BigDecimal("200.00"));
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

            InvoiceLatePaymentFee latePaymentFee = EntityFactory.create(InvoiceLatePaymentFee.class);
            latePaymentFee.amount().setValue(new BigDecimal("50.00"));
            latePaymentFee.description().setValue("Late Payment Fee");
            bill.lineItems().add(latePaymentFee);

            bill.pendingAccountAdjustments().setValue(new BigDecimal("150.00"));
        }

        {
            InvoiceDeposit deposit = EntityFactory.create(InvoiceDeposit.class);
            deposit.amount().setValue(new BigDecimal("650.00"));
            deposit.description().setValue("Lease Deposit");
            bill.lineItems().add(deposit);

            deposit = EntityFactory.create(InvoiceDeposit.class);
            deposit.amount().setValue(new BigDecimal("150.00"));
            deposit.description().setValue("Parking Deposit");
            bill.lineItems().add(deposit);

            bill.depositAmount().setValue(new BigDecimal("800.00"));
        }

        {
            InvoiceProductCredit productCredit = EntityFactory.create(InvoiceProductCredit.class);
            productCredit.amount().setValue(new BigDecimal("-650.00"));
            productCredit.description().setValue("Credit 1");
            bill.lineItems().add(productCredit);

            productCredit = EntityFactory.create(InvoiceProductCredit.class);
            productCredit.amount().setValue(new BigDecimal("-50.00"));
            productCredit.description().setValue("Credit 2");
            bill.lineItems().add(productCredit);

            bill.productCreditAmount().setValue(new BigDecimal("-700.00"));
        }
        bill.pastDueAmount().setValue(new BigDecimal("950.00"));

        bill.currentAmount().setValue(new BigDecimal("1150.00"));

        bill.taxes().setValue(new BigDecimal("150.00"));

        bill.totalDueAmount().setValue(new BigDecimal("2250.00"));

        BillPrint.printBill(BillingUtils.createBillDto(bill), new FileOutputStream(billFileName(bill, "DemoBill")));

    }
}
