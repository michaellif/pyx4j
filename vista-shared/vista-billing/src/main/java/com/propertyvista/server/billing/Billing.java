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
package com.propertyvista.server.billing;

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

    private final Bill bill;

    private Billing(Bill bill) {
        this.bill = bill;
    }

    static void createBill(BillingRun billingRun, BillingAccount billingAccount) {
        Persistence.service().retrieve(billingAccount.leaseFinancial());
        Persistence.service().retrieve(billingAccount.leaseFinancial().lease());
        Persistence.service().retrieve(billingAccount.leaseFinancial().lease().leaseProducts());

        Bill bill = EntityFactory.create(Bill.class);

        try {
            Persistence.service().startBackgroundProcessTransaction();

            try {
                bill.billStatus().setValue(Bill.BillStatus.Running);
                bill.billingAccount().set(billingAccount);

                bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
                bill.billingRun().set(billingRun);

                Bill previousBill = BillingLifecycle.getLatestConfirmedBill(billingAccount);
                bill.previousBill().set(previousBill);

                Persistence.service().persist(bill);

                if (Status.New.equals(billingAccount.leaseFinancial().lease().status().getValue())) {// draft bill should be issued
                    bill.billType().setValue(Bill.BillType.Draft);
                } else if (Status.Approved.equals(billingAccount.leaseFinancial().lease().status().getValue())) {// first bill should be issued
                    bill.billType().setValue(Bill.BillType.First);
                    bill.billingPeriodStartDate().setValue(billingAccount.leaseFinancial().lease().leaseFrom().getValue());

                    if (billingAccount.leaseFinancial().lease().leaseTo().isNull()
                            || (billingAccount.leaseFinancial().lease().leaseTo().getValue().compareTo(billingRun.billingPeriodEndDate().getValue()) >= 0)) {
                        bill.billingPeriodEndDate().setValue(billingRun.billingPeriodEndDate().getValue());
                    } else if (billingAccount.leaseFinancial().lease().leaseTo().getValue().compareTo(billingRun.billingPeriodStartDate().getValue()) >= 0) {
                        bill.billingPeriodEndDate().setValue(billingAccount.leaseFinancial().lease().leaseTo().getValue());
                    } else {
                        throw new BillingException("Lease already ended");
                    }

                } else if (Status.Completed.equals(billingAccount.leaseFinancial().lease().status().getValue())) {// final bill should be issued
                    bill.billType().setValue(Bill.BillType.Final);
                } else {
                    bill.billType().setValue(Bill.BillType.Regular);

                    if (BillingLifecycle.getSysDate().compareTo(billingRun.executionTargetDate().getValue()) < 0) {
                        throw new BillingException("Regular billing can't run before target execution date");
                    }

                    bill.billingPeriodStartDate().setValue(billingRun.billingPeriodStartDate().getValue());

                    if (billingAccount.leaseFinancial().lease().leaseTo().isNull()
                            || (billingAccount.leaseFinancial().lease().leaseTo().getValue().compareTo(billingRun.billingPeriodEndDate().getValue()) >= 0)) {
                        bill.billingPeriodEndDate().setValue(billingRun.billingPeriodEndDate().getValue());
                    } else if (billingAccount.leaseFinancial().lease().leaseTo().getValue().compareTo(billingRun.billingPeriodStartDate().getValue()) >= 0) {
                        bill.billingPeriodEndDate().setValue(billingAccount.leaseFinancial().lease().leaseTo().getValue());
                    } else {
                        throw new BillingException("Lease already ended");
                    }
                }

                new Billing(bill).run();
                bill.billStatus().setValue(Bill.BillStatus.Finished);
            } catch (Throwable e) {
                log.error("Bill run error", e);
                bill.billStatus().setValue(Bill.BillStatus.Erred);
            }
            Persistence.service().persist(bill);
            Persistence.service().commit();
        } finally {
            Persistence.service().endTransaction();
        }

    }

    private void run() {

        //Set accumulating fields to 0 value
        bill.paymentReceivedAmount().setValue(new BigDecimal(0));
        bill.recurringFeatureCharges().setValue(new BigDecimal(0));
        bill.oneTimeFeatureCharges().setValue(new BigDecimal(0));
        bill.totalAdjustments().setValue(new BigDecimal(0));
        bill.immediateAdjustments().setValue(new BigDecimal(0));
        bill.latePaymentCharges().setValue(new BigDecimal(0));
        bill.taxes().setValue(new BigDecimal(0));
        bill.depositPaidAmount().setValue(new BigDecimal(0));

        getPreviousTotals();

        new PaymentProcessor(bill).createPayments();
        new ChargeProcessor(bill).createCharges();
        new ChargeAdjustmentProcessor(bill).createChargeAdjustments();
        new LeaseAdjustmentProcessor(bill).createLeaseAdjustments();

        calculateTotals();

    }

    private void getPreviousTotals() {
        Bill lastBill = BillingLifecycle.getLatestConfirmedBill(bill.billingAccount());
        if (lastBill != null) {
            bill.previousBalanceAmount().setValue(lastBill.totalDueAmount().getValue());
        } else {
            bill.previousBalanceAmount().setValue(new BigDecimal(0));
        }
    }

    private void calculateTotals() {
        bill.pastDueAmount().setValue(
                bill.previousBalanceAmount().getValue().subtract(bill.paymentReceivedAmount().getValue()).subtract(bill.immediateAdjustments().getValue()));

        bill.currentAmount().setValue(
                bill.pastDueAmount().getValue().add(bill.serviceCharge().getValue()).add(bill.recurringFeatureCharges().getValue())
                        .add(bill.oneTimeFeatureCharges().getValue()).add(bill.totalAdjustments().getValue()).subtract(bill.depositPaidAmount().getValue())
                        .add(bill.latePaymentCharges().getValue()));

        bill.totalDueAmount().setValue(bill.currentAmount().getValue().add(bill.taxes().getValue()));
    }

}
