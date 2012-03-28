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
package com.propertyvista.server.accounting.billing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.BillingRun.BillingRunStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingLifecycle {

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycle.class);

    private static LogicalDate sysDate;

    static BillingRun runBilling(Lease lease) {
        BillingAccount billingAccount = ensureBillingAccount(lease);

        if (!billingAccount.currentBillingRun().isNull()) {
            throw new BillingException("Can't run billing on Account with non-confirmed bills");
        }

        BillingRun billingRun = createBillingRun(lease);
        Persistence.service().retrieve(lease.unit());
        billingRun.building().set(lease.unit().belongsTo());
        Persistence.service().persist(billingRun);

        Bill previousBill = BillingLifecycle.getLatestConfirmedBill(lease);
        if (previousBill != null && Bill.BillType.Final.equals(previousBill.billType().getValue())) {
            throw new BillingException("Final bill has been issued");
        }

        billingAccount.currentBillingRun().set(billingRun);
        Persistence.service().persist(billingAccount);
        Persistence.service().commit();

        runBilling(billingRun);

        return billingRun;
    }

    static BillingRun runBilling(Building building, PaymentFrequency paymentFrequency, Integer billingDay, LogicalDate billingPeriodStartDate) {
        //TODO
        return null;
    }

    private static void runBilling(BillingRun billingRun) {
        billingRun.status().setValue(BillingRunStatus.Running);
        billingRun.executionDate().setValue(new LogicalDate());
        Persistence.service().persist(billingRun);

        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().currentBillingRun(), billingRun));
        List<BillingAccount> billingAccounts = Persistence.service().query(criteria);
        try {
            for (BillingAccount billingAccount : billingAccounts) {
                Billing.createBill(billingRun, billingAccount);
            }

            billingRun.status().setValue(BillingRunStatus.Finished);
            Persistence.service().persist(billingRun);
            Persistence.service().commit();

        } catch (Throwable e) {
            Persistence.service().rollback();
            log.error("Bill run error", e);

            for (BillingAccount billingAccount : billingAccounts) {
                billingAccount.currentBillingRun().setValue(null);
                billingAccount.billCounter().setValue(billingAccount.billCounter().getValue() + 1);
                Persistence.service().persist(billingAccount);
            }

            billingRun.status().setValue(BillingRunStatus.Erred);
            Persistence.service().persist(billingRun);
            Persistence.service().commit();

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

            bill.billingAccount().interimLineItems().clear();

            Persistence.service().persist(bill.billingAccount());

            Persistence.service().commit();
        } else {
            throw new BillingException("Bill is in status '" + bill.billStatus().getValue() + "'. Bill should be in 'Finished' state in order to verify it.");
        }
    }

    static BillingRun createBillingRun(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        BillingAccount billingAccount = lease.billingAccount();
        if (previousBill == null) {
            return BillingCycleManger.createFirstBillingRun(billingAccount.billingCycle(), billingAccount.lease().leaseFrom().getValue(), !billingAccount
                    .billingPeriodStartDate().isNull());
        } else {
            return BillingCycleManger.createSubsiquentBillingRun(billingAccount.billingCycle(), previousBill.billingRun());
        }
    }

    /*
     * Creates BillingAccount when needed
     */
    static BillingAccount ensureBillingAccount(Lease lease) {
        if (lease.leaseFrom().isNull()) {
            throw new BillingException("'Lease from' date is not set");
        }
        BillingAccount billingAccount = lease.billingAccount();
        if (billingAccount.isValueDetached()) {
            Persistence.service().retrieve(billingAccount);
        }
        if (billingAccount.billingCycle().isNull()) {
            billingAccount.billingCycle().set(BillingCycleManger.ensureBillingCycle(lease));
            billingAccount.billCounter().setValue(1);
            Persistence.service().persist(billingAccount);
        }
        return billingAccount;

    }

    static Bill getLatestConfirmedBill(Lease lease) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.eq(criteria.proto().billStatus(), BillStatus.Confirmed));
        criteria.desc(criteria.proto().billSequenceNumber());
        return Persistence.service().retrieve(criteria);
    }

    static Bill getLatestBill(Lease lease) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.desc(criteria.proto().billSequenceNumber());
        return Persistence.service().retrieve(criteria);
    }

    static LogicalDate getSysDate() {
        if (sysDate != null) {
            return sysDate;
        } else {
            return new LogicalDate();
        }
    }

    static void setSysDate(LogicalDate date) {
        sysDate = date;
    }
}
