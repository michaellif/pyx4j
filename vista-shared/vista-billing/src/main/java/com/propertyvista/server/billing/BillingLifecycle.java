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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingCycle.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.BillingRun.BillingRunStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseFinancial;

public class BillingLifecycle {

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycle.class);

    /*
     * Creates BillingAccount when possible
     */
    public static BillingAccount ensureBillingAccount(Lease lease) {
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

        Persistence.service().persist(leaseFinancial.billingAccount());
        Persistence.service().persist(leaseFinancial);

        return leaseFinancial.billingAccount();

    }

    static Bill getLatestBill(BillingAccount billingAccount) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.eq(criteria.proto().billStatus(), BillStatus.Approved));
        criteria.asc(criteria.proto().billingRun().billingPeriodStartDate());
        return Persistence.service().retrieve(criteria);
    }

    static LogicalDate getNextBillingPeriodStartDate(BillingAccount billingAccount, Bill bill) {
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
    }

    public static void runBilling(Lease lease) {
        BillingAccount billingAccount = ensureBillingAccount(lease);
        if (!billingAccount.currentBillingRun().isNull()) {
            throw new UserRuntimeException("Can't run billing on with un-approved bills");
        }
        BillingRun billingRun = EntityFactory.create(BillingRun.class);
        billingRun.status().setValue(BillingRunStatus.Scheduled);
        billingRun.billingCycle().set(billingAccount.billingCycle());
        billingRun.building().set(lease.unit().belongsTo());

        Bill bill = getLatestBill(billingAccount);
        if (bill != null) {
            billingRun.billingPeriodStartDate().setValue(getNextBillingPeriodStartDate(billingAccount, bill));
        } else {
            billingRun.billingPeriodStartDate().setValue(lease.leaseFrom().getValue());
        }

        Persistence.service().persist(billingRun);

        billingAccount.currentBillingRun().set(billingRun);
        Persistence.service().persist(billingAccount);

        runBilling(billingRun);
    }

    public static void runBilling(BillingRun billingRun) {
        billingRun.status().setValue(BillingRunStatus.Running);
        billingRun.executionDate().setValue(new LogicalDate());
        Persistence.service().persist(billingRun);
        try {

            billingRun.status().setValue(BillingRunStatus.Finished);
            Persistence.service().persist(billingRun);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            billingRun.status().setValue(BillingRunStatus.Erred);
            Persistence.service().persist(billingRun);
        }
    }
}
