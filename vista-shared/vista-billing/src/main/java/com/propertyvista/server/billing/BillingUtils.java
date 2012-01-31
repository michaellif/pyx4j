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

    static Bill getLatestBill(BillingAccount billingAccount) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.eq(criteria.proto().billStatus(), BillStatus.Confirmed));
        criteria.asc(criteria.proto().billingRun().billingPeriodStartDate());
        return Persistence.service().retrieve(criteria);
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

        Persistence.service().persist(leaseFinancial.billingAccount());
        Persistence.service().persist(leaseFinancial);

        return leaseFinancial.billingAccount();

    }

}
