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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillCharge;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.BillableItem;

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
        bill.billingRun().set(billingRun);
        Persistence.service().persist(bill);
        try {
            new Billing(bill).run();
            bill.billStatus().setValue(BillStatus.Finished);
            Persistence.service().persist(bill);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(BillStatus.Erred);
            Persistence.service().persist(bill);
        }
    }

    private void run() {
        getPreviousTotals();
        getPayments();
        createCharges();
    }

    private void getPreviousTotals() {
        Bill lastBill = BillingUtils.getLatestBill(bill.billingAccount());
        if (lastBill != null) {
            bill.previousBalanceAmount().setValue(lastBill.totalDueAmount().getValue());
        }
    }

    private void getPayments() {
        // TODO Auto-generated method stub
        bill.paymentReceivedAmount().setValue(25.0);
    }

    private void createCharges() {

        Persistence.service().retrieve(bill.billingAccount().leaseFinancial());

        double totalRecurringCharges = 0;

        totalRecurringCharges += createCharge(bill.billingAccount().leaseFinancial().serviceAgreement().serviceItem()).price().getValue();

        for (BillableItem item : bill.billingAccount().leaseFinancial().serviceAgreement().featureItems()) {
            totalRecurringCharges += createCharge(item).price().getValue();
        }

        bill.totalRecurringCharges().setValue(totalRecurringCharges);
    }

    private BillCharge createCharge(BillableItem serviceItem) {
        return null;
    }

    private void addCharge(BillCharge charge) {

    }

}
