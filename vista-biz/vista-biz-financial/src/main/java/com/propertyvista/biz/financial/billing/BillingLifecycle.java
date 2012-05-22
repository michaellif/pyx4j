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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.BillingRun.BillingRunStatus;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingLifecycle {

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycle.class);

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

        runBilling(billingRun, false);

        return billingRun;
    }

    static BillingRun runBilling(Building building, PaymentFrequency paymentFrequency, LogicalDate billingPeriodStartDate) {
        //TODO

        // runBilling(billingRun, true);

        return null;
    }

    private static void runBilling(BillingRun billingRun, boolean manageTransactions) {
        billingRun.status().setValue(BillingRunStatus.Running);
        billingRun.executionDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        Persistence.service().persist(billingRun);

        if (manageTransactions) {
            Persistence.service().commit();
        }

        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().currentBillingRun(), billingRun));
        List<BillingAccount> billingAccounts = Persistence.service().query(criteria);
        try {
            for (BillingAccount billingAccount : billingAccounts) {
                Billing.createBill(billingRun, billingAccount);
                if (manageTransactions) {
                    Persistence.service().commit();
                }
            }

            billingRun.status().setValue(BillingRunStatus.Finished);
            Persistence.service().persist(billingRun);

            if (manageTransactions) {
                Persistence.service().commit();
            }

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

            if (manageTransactions) {
                Persistence.service().commit();
            }
        }
    }

    static Bill confirmBill(Bill billStub) {
        return verifyBill(billStub, BillStatus.Confirmed);
    }

    static Bill rejectBill(Bill billStub) {
        return verifyBill(billStub, BillStatus.Rejected);
    }

    private static Bill verifyBill(Bill billStub, BillStatus billStatus) {
        Bill bill = Persistence.service().retrieve(Bill.class, billStub.getPrimaryKey());
        if (BillStatus.Finished.equals(bill.billStatus().getValue())) {
            bill.billStatus().setValue(billStatus);

            Persistence.service().retrieve(bill.billingAccount());

            if (BillStatus.Confirmed == billStatus) {
                Persistence.service().retrieve(bill.lineItems());
                consumeExistingLineItems(bill.lineItems());
                postNewLineItems(bill.lineItems());
            }

            bill.billingAccount().currentBillingRun().setValue(null);
            bill.billingAccount().billCounter().setValue(bill.billingAccount().billCounter().getValue() + 1);

            Persistence.service().persist(bill);
            Persistence.service().persist(bill.billingAccount());

        } else {
            throw new BillingException("Bill is in status '" + bill.billStatus().getValue() + "'. Bill should be in 'Finished' state in order to verify it.");
        }
        return bill;
    }

    private static void consumeExistingLineItems(List<InvoiceLineItem> lineItems) {
        for (InvoiceLineItem invoiceLineItem : lineItems) {
            if (!invoiceLineItem.postDate().isNull()) {
                invoiceLineItem.consumed().setValue(true);
                Persistence.service().persist(invoiceLineItem);
            }
        }
    }

    private static void postNewLineItems(List<InvoiceLineItem> lineItems) {
        for (InvoiceLineItem invoiceLineItem : lineItems) {
            if (invoiceLineItem.postDate().isNull() && !invoiceLineItem.amount().getValue().equals(new BigDecimal("0.00"))) {
                ServerSideFactory.create(ARFacade.class).postInvoiceLineItem(invoiceLineItem);
            }
        }
    }

    static BillingRun createBillingRun(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        BillingAccount billingAccount = lease.billingAccount();
        if (previousBill == null) {
            if (lease.billingAccount().initialBalance().isNull()) {
                return BillingCycleManger.createNewLeaseFirstBillingRun(billingAccount.billingCycle(), billingAccount.lease().leaseFrom().getValue(),
                        !billingAccount.billingPeriodStartDate().isNull());

            } else {
                return BillingCycleManger.createExistingLeaseInitialBillingRun(billingAccount.billingCycle(), billingAccount.lease().leaseFrom().getValue(),
                /** TODO fix me **/
                null, !billingAccount.billingPeriodStartDate().isNull());
            }
        } else {
            return BillingCycleManger.createSubsiquentBillingRun(billingAccount.billingCycle(), previousBill.billingRun());
        }
    }

    /**
     * Makes sure <code>lease.billingAccount()</code> is filled with the most recent billing account. Creates BillingAccount when needed.
     */
    static BillingAccount ensureBillingAccount(Lease lease) {
        if (lease.leaseFrom().isNull()) {
            throw new BillingException("'Lease from' date is not set");
        }

        // fetch *BillingAccount* from the persistence to be sure we get the most recent version
        // (i.e. fetching it from lease.billingAccount() might give use not the most recent one, if lease is not up to date for some oblivious reason)
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, lease.billingAccount().getPrimaryKey());
        if (billingAccount == null) {
            billingAccount = EntityFactory.create(BillingAccount.class);
        }
        if (billingAccount.billingCycle().isNull()) {
            billingAccount.billingCycle().set(BillingCycleManger.ensureBillingCycle(lease));
            billingAccount.billCounter().setValue(1);
            Persistence.service().persist(billingAccount);
        }
        lease.billingAccount().set(billingAccount);
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

}
