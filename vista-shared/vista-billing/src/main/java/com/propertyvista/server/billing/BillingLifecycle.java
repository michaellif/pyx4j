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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle.BillingFrequency;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.BillingRun.BillingRunStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingLifecycle {

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycle.class);

    static BillingRun runBilling(Lease lease) {
        BillingAccount billingAccount = BillingUtils.ensureBillingAccount(lease);
        if (!billingAccount.currentBillingRun().isNull()) {
            throw new UserRuntimeException("Can't run billing on Account with non-confirmed bills");
        }
        BillingRun billingRun = EntityFactory.create(BillingRun.class);
        billingRun.status().setValue(BillingRunStatus.Scheduled);
        billingRun.billingCycle().set(billingAccount.billingCycle());
        Persistence.service().retrieve(lease.unit());
        billingRun.building().set(lease.unit().belongsTo());

        billingRun.billingPeriodStartDate().setValue(BillingUtils.getNextBillingPeriodStartDate(billingAccount));

        Persistence.service().persist(billingRun);

        billingAccount.currentBillingRun().set(billingRun);
        Persistence.service().persist(billingAccount);

        runBilling(billingRun);

        return billingRun;
    }

    static BillingRun runBilling(Building building, BillingFrequency billingPeriod, Integer billingDay, LogicalDate billingPeriodStartDate) {
        //TODO
        return null;
    }

    private static void runBilling(BillingRun billingRun) {
        billingRun.status().setValue(BillingRunStatus.Running);
        billingRun.executionDate().setValue(new LogicalDate());
        Persistence.service().persist(billingRun);
        try {

            EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().currentBillingRun(), billingRun));
            for (BillingAccount billingAccount : Persistence.service().query(criteria)) {
                Billing.createBill(billingRun, billingAccount);
            }

            billingRun.status().setValue(BillingRunStatus.Finished);
            Persistence.service().persist(billingRun);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            billingRun.status().setValue(BillingRunStatus.Erred);
            Persistence.service().persist(billingRun);
        }
    }

    static void confirmBill(Bill bill) {
        verifyBill(bill, BillStatus.Confirmed);
    }

    static void rejectBill(Bill bill) {
        verifyBill(bill, BillStatus.Rejected);
    }

    private static void verifyBill(Bill bill, BillStatus billStatus) {
        if (BillStatus.Finished.equals(bill.billStatus().getValue())) {
            bill.billStatus().setValue(billStatus);
            Persistence.service().persist(bill);

            bill.billingAccount().currentBillingRun().setValue(null);
            bill.billingAccount().billCounter().setValue(bill.billingAccount().billCounter().getValue() + 1);

            if (BillStatus.Confirmed.equals(billStatus)) {
                bill.billingAccount().billingPeriodCounter().setValue(bill.billingAccount().billingPeriodCounter().getValue() + 1);
            }

            Persistence.service().persist(bill.billingAccount());
        } else {
            throw new Error("Bill is in status '" + bill.billStatus().getValue() + "'. Bill should be in 'Finished' state in order to verify it.");
        }
    }
}
