/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.Bill;

class AbstractBillingProcessor {

    private final static Logger log = LoggerFactory.getLogger(AbstractBillingProcessor.class);

    private static final I18n i18n = I18n.get(AbstractBillingProcessor.class);

    private final Bill nextPeriodBill;

    private Bill currentPeriodBill;

    private Bill previousPeriodBill;

    AbstractBillingProcessor(Bill bill, Bill.BillType billType) {

        bill.billType().setValue(billType);

        bill.billingPeriodStartDate().setValue(BillDateUtils.calculateBillingPeriodStartDate(bill));
        bill.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingPeriodEndDate(bill));
        bill.dueDate().setValue(BillDateUtils.calculateBillDueDate(bill));

        this.nextPeriodBill = bill;
        if (!bill.previousBill().isNull()) {
            this.currentPeriodBill = bill.previousBill();
            Persistence.service().retrieve(currentPeriodBill.lineItems());
        }
        if (currentPeriodBill != null && !currentPeriodBill.previousBill().isNull()) {
            this.previousPeriodBill = currentPeriodBill.previousBill();
            Persistence.service().retrieve(previousPeriodBill.lineItems());
        }
    }

    void run() {

        //Set accumulating fields to 0 value
        nextPeriodBill.depositRefundAmount().setValue(new BigDecimal(0));
        nextPeriodBill.immediateAccountAdjustments().setValue(new BigDecimal(0));
        nextPeriodBill.withdrawalAmount().setValue(new BigDecimal(0));
        nextPeriodBill.paymentRejectedAmount().setValue(new BigDecimal(0));
        nextPeriodBill.paymentReceivedAmount().setValue(new BigDecimal(0));

        nextPeriodBill.recurringFeatureCharges().setValue(new BigDecimal(0));
        nextPeriodBill.oneTimeFeatureCharges().setValue(new BigDecimal(0));
        nextPeriodBill.pendingAccountAdjustments().setValue(new BigDecimal(0));
        nextPeriodBill.depositAmount().setValue(new BigDecimal(0));
        nextPeriodBill.productCreditAmount().setValue(new BigDecimal(0));

        nextPeriodBill.taxes().setValue(new BigDecimal(0));

        Bill lastBill = BillingLifecycleManager.getLatestConfirmedBill(nextPeriodBill.billingAccount().lease());
        if (lastBill != null) {
            nextPeriodBill.balanceForwardAmount().setValue(lastBill.totalDueAmount().getValue());
        } else {
            nextPeriodBill.balanceForwardAmount().setValue(new BigDecimal(0));
        }

        new BillingInitialBalanceProcessor(this).createInitialBalanceRecord();
        new BillingProductChargeProcessor(this).createCharges();
        new BillingDepositProcessor(this).createDeposits();

        BillingLeaseAdjustmentProcessor leaseAdjustmentProcessor = new BillingLeaseAdjustmentProcessor(this);
        leaseAdjustmentProcessor.createPendingLeaseAdjustments();
        leaseAdjustmentProcessor.attachImmediateLeaseAdjustments();

        new BillingPaymentProcessor(this).attachPaymentRecords();

        //Should run last
        new BillingLatePaymentFeeProcessor(this).createLatePaymentFeeItem();

        calculateTotals();

    }

    private void calculateTotals() {
        nextPeriodBill.pastDueAmount().setValue(
                nextPeriodBill.balanceForwardAmount().getValue().add(nextPeriodBill.paymentReceivedAmount().getValue())
                        .add(nextPeriodBill.paymentRejectedAmount().getValue()).add(nextPeriodBill.immediateAccountAdjustments().getValue())
                        .add(nextPeriodBill.depositRefundAmount().getValue()).add(nextPeriodBill.withdrawalAmount().getValue()));

        nextPeriodBill.currentAmount().setValue(
                nextPeriodBill.pastDueAmount().getValue().add(nextPeriodBill.serviceCharge().getValue())
                        .add(nextPeriodBill.recurringFeatureCharges().getValue()).add(nextPeriodBill.oneTimeFeatureCharges().getValue())
                        .add(nextPeriodBill.pendingAccountAdjustments().getValue()).add(nextPeriodBill.depositAmount().getValue()));

        nextPeriodBill.totalDueAmount().setValue(nextPeriodBill.currentAmount().getValue().add(nextPeriodBill.taxes().getValue()));
    }

    public Bill getNextPeriodBill() {
        return nextPeriodBill;
    }

    public Bill getCurrentPeriodBill() {
        return currentPeriodBill;
    }

    public Bill getPreviousPeriodBill() {
        return previousPeriodBill;
    }

}
