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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.BillingRun.BillingRunStatus;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;

public class BillingLifecycleManager {

    private static final I18n i18n = I18n.get(BillingLifecycleManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycleManager.class);

    static BillingRun runBilling(Lease lease) {
        BillingAccount billingAccount = ensureBillingAccount(lease);

        if (!billingAccount.currentBillingRun().isNull()) {
            throw new BillingException("Can't run billing on Account with non-confirmed bills");
        }

        BillingRun billingRun = createBillingRun(lease);
        Persistence.service().retrieve(lease.unit());
        billingRun.building().set(lease.unit().belongsTo());
        Persistence.service().persist(billingRun);

        Bill previousBill = BillingLifecycleManager.getLatestConfirmedBill(lease);
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
                createBill(billingRun, billingAccount);
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

    static void createBill(BillingRun billingRun, BillingAccount billingAccount) {
        Persistence.service().retrieve(billingAccount.lease());

        Persistence.service().retrieve(billingAccount.adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billStatus().setValue(Bill.BillStatus.Running);
            bill.billingAccount().set(billingAccount);

            bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
            bill.billingRun().set(billingRun);

            Bill previousBill = BillingLifecycleManager.getLatestConfirmedBill(billingAccount.lease());
            bill.previousBill().set(previousBill);

            Persistence.service().persist(bill);

            if (Status.Created == billingAccount.lease().version().status().getValue()) {//zeroCycle bill should be issued
                if (billingAccount.carryforwardBalance().isNull()) {
                    new EstimateBillingProcessor(bill).run();
                } else {
                    new ZeroCycleBillingProcessor(bill).run();
                }
            } else if (Status.Approved == billingAccount.lease().version().status().getValue()) {// first bill should be issued
                new FirstBillingProcessor(bill).run();
            } else if (Status.Active == billingAccount.lease().version().status().getValue()) {
                new RegularBillingProcessor(bill).run();
            } else if (Status.Completed == billingAccount.lease().version().status().getValue()) {// final bill should be issued
                new FinalBillingProcessor(bill).run();
            } else {
                throw new BillingException(i18n.tr("Billing can't run when lease is in status '{0}'", billingAccount.lease().version().status().getValue()));
            }

            bill.billStatus().setValue(Bill.BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(Bill.BillStatus.Failed);
        }
        Persistence.service().persist(bill);

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
            if (lease.billingAccount().carryforwardBalance().isNull()) {
                return createNewLeaseFirstBillingRun(billingAccount.billingCycle(), billingAccount.lease().leaseFrom().getValue(), !billingAccount
                        .billingPeriodStartDate().isNull());

            } else {
                return createExistingLeaseInitialBillingRun(billingAccount.billingCycle(), billingAccount.lease().leaseFrom().getValue(), billingAccount
                        .lease().creationDate().getValue(), !billingAccount.billingPeriodStartDate().isNull());
            }
        } else {
            return createSubsiquentBillingRun(billingAccount.billingCycle(), previousBill.billingRun());
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
            billingAccount.billingCycle().set(ensureBillingCycle(lease));
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

    static BillingCycle ensureBillingCycle(Lease lease) {
        BillingCycle billingCycle = lease.billingAccount().billingCycle();

        if (billingCycle.isNull()) {
            PaymentFrequency paymentFrequency = lease.paymentFrequency().getValue();
            Integer billingPeriodStartDay = null;
            if (lease.billingAccount().billingPeriodStartDate().isNull()) {
                billingPeriodStartDay = BillDateUtils.calculateBillingCycleStartDay(lease.paymentFrequency().getValue(), lease.leaseFrom().getValue());
            } else {
                billingPeriodStartDay = lease.billingAccount().billingPeriodStartDate().getValue();
            }

            //try to find existing billing cycle    
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentFrequency(), paymentFrequency));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingPeriodStartDay(), billingPeriodStartDay));
            billingCycle = Persistence.service().retrieve(criteria);

            if (billingCycle == null) {
                billingCycle = EntityFactory.create(BillingCycle.class);
                billingCycle.paymentFrequency().setValue(paymentFrequency);
                billingCycle.billingPeriodStartDay().setValue(billingPeriodStartDay);
                billingCycle.billingRunTargetDay().setValue(
                        (billingPeriodStartDay + lease.paymentFrequency().getValue().getNumOfCycles() - lease.paymentFrequency().getValue()
                                .getBillRunTargetDayOffset())
                                % lease.paymentFrequency().getValue().getNumOfCycles());

                Persistence.service().persist(billingCycle);
            }
        }
        return billingCycle;
    }

    static BillingRun createNewLeaseFirstBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        return createBillingRun(cycle, BillDateUtils.calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay));
    }

    static BillingRun createExistingLeaseInitialBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, LogicalDate leaseCreationDate,
            boolean useCyclePeriodStartDay) {
        if (!leaseStartDate.before(leaseCreationDate)) {
            throw new BillingException("Existing lease start date should be earlier than creation date");
        }
        LogicalDate firstBillingRunStartDate = BillDateUtils.calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay);
        LogicalDate billingRunStartDate = null;
        LogicalDate nextBillingRunStartDate = firstBillingRunStartDate;
        do {
            billingRunStartDate = nextBillingRunStartDate;
            nextBillingRunStartDate = BillDateUtils.calculateSubsiquentBillingRunStartDate(cycle.paymentFrequency().getValue(), billingRunStartDate);
        } while (nextBillingRunStartDate.compareTo(leaseCreationDate) <= 0);

        return createBillingRun(cycle, billingRunStartDate);
    }

    static BillingRun createSubsiquentBillingRun(BillingCycle cycle, BillingRun previousRun) {
        return createBillingRun(cycle,
                BillDateUtils.calculateSubsiquentBillingRunStartDate(cycle.paymentFrequency().getValue(), previousRun.billingPeriodStartDate().getValue()));
    }

    private static BillingRun createBillingRun(BillingCycle cycle, LogicalDate billingRunStartDate) {
        BillingRun billingRun = EntityFactory.create(BillingRun.class);
        billingRun.status().setValue(BillingRun.BillingRunStatus.Scheduled);
        billingRun.billingCycle().set(cycle);
        billingRun.billingPeriodStartDate().setValue(billingRunStartDate);
        billingRun.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingRunEndDate(cycle.paymentFrequency().getValue(), billingRunStartDate));
        billingRun.executionTargetDate().setValue(BillDateUtils.calculateBillingRunTargetExecutionDate(cycle, billingRunStartDate));
        return billingRun;

    }
}
