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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Filter;
import com.pyx4j.commons.FilterIterator;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.server.jobs.StatisticsUtils;

public class BillingLifecycleManager {

    private static final I18n i18n = I18n.get(BillingLifecycleManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingLifecycleManager.class);

    static BillCreationResult runBilling(Lease lease) {
        BillingAccount billingAccount = ensureInitBillingAccount(lease);
        BillingCycle billingCycle = getNextBillBillingCycle(lease);
        validateBillingRunPreconditions(billingCycle, billingAccount);

        try {
            return createBill(billingCycle, billingAccount);
        } catch (Throwable e) {
            Persistence.service().rollback();
            log.error("Bill run error", e);
            return new BillCreationResult("Bill run error");
        }

    }

    static void runBilling(final BillingCycle billingCycle, StatisticsRecord dynamicStatisticsRecord) {
        EntityQueryCriteria<BillingAccount> billingAccountCriteria = EntityQueryCriteria.create(BillingAccount.class);
        billingAccountCriteria.add(PropertyCriterion.eq(billingAccountCriteria.proto().lease().unit().belongsTo(), billingCycle.building()));
        billingAccountCriteria.add(PropertyCriterion.eq(billingAccountCriteria.proto().billingType(), billingCycle.billingType()));
        ICursorIterator<BillingAccount> billingAccountIterator = Persistence.service().query(null, billingAccountCriteria, AttachLevel.Attached);
        FilterIterator<BillingAccount> filteredbillingAccountIterator = new FilterIterator<BillingAccount>(billingAccountIterator,
                new Filter<BillingAccount>() {

                    @Override
                    public boolean accept(BillingAccount billingAccount) {
                        try {
                            validateBillingRunPreconditions(billingCycle, billingAccount);
                            return true;
                        } catch (BillingException e) {
                            return false;
                        }
                    }
                });
        runBilling(billingCycle, filteredbillingAccountIterator, true, dynamicStatisticsRecord);
    }

    static void runBilling(LogicalDate date, StatisticsRecord dynamicStatisticsRecord) {
        for (BillingType billingType : Persistence.service().query(EntityQueryCriteria.create(BillingType.class))) {

            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
            criteria.add(PropertyCriterion.eq(criteria.proto().executionTargetDate(), date));

            ICursorIterator<BillingCycle> billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
            while (billingCycleIterator.hasNext()) {
                BillingCycle billingCycle = billingCycleIterator.next();
                runBilling(billingCycle, dynamicStatisticsRecord);
            }
        }
    }

    private static void runBilling(BillingCycle billingCycle, Iterator<BillingAccount> billingAccounts, boolean manageTransactions,
            StatisticsRecord dynamicStatisticsRecord) {
        if (manageTransactions) {
            Persistence.service().commit();
        }
        try {
            while (billingAccounts.hasNext()) {
                BillCreationResult result = createBill(billingCycle, billingAccounts.next());
                appendStats(dynamicStatisticsRecord, result);
                if (manageTransactions) {
                    Persistence.service().commit();
                }
            }
        } catch (Throwable e) {
            Persistence.service().rollback();
            log.error("Bill run error", e);
            appendStats(dynamicStatisticsRecord, new BillCreationResult(i18n.tr("Bill run error")));
            if (manageTransactions) {
                Persistence.service().commit();
            }
        }
    }

    private static void appendStats(StatisticsRecord dynamicStatisticsRecord, BillCreationResult result) {
        if (result.getStatus() == BillCreationResult.Status.created) {
            StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            Double amountProcessed = dynamicStatisticsRecord.amountProcessed().getValue();
            dynamicStatisticsRecord.amountProcessed().setValue(amountProcessed != null ? amountProcessed : 0 + result.getTotalDueAmount().doubleValue());
        } else {
            StatisticsUtils.addFailed(dynamicStatisticsRecord, 1);
        }
    }

    static BillCreationResult createBill(BillingCycle billingCycle, BillingAccount billingAccount) {

        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.adjustments());

        billingAccount.billCounter().setValue(billingAccount.billCounter().getValue() + 1);
        Persistence.service().persist(billingAccount);

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billStatus().setValue(Bill.BillStatus.Running);
            bill.billingAccount().set(billingAccount);

            bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
            bill.billingCycle().set(billingCycle);

            Bill previousCycleBill = BillingLifecycleManager.getLatestConfirmedBill(billingAccount.lease());
            bill.previousCycleBill().set(previousCycleBill);

            bill.executionDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

            AbstractBillingManager manager = null;

            if (Status.Created == billingAccount.lease().version().status().getValue()) {//zeroCycle bill should be issued
                if (billingAccount.carryforwardBalance().isNull()) {
                    manager = new EstimateBillingManager(bill);
                } else {
                    manager = new ZeroCycleBillingManager(bill);
                }
            } else if (Status.Approved == billingAccount.lease().version().status().getValue()) {// first bill should be issued
                manager = new FirstBillingManager(bill);
            } else if (Status.Active == billingAccount.lease().version().status().getValue()) {
                manager = new RegularBillingManager(bill);
            } else if (Status.Completed == billingAccount.lease().version().status().getValue()) {// final bill should be issued
                manager = new FinalBillingManager(bill);
            } else {
                throw new BillingException(i18n.tr("Billing can't run when lease is in status '{0}'", billingAccount.lease().version().status().getValue()));
            }

            manager.processBill();

            bill.billStatus().setValue(Bill.BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(Bill.BillStatus.Failed);
            String billCreationError = i18n.tr("Bill run error");
            if (BillingException.class.isAssignableFrom(e.getClass())) {
                billCreationError = e.getMessage();
            }
            bill.billCreationError().setValue(billCreationError);
        }
        Persistence.service().persist(bill);

        return new BillCreationResult(bill);
    }

    private static void validateBillingRunPreconditions(BillingCycle billingCycle, BillingAccount billingAccount) {

        Persistence.service().retrieve(billingAccount.lease());

        if (!getNextBillBillingCycle(billingAccount.lease()).equals(billingCycle)) {
            throw new BillingException(i18n.tr("Bill can't be created for a given billing cycle"));
        }

        if (billingAccount.lease().version().status().getValue() == Lease.Status.Closed) {
            throw new BillingException("Lease is closed");
        }

        Bill previousBill = BillingLifecycleManager.getLatestBill(billingAccount.lease());
        if (previousBill != null) {
            if (BillStatus.notConfirmed(previousBill.billStatus().getValue())) {
                throw new BillingException(i18n.tr("Can't run billing on Account with non-confirmed bills"));
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

            if (BillStatus.Confirmed == billStatus) {
                Persistence.service().retrieve(bill.lineItems());
                claimExistingLineItems(bill.lineItems());
                postNewLineItems(bill.lineItems());
            }

            Persistence.service().persist(bill);

        } else {
            throw new BillingException("Bill is in status '" + bill.billStatus().getValue() + "'. Bill should be in 'Finished' state in order to verify it.");
        }
        return bill;
    }

    private static void claimExistingLineItems(List<InvoiceLineItem> lineItems) {
        for (InvoiceLineItem invoiceLineItem : lineItems) {
            if (!invoiceLineItem.postDate().isNull()) {
                invoiceLineItem.claimed().setValue(true);
                Persistence.service().persist(invoiceLineItem);
            }
        }
    }

    private static void postNewLineItems(List<InvoiceLineItem> lineItems) {
        for (InvoiceLineItem invoiceLineItem : lineItems) {
            if (invoiceLineItem.postDate().isNull() && invoiceLineItem.amount().getValue().compareTo(BigDecimal.ZERO) != 0) {
                ServerSideFactory.create(ARFacade.class).postInvoiceLineItem(invoiceLineItem);
            }
        }
    }

    static BillingCycle getNextBillBillingCycle(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, lease.billingAccount().getPrimaryKey());
        Persistence.service().retrieve(lease.unit());

        if (previousBill == null) {
            if (billingAccount.carryforwardBalance().isNull()) {
                return getNewLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().belongsTo(), lease.leaseFrom().getValue(), !billingAccount
                        .billingType().billingCycleStartDay().isNull());

            } else {
                return getExistingLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().belongsTo(), lease.leaseFrom().getValue(), lease
                        .creationDate().getValue(), !billingAccount.billingType().billingCycleStartDay().isNull());
            }
        } else {
            return getSubsiquentBillingCycle(previousBill.billingCycle());
        }
    }

    /**
     * Makes sure <code>lease.billingAccount()</code> is filled with the most recent billing account. Creates BillingAccount when needed.
     */
    static BillingAccount ensureInitBillingAccount(Lease lease) {
        if (lease.leaseFrom().isNull()) {
            throw new BillingException("'Lease from' date is not set");
        }

        // fetch *BillingAccount* from the persistence to be sure we get the most recent version
        // (i.e. fetching it from lease.billingAccount() might give use not the most recent one, if lease is not up to date for some oblivious reason)
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, lease.billingAccount().getPrimaryKey());
        if (billingAccount == null) {
            throw new BillingException("BillingAccount not exist");
        }
        if (billingAccount.billingType().isNull()) {
            billingAccount.billingType().set(ensureBillingType(lease));
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

            LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().belongsTo(),
                    LeaseBillingPolicy.class);
            Integer billingCycleStartDay = leaseBillingPolicy.defaultBillingCycleSartDay().getValue();
            if (billingCycleStartDay == null) {
                billingCycleStartDay = BillDateUtils.calculateBillingTypeStartDay(lease.paymentFrequency().getValue(), lease.leaseFrom().getValue());
            }

            //try to find existing billing cycle    
            EntityQueryCriteria<BillingType> criteria = EntityQueryCriteria.create(BillingType.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentFrequency(), paymentFrequency));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingCycleStartDay(), billingCycleStartDay));
            billingType = Persistence.service().retrieve(criteria);

            if (billingType == null) {
                billingType = EntityFactory.create(BillingType.class);
                billingType.paymentFrequency().setValue(paymentFrequency);
                billingType.billingCycleStartDay().setValue(billingCycleStartDay);
                billingType.billingCycleTargetDay().setValue(
                        (billingCycleStartDay + lease.paymentFrequency().getValue().getNumOfCycles() - paymentFrequency.getBillRunTargetDayOffset())
                                % lease.paymentFrequency().getValue().getNumOfCycles());

                Persistence.service().persist(billingType);
            }
        }
        return billingType;
    }

    static BillingCycle getNewLeaseInitialBillingCycle(BillingType billingType, Building building, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        return ensureBillingCycle(billingType, building,
                BillDateUtils.calculateInitialBillingCycleStartDate(billingType, leaseStartDate, useCyclePeriodStartDay));
    }

    static BillingCycle getExistingLeaseInitialBillingCycle(BillingType billingType, Building building, LogicalDate leaseStartDate,
            LogicalDate leaseCreationDate, boolean useCyclePeriodStartDay) {
        if (!leaseStartDate.before(leaseCreationDate)) {
            throw new BillingException("Existing lease start date should be earlier than creation date");
        }
        LogicalDate firstBillingCycleStartDate = BillDateUtils.calculateInitialBillingCycleStartDate(billingType, leaseStartDate, useCyclePeriodStartDay);
        LogicalDate billingCycleStartDate = null;
        LogicalDate nextBillingCycleStartDate = firstBillingCycleStartDate;
        do {
            billingCycleStartDate = nextBillingCycleStartDate;
            nextBillingCycleStartDate = BillDateUtils
                    .calculateSubsiquentBillingCycleStartDate(billingType.paymentFrequency().getValue(), billingCycleStartDate);
        } while (nextBillingCycleStartDate.compareTo(leaseCreationDate) <= 0);

        return ensureBillingCycle(billingType, building, billingCycleStartDate);
    }

    static BillingCycle getSubsiquentBillingCycle(BillingCycle previousBillingCycle) {
        return ensureBillingCycle(previousBillingCycle.billingType(), previousBillingCycle.building(), BillDateUtils.calculateSubsiquentBillingCycleStartDate(
                previousBillingCycle.billingType().paymentFrequency().getValue(), previousBillingCycle.billingCycleStartDate().getValue()));
    }

    private static BillingCycle ensureBillingCycle(BillingType billingType, Building building, LogicalDate billingCycleStartDate) {

        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
        criteria.add(PropertyCriterion.eq(criteria.proto().billingCycleStartDate(), billingCycleStartDate));
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        BillingCycle billingCycle = Persistence.service().retrieve(criteria);

        if (billingCycle == null) {
            billingCycle = EntityFactory.create(BillingCycle.class);
            billingCycle.billingType().set(billingType);
            billingCycle.building().set(building);
            billingCycle.billingCycleStartDate().setValue(billingCycleStartDate);
            billingCycle.billingCycleEndDate().setValue(
                    BillDateUtils.calculateBillingCycleEndDate(billingType.paymentFrequency().getValue(), billingCycleStartDate));
            billingCycle.executionTargetDate().setValue(BillDateUtils.calculateBillingCycleTargetExecutionDate(billingType, billingCycleStartDate));

            Persistence.service().persist(billingCycle);
        }
        return billingCycle;

    }

    static void initializeFutureBillingCycles() {
        for (BillingType billingType : Persistence.service().query(EntityQueryCriteria.create(BillingType.class))) {

            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().billingCycles().$().billingType(), billingType));
            ICursorIterator<Building> buildingIterator = Persistence.service().query(null, buildingCriteria, AttachLevel.IdOnly);
            while (buildingIterator.hasNext()) {
                Building building = buildingIterator.next();

                // Find latest BillingCycle
                EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                criteria.desc(criteria.proto().billingCycleStartDate());
                BillingCycle latestBillingCycle = Persistence.service().retrieve(criteria);

                Calendar createUntill = new GregorianCalendar();
                createUntill.setTime(Persistence.service().getTransactionSystemTime());
                createUntill.add(Calendar.MONTH, 1);

                while (latestBillingCycle.billingCycleStartDate().getValue().before(createUntill.getTime())) {
                    latestBillingCycle = getSubsiquentBillingCycle(latestBillingCycle);
                }

            }
            Persistence.service().commit();
        }
    }

}
