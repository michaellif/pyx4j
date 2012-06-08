/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.biz.financial.Tester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;

public class BillTester extends Tester {

    private final Bill bill;

    public BillTester(Bill bill, boolean continueOnError) {
        super(continueOnError);
        this.bill = bill;
    }

    public BillTester(Bill bill) {
        this(bill, false);
    }

    public BillTester billSequenceNumber(Integer sequenceNumber) {
        assertEquals("Bill Sequence number", sequenceNumber, bill.billSequenceNumber().getValue());
        return this;
    }

    public BillTester previousBillSequenceNumber(Integer sequenceNumber) {
        assertEquals("Previous Bill Sequence number", sequenceNumber, bill.previousBill().billSequenceNumber().getValue());
        return this;
    }

    public BillTester billingTypePeriodStartDay(Integer day) {
        assertEquals("Billing Cycle Period Start Day", day, bill.billingCycle().billingType().billingPeriodStartDay().getValue());
        return this;
    }

    public BillTester billingTypePeriodTargetDay(Integer day) {
        assertEquals("Billing Cycle Period Run Target Day", day, bill.billingCycle().billingType().billingCycleTargetDay().getValue());
        return this;
    }

    public BillTester billingCyclePeriodStartDate(String date) {
        assertEquals("Billing Run Period Start Day", getDate(date), bill.billingCycle().billingPeriodStartDate().getValue());
        return this;
    }

    public BillTester billingCyclePeriodEndDate(String date) {
        assertEquals("Billing Run Period End Day", getDate(date), bill.billingCycle().billingPeriodEndDate().getValue());
        return this;
    }

    public BillTester billingCycleExecutionTargetDate(String date) {
        assertEquals("Billing Run Execution Target Day", getDate(date), bill.billingCycle().executionTargetDate().getValue());
        return this;
    }

    public BillTester billingPeriodStartDate(String date) {
        assertEquals("Billing Period Start Day", getDate(date), bill.billingPeriodStartDate().getValue());
        return this;
    }

    public BillTester billingPeriodEndDate(String date) {
        assertEquals("Billing Period End Day", getDate(date), bill.billingPeriodEndDate().getValue());
        return this;
    }

    public BillTester billType(BillType type) {
        assertEquals("Bill Type", type, bill.billType().getValue());
        return this;
    }

    public BillTester billStatus(Bill.BillStatus status) {
        assertEquals("Bill Status", status, bill.billStatus().getValue());
        return this;
    }

    public BillTester numOfProductCharges(Integer num) {
        assertEquals("Number of Charges", num, BillingUtils.getLineItemsForType(bill, InvoiceProductCharge.class).size());
        return this;
    }

    public BillTester serviceCharge(String value) {
        assertEquals("Service Charge", new BigDecimal(value), bill.serviceCharge().getValue());
        return this;
    }

    public BillTester recurringFeatureCharges(String value) {
        assertEquals("Recurring Feature Charges", new BigDecimal(value), bill.recurringFeatureCharges().getValue());
        return this;
    }

    public BillTester oneTimeFeatureCharges(String value) {
        assertEquals("One Time Feature Charges", new BigDecimal(value), bill.oneTimeFeatureCharges().getValue());
        return this;
    }

    public BillTester taxes(String value) {
        assertEquals("Taxes", new BigDecimal(value), bill.taxes().getValue());
        return this;
    }

    public BillTester paymentReceivedAmount(String value) {
        assertEquals("Payment Received Amount", new BigDecimal(value), bill.paymentReceivedAmount().getValue());
        return this;
    }

    public BillTester paymentRejectedAmount(String value) {
        assertEquals("Payment Rejected Amount", new BigDecimal(value), bill.paymentRejectedAmount().getValue());
        return this;
    }

    public BillTester nsfCharges(String value) {
        assertEquals("NSF Charges", new BigDecimal(value), bill.nsfCharges().getValue());
        return this;
    }

    public BillTester latePaymentFees(String value) {
        assertEquals("Late Payment Fees", new BigDecimal(value), bill.latePaymentFees().getValue());
        return this;
    }

    public BillTester depositAmount(String value) {
        assertEquals("Deposit Amount", new BigDecimal(value), bill.depositAmount().getValue());
        return this;
    }

    public BillTester totalDueAmount(String value) {
        assertEquals("Total Due Amount", new BigDecimal(value), bill.totalDueAmount().getValue());
        return this;
    }

    public BillTester pastDueAmount(String value) {
        assertEquals("Past Due Amount", new BigDecimal(value), bill.pastDueAmount().getValue());
        return this;
    }

    protected static LogicalDate getDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return FinancialTestsUtils.getDate(date);
        } catch (Exception e) {
            throw new Error("Failed to parse date " + date);
        }

    }

}
