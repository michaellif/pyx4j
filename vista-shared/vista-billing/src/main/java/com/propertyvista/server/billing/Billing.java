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
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingRun;

class Billing {

    private final static Logger log = LoggerFactory.getLogger(Billing.class);

    private final Bill bill;

    private Billing(Bill bill) {
        this.bill = bill;
    }

    static void createBill(BillingRun billingRun, BillingAccount billingAccount) {
        Bill bill = EntityFactory.create(Bill.class);
        bill.billStatus().setValue(BillStatus.Running);
        bill.billingAccount().set(billingAccount);

        bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
        bill.billingPeriodNumber().setValue(billingAccount.billingPeriodCounter().getValue());

        bill.billingRun().set(billingRun);
        Persistence.service().persist(bill);
        try {
            new Billing(bill).run();
            bill.billStatus().setValue(BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(BillStatus.Erred);
        }
        Persistence.service().persist(bill);
    }

    private void run() {
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial());
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial().lease());
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial().lease().serviceAgreement());

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
        Bill lastBill = BillingLifecycle.getLatestBill(bill.billingAccount());
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
