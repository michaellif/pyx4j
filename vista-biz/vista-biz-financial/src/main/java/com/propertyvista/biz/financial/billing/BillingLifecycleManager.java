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
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;

public class BillingLifecycleManager {

    private static final I18n i18n = I18n.get(BillingLifecycleManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycleManager.class);

    static BillingCycle runBilling(Lease lease) {
        BillingAccount billingAccount = ensureBillingAccount(lease);

        if (!billingAccount.currentBillingCycle().isNull()) {
            throw new BillingException("Can't run billing on Account with non-confirmed bills");
        }

        BillingCycle billingCycle = createBillingCycle(lease);
        Persistence.service().retrieve(lease.unit());
        billingCycle.building().set(lease.unit().belongsTo());
        Persistence.service().persist(billingCycle);

        Bill previousBill = BillingLifecycleManager.getLatestConfirmedBill(lease);
        if (previousBill != null && Bill.BillType.Final.equals(previousBill.billType().getValue())) {
            throw new BillingException("Final bill has been issued");
        }

        billingAccount.currentBillingCycle().set(billingCycle);
        Persistence.service().persist(billingAccount);
        Persistence.service().commit();

        runBilling(billingCycle, false);

        return billingCycle;
    }

    static BillingCycle runBilling(Building building, PaymentFrequency paymentFrequency, LogicalDate billingPeriodStartDate) {
        //TODO

        // runBilling(billingCycle, true);

        return null;
    }

    private static void runBilling(BillingCycle billingCycle, boolean manageTransactions) {
        Persistence.service().persist(billingCycle);

        if (manageTransactions) {
            Persistence.service().commit();
        }

        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().currentBillingCycle(), billingCycle));
        List<BillingAccount> billingAccounts = Persistence.service().query(criteria);
        try {
            for (BillingAccount billingAccount : billingAccounts) {
                createBill(billingCycle, billingAccount);
                if (manageTransactions) {
                    Persistence.service().commit();
                }
            }

            Persistence.service().persist(billingCycle);

            if (manageTransactions) {
                Persistence.service().commit();
            }

        } catch (Throwable e) {
            Persistence.service().rollback();
            log.error("Bill run error", e);

            for (BillingAccount billingAccount : billingAccounts) {
                billingAccount.currentBillingCycle().setValue(null);
                billingAccount.billCounter().setValue(billingAccount.billCounter().getValue() + 1);
                Persistence.service().persist(billingAccount);
            }

            Persistence.service().persist(billingCycle);

            if (manageTransactions) {
                Persistence.service().commit();
            }
        }
    }

    static void createBill(BillingCycle billingCycle, BillingAccount billingAccount) {
        Persistence.service().retrieve(billingAccount.lease());

        Persistence.service().retrieve(billingAccount.adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billStatus().setValue(Bill.BillStatus.Running);
            bill.billingAccount().set(billingAccount);

            bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
            bill.billingCycle().set(billingCycle);

            Bill previousBill = BillingLifecycleManager.getLatestConfirmedBill(billingAccount.lease());
            bill.previousBill().set(previousBill);

            bill.executionDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

            Persistence.service().persist(bill);

            AbstractBillingManager processor = null;

            if (Status.Created == billingAccount.lease().version().status().getValue()) {//zeroCycle bill should be issued
                if (billingAccount.carryforwardBalance().isNull()) {
                    processor = new EstimateBillingManager(bill);
                } else {
                    processor = new ZeroCycleBillingManager(bill);
                }
            } else if (Status.Approved == billingAccount.lease().version().status().getValue()) {// first bill should be issued
                processor = new FirstBillingManager(bill);
            } else if (Status.Active == billingAccount.lease().version().status().getValue()) {
                processor = new RegularBillingManager(bill);
            } else if (Status.Completed == billingAccount.lease().version().status().getValue()) {// final bill should be issued
                processor = new FinalBillingManager(bill);
            } else {
                throw new BillingException(i18n.tr("Billing can't run when lease is in status '{0}'", billingAccount.lease().version().status().getValue()));
            }

            processor.processBill();

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

            bill.billingAccount().currentBillingCycle().setValue(null);
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

    static BillingCycle createBillingCycle(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        BillingAccount billingAccount = lease.billingAccount();
        if (previousBill == null) {
            if (lease.billingAccount().carryforwardBalance().isNull()) {
                return createNewLeaseFirstBillingCycle(billingAccount.billingType(), billingAccount.lease().leaseFrom().getValue(), !billingAccount
                        .billingPeriodStartDate().isNull());

            } else {
                return createExistingLeaseInitialBillingCycle(billingAccount.billingType(), billingAccount.lease().leaseFrom().getValue(), billingAccount
                        .lease().creationDate().getValue(), !billingAccount.billingPeriodStartDate().isNull());
            }
        } else {
            return createSubsiquentBillingCycle(billingAccount.billingType(), previousBill.billingCycle());
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
        if (billingAccount.billingType().isNull()) {
            billingAccount.billingType().set(ensureBillingType(lease));
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

    static boolean isLatestBill(Bill bill) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), bill.billingAccount()));
        criteria.add(PropertyCriterion.gt(criteria.proto().billSequenceNumber(), bill.billSequenceNumber()));
        return !Persistence.service().exists(criteria);
    }

    static BillingType ensureBillingType(Lease lease) {
        BillingType billingType = lease.billingAccount().billingType();

        if (billingType.isNull()) {
            PaymentFrequency paymentFrequency = lease.paymentFrequency().getValue();
            Integer billingPeriodStartDay = null;
            if (lease.billingAccount().billingPeriodStartDate().isNull()) {
                billingPeriodStartDay = BillDateUtils.calculateBillingTypeStartDay(lease.paymentFrequency().getValue(), lease.leaseFrom().getValue());
            } else {
                billingPeriodStartDay = lease.billingAccount().billingPeriodStartDate().getValue();
            }

            //try to find existing billing cycle    
            EntityQueryCriteria<BillingType> criteria = EntityQueryCriteria.create(BillingType.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentFrequency(), paymentFrequency));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingPeriodStartDay(), billingPeriodStartDay));
            billingType = Persistence.service().retrieve(criteria);

            if (billingType == null) {
                billingType = EntityFactory.create(BillingType.class);
                billingType.paymentFrequency().setValue(paymentFrequency);
                billingType.billingPeriodStartDay().setValue(billingPeriodStartDay);
                billingType.billingCycleTargetDay().setValue(
                        (billingPeriodStartDay + lease.paymentFrequency().getValue().getNumOfCycles() - lease.paymentFrequency().getValue()
                                .getBillRunTargetDayOffset())
                                % lease.paymentFrequency().getValue().getNumOfCycles());

                Persistence.service().persist(billingType);
            }
        }
        return billingType;
    }

    static BillingCycle createNewLeaseFirstBillingCycle(BillingType cycle, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        return createBillingCycle(cycle, BillDateUtils.calculateFirstBillingCycleStartDate(cycle, leaseStartDate, useCyclePeriodStartDay));
    }

    static BillingCycle createExistingLeaseInitialBillingCycle(BillingType cycle, LogicalDate leaseStartDate, LogicalDate leaseCreationDate,
            boolean useCyclePeriodStartDay) {
        if (!leaseStartDate.before(leaseCreationDate)) {
            throw new BillingException("Existing lease start date should be earlier than creation date");
        }
        LogicalDate firstBillingCycleStartDate = BillDateUtils.calculateFirstBillingCycleStartDate(cycle, leaseStartDate, useCyclePeriodStartDay);
        LogicalDate billingCycleStartDate = null;
        LogicalDate nextBillingCycleStartDate = firstBillingCycleStartDate;
        do {
            billingCycleStartDate = nextBillingCycleStartDate;
            nextBillingCycleStartDate = BillDateUtils.calculateSubsiquentBillingCycleStartDate(cycle.paymentFrequency().getValue(), billingCycleStartDate);
        } while (nextBillingCycleStartDate.compareTo(leaseCreationDate) <= 0);

        return createBillingCycle(cycle, billingCycleStartDate);
    }

    static BillingCycle createSubsiquentBillingCycle(BillingType cycle, BillingCycle previousRun) {
        return createBillingCycle(cycle,
                BillDateUtils.calculateSubsiquentBillingCycleStartDate(cycle.paymentFrequency().getValue(), previousRun.billingPeriodStartDate().getValue()));
    }

    private static BillingCycle createBillingCycle(BillingType cycle, LogicalDate billingCycleStartDate) {
        BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
        billingCycle.billingType().set(cycle);
        billingCycle.billingPeriodStartDate().setValue(billingCycleStartDate);
        billingCycle.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingCycleEndDate(cycle.paymentFrequency().getValue(), billingCycleStartDate));
        billingCycle.executionTargetDate().setValue(BillDateUtils.calculateBillingCycleTargetExecutionDate(cycle, billingCycleStartDate));
        return billingCycle;

    }
}
