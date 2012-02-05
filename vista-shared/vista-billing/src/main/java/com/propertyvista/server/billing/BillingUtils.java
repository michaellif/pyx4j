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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingCycle.BillingPeriod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseFinancial;

public class BillingUtils {

    static LogicalDate getNextBillingPeriodStartDate(BillingAccount billingAccount) {
        Bill bill = BillingUtils.getLatestConfirmedBill(billingAccount);
        if (bill != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(bill.billingRun().billingPeriodStartDate().getValue());
            switch (billingAccount.billingCycle().billingPeriod().getValue()) {
            case monthly:
                // TODO use proper bill day
                c.add(Calendar.MONTH, 1);
                break;
            case weekly:
            case biWeekly:
            case semiMonthly:
            case annyally:
                throw new Error("Not implemented");
            }
            return new LogicalDate(c.getTime());
        } else {
            return billingAccount.leaseFinancial().lease().leaseFrom().getValue();
        }

    }

    static Bill getLatestBill(BillingAccount billingAccount, Bill.BillStatus status) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        if (status != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().billStatus(), status));
        }
        criteria.desc(criteria.proto().billingRun().billingPeriodStartDate());
        return Persistence.service().retrieve(criteria);
    }

    /**
     * Returns last bill in any status
     */
    static Bill getLatestBill(BillingAccount billingAccount) {
        return getLatestBill(billingAccount, null);
    }

    /**
     * Returns last bill in status 'Confirmed'
     */
    static Bill getLatestConfirmedBill(BillingAccount billingAccount) {
        return getLatestBill(billingAccount, BillStatus.Confirmed);
    }

    /*
     * Creates BillingAccount when possible
     */
    static BillingAccount ensureBillingAccount(Lease lease) {
        if (lease.leaseFrom().isNull()) {
            throw new Error();
        }
        LeaseFinancial leaseFinancial = lease.leaseFinancial();
        if (leaseFinancial.isValueDetached()) {
            Persistence.service().retrieve(leaseFinancial);
        }
        if (!leaseFinancial.billingAccount().id().isNull()) {
            return leaseFinancial.billingAccount();
        }

        // TOTO select a day base on lease.leaseFrom(), e.g. use Policy
        int billingDay = 1;
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingDay(), billingDay));
        criteria.add(PropertyCriterion.eq(criteria.proto().billingPeriod(), BillingPeriod.monthly));
        BillingCycle billingCycle = Persistence.service().retrieve(criteria);
        // Auto Create BillingCycle for now. 
        if (billingCycle == null) {
            billingCycle = EntityFactory.create(BillingCycle.class);
            billingCycle.billingDay().setValue(billingDay);
            billingCycle.billingPeriod().setValue(BillingPeriod.monthly);
            Persistence.service().persist(billingCycle);
        }
        leaseFinancial.billingAccount().billingCycle().set(billingCycle);
        leaseFinancial.billingAccount().leaseFinancial().set(leaseFinancial);
        leaseFinancial.billingAccount().billCounter().setValue(1);
        Persistence.service().persist(leaseFinancial.billingAccount());
        Persistence.service().persist(leaseFinancial);

        return leaseFinancial.billingAccount();

    }

    public static void confirmBill(Bill bill) {
        verifyBill(bill, BillStatus.Confirmed);
    }

    public static void rejectBill(Bill bill) {
        verifyBill(bill, BillStatus.Rejected);
    }

    private static void verifyBill(Bill bill, BillStatus billStatus) {
        if (BillStatus.Finished.equals(bill.billStatus().getValue())) {
            bill.billStatus().setValue(billStatus);
            Persistence.service().persist(bill);

            bill.billingAccount().currentBillingRun().setValue(null);
            bill.billingAccount().billCounter().setValue(bill.billingAccount().billCounter().getValue() + 1);
            Persistence.service().persist(bill.billingAccount());
        } else {
            throw new Error("Bill should be in 'Finished' state in order to verify it.");
        }
    }
}
