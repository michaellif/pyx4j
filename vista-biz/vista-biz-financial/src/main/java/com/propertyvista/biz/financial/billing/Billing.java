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
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.Lease.Status;

class Billing {

    private final static Logger log = LoggerFactory.getLogger(Billing.class);

    private final Bill nextPeriodBill;

    private Bill currentPeriodBill;

    private Bill previousPeriodBill;

    private final BillingPaymentProcessor paymentProcessor;

    private final BillingDepositProcessor depositProcessor;

    private final BillingLeaseAdjustmentProcessor leaseAdjustmentProcessor;

    private final BillingProductChargeProcessor productChargeProcessor;

    private Billing(Bill bill) {
        this.nextPeriodBill = bill;
        if (!bill.previousBill().isNull()) {
            this.currentPeriodBill = bill.previousBill();
            Persistence.service().retrieve(currentPeriodBill.lineItems());
        }
        if (currentPeriodBill != null && !currentPeriodBill.previousBill().isNull()) {
            this.previousPeriodBill = currentPeriodBill.previousBill();
            Persistence.service().retrieve(previousPeriodBill.lineItems());
        }

        productChargeProcessor = new BillingProductChargeProcessor(this);

        depositProcessor = new BillingDepositProcessor(this);

        paymentProcessor = new BillingPaymentProcessor(this);

        leaseAdjustmentProcessor = new BillingLeaseAdjustmentProcessor(this);

    }

    private void run() {

        //Set accumulating fields to 0 value
        nextPeriodBill.paymentReceivedAmount().setValue(new BigDecimal(0));
        nextPeriodBill.recurringFeatureCharges().setValue(new BigDecimal(0));
        nextPeriodBill.oneTimeFeatureCharges().setValue(new BigDecimal(0));
        nextPeriodBill.totalAdjustments().setValue(new BigDecimal(0));
        nextPeriodBill.immediateAdjustments().setValue(new BigDecimal(0));
        nextPeriodBill.depositRefundAmount().setValue(new BigDecimal(0));
        nextPeriodBill.taxes().setValue(new BigDecimal(0));
        nextPeriodBill.depositAmount().setValue(new BigDecimal(0));

        getPreviousTotals();

        productChargeProcessor.createCharges();
        depositProcessor.createDeposits();
        leaseAdjustmentProcessor.createPendingLeaseAdjustments();
        leaseAdjustmentProcessor.attachImmediateLeaseAdjustments();

        paymentProcessor.attachPayments();

        calculateTotals();

    }

    private void getPreviousTotals() {
        Bill lastBill = BillingLifecycle.getLatestConfirmedBill(nextPeriodBill.billingAccount().lease());
        if (lastBill != null) {
            nextPeriodBill.previousBalanceAmount().setValue(lastBill.totalDueAmount().getValue());
        } else {
            nextPeriodBill.previousBalanceAmount().setValue(new BigDecimal(0));
        }
    }

    private void calculateTotals() {
        nextPeriodBill.pastDueAmount().setValue(
                nextPeriodBill.previousBalanceAmount().getValue().subtract(nextPeriodBill.paymentReceivedAmount().getValue())
                        .subtract(nextPeriodBill.immediateAdjustments().getValue()).subtract(nextPeriodBill.depositRefundAmount().getValue()));

        nextPeriodBill.currentAmount().setValue(
                nextPeriodBill.pastDueAmount().getValue().add(nextPeriodBill.serviceCharge().getValue())
                        .add(nextPeriodBill.recurringFeatureCharges().getValue()).add(nextPeriodBill.oneTimeFeatureCharges().getValue())
                        .add(nextPeriodBill.totalAdjustments().getValue()).add(nextPeriodBill.depositAmount().getValue()));

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

    public BillingPaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public BillingLeaseAdjustmentProcessor getLeaseAdjustmentProcessor() {
        return leaseAdjustmentProcessor;
    }

    static void createBill(BillingRun billingRun, BillingAccount billingAccount) {
        Persistence.service().retrieveMember(billingAccount.interimLineItems());

        Persistence.service().retrieve(billingAccount.lease());

        Persistence.service().retrieve(billingAccount.adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billStatus().setValue(Bill.BillStatus.Running);
            bill.billingAccount().set(billingAccount);

            bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
            bill.billingRun().set(billingRun);

            Bill previousBill = BillingLifecycle.getLatestConfirmedBill(billingAccount.lease());
            bill.previousBill().set(previousBill);

            Persistence.service().persist(bill);

            bill.draft().setValue(Status.Created.equals(billingAccount.lease().version().status().getValue()));

            if (Status.Approved.equals(billingAccount.lease().version().status().getValue())) {// first bill should be issued
                bill.billType().setValue(Bill.BillType.First);
                bill.billingPeriodStartDate().setValue(billingAccount.lease().leaseFrom().getValue());

                if (billingAccount.lease().leaseTo().isNull()
                        || (billingAccount.lease().leaseTo().getValue().compareTo(billingRun.billingPeriodEndDate().getValue()) >= 0)) {
                    bill.billingPeriodEndDate().setValue(billingRun.billingPeriodEndDate().getValue());
                } else if (billingAccount.lease().leaseTo().getValue().compareTo(billingRun.billingPeriodStartDate().getValue()) >= 0) {
                    bill.billingPeriodEndDate().setValue(billingAccount.lease().leaseTo().getValue());
                } else {
                    throw new BillingException("Lease already ended");
                }

            } else if (Status.Completed.equals(billingAccount.lease().version().status().getValue())) {// final bill should be issued
                bill.billType().setValue(Bill.BillType.Final);
            } else {
                bill.billType().setValue(Bill.BillType.Regular);

                if (BillingLifecycle.getSysDate().compareTo(billingRun.executionTargetDate().getValue()) < 0) {
                    throw new BillingException("Regular billing can't run before target execution date");
                }

                bill.billingPeriodStartDate().setValue(billingRun.billingPeriodStartDate().getValue());

                if (billingAccount.lease().leaseTo().isNull()
                        || (billingAccount.lease().leaseTo().getValue().compareTo(billingRun.billingPeriodEndDate().getValue()) >= 0)) {
                    bill.billingPeriodEndDate().setValue(billingRun.billingPeriodEndDate().getValue());
                } else if (billingAccount.lease().leaseTo().getValue().compareTo(billingRun.billingPeriodStartDate().getValue()) >= 0) {
                    bill.billingPeriodEndDate().setValue(billingAccount.lease().leaseTo().getValue());
                } else {
                    throw new BillingException("Lease already ended");
                }
            }

            new Billing(bill).run();
            bill.billStatus().setValue(Bill.BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(Bill.BillStatus.Failed);
        }
        Persistence.service().persist(bill);

    }
}
