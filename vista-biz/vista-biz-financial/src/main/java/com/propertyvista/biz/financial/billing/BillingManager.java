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
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Filter;
import com.pyx4j.commons.FilterIterator;
import com.pyx4j.commons.Key;
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
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.portal.rpc.shared.BillingException;
import com.propertyvista.server.jobs.StatisticsUtils;
import com.propertyvista.server.jobs.TaskRunner;

public class BillingManager {

    private static final I18n i18n = I18n.get(BillingManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingManager.class);

    static Bill runBilling(Lease leaseId, boolean preview) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asCurrentKey()));
        if (lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        }

        BillingCycle billingCycle = getNextBillingCycle(lease);
        validateBillingRunPreconditions(billingCycle, lease, preview);
        return produceBill(billingCycle, lease, preview);
    }

    /**
     * Runs all Billing Cycles which executionTargetDate is the same as date.
     * 
     * @param date
     *            - executionTargetDate
     * @param dynamicStatisticsRecord
     */
    static void runBilling(LogicalDate date, StatisticsRecord dynamicStatisticsRecord) {
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().executionTargetDate(), date));
        ICursorIterator<BillingCycle> billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        while (billingCycleIterator.hasNext()) {
            runBilling(billingCycleIterator.next(), dynamicStatisticsRecord);
        }
    }

    static void runBilling(final BillingCycle billingCycle, StatisticsRecord dynamicStatisticsRecord) {
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit().building(), billingCycle.building()));
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().billingAccount().billingType(), billingCycle.billingType()));
        leaseCriteria.add(PropertyCriterion.in(leaseCriteria.proto().status(), Lease.Status.Active));

        ICursorIterator<Lease> leaseIterator = Persistence.service().query(null, leaseCriteria, AttachLevel.Attached);
        FilterIterator<Lease> filteredLeaseIterator = new FilterIterator<Lease>(leaseIterator, new Filter<Lease>() {
            @Override
            public boolean accept(Lease lease) {
                try {
                    if (isLeaseLastBillingCycle(billingCycle, lease)) {
                        return false;
                    } else {
                        validateBillingRunPreconditions(billingCycle, lease, false);
                        return true;
                    }
                } catch (BillingException e) {
                    log.error(i18n.tr("Billing Run Preconditions failed"), e);
                    return false;
                }
            }
        });
        runBilling(billingCycle, filteredLeaseIterator, dynamicStatisticsRecord);
    }

    /**
     * Billing shoudn't run on a lease with
     */
    private static boolean isLeaseLastBillingCycle(BillingCycle billingCycle, Lease lease) {
        return billingCycle.billingCycleStartDate().getValue().compareTo(lease.leaseTo().getValue()) <= 0
                && billingCycle.billingCycleEndDate().getValue().compareTo(lease.leaseTo().getValue()) >= 0;
    }

    private static void validateBillingRunPreconditions(BillingCycle billingCycle, Lease lease, boolean preview) {

        if (!getNextBillingCycle(lease).equals(billingCycle)) {
            getNextBillingCycle(lease);
            throw new BillingException(i18n.tr("Bill can't be created for a given billing cycle"));
        }

        if (lease.currentTerm().getPrimaryKey().isDraft() && !preview) {
            throw new BillingException(i18n.tr("Lease Term is in draft state. Billing can run only in preview mode."));
        }

        if (lease.status().getValue() == Lease.Status.Closed) {
            throw new BillingException("Lease is closed");
        }

        Bill previousBill = getLatestBill(lease);
        if (previousBill != null) {
            if (BillStatus.notConfirmed(previousBill.billStatus().getValue())) {
                throw new BillingException(i18n.tr("Can't run billing on Account with non-confirmed bills"));
            }
        }

        Bill previousConfirmedBill = getLatestConfirmedBill(lease);
        if (previousConfirmedBill != null) {

            Persistence.service().retrieve(previousConfirmedBill.billingAccount());

            //check if previous confirmed Bill is the last cycle bill and only final bill should run after
            boolean isPreviousConfirmedBillTheLast = previousConfirmedBill.billingPeriodEndDate().getValue().compareTo(lease.currentTerm().termTo().getValue()) == 0;

            //previous bill wasn't the last one so we are dealing here with the regular bill which can't run before executionTargetDate
            if (!isPreviousConfirmedBillTheLast && SysDateManager.getSysDate().compareTo(billingCycle.executionTargetDate().getValue()) < 0) {
                throw new BillingException(i18n.tr("Regular billing can't run before target execution date"));
            }

            //previous bill was the last one so we have to run a final bill but not before lease end date or lease move-out date whatever is first
            if (isPreviousConfirmedBillTheLast && (SysDateManager.getSysDate().compareTo(lease.currentTerm().termTo().getValue()) < 0)
                    && (lease.expectedMoveOut().isNull() || (SysDateManager.getSysDate().compareTo(lease.expectedMoveOut().getValue()) < 0))) {
                throw new BillingException(i18n.tr("Final billing can't run before both lease end date and move-out date"));
            }
        }

    }

    private static void runBilling(BillingCycle billingCycle, Iterator<Lease> leasesIterator, StatisticsRecord dynamicStatisticsRecord) {
        Persistence.service().commit();
        try {
            while (leasesIterator.hasNext()) {
                BillCreationResult result = new BillCreationResult(produceBill(billingCycle, leasesIterator.next(), false));
                appendStats(dynamicStatisticsRecord, result);
                Persistence.service().commit();
            }
        } catch (Throwable e) {
            Persistence.service().rollback();
            log.error("Bill run error", e);
            appendStats(dynamicStatisticsRecord, new BillCreationResult(i18n.tr("Bill run error")));
            Persistence.service().commit();
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

    private static Bill produceBill(BillingCycle billingCycle, Lease lease, boolean preview) {
        BillProducer producer = new BillProducer(billingCycle, lease, preview);
        return producer.produceBill();
    }

    static Bill confirmBill(Bill billStub) {
        return verifyBill(billStub, BillStatus.Confirmed);
    }

    static Bill rejectBill(Bill billStub, String reason) {
        Bill bill = verifyBill(billStub, BillStatus.Rejected);
        bill.rejectReason().setValue(reason);
        Persistence.service().persist(bill);
        return bill;
    }

    private static Bill verifyBill(Bill billStub, BillStatus billStatus) {
        Bill bill = Persistence.service().retrieve(Bill.class, billStub.getPrimaryKey());
        if (BillStatus.Finished.equals(bill.billStatus().getValue())) {
            bill.billStatus().setValue(billStatus);

            if (BillStatus.Confirmed == billStatus) {
                Persistence.service().retrieve(bill.lineItems());
                claimExistingLineItems(bill.lineItems());
                postNewLineItems(bill.lineItems());
                ServerSideFactory.create(DepositFacade.class).onConfirmedBill(bill);
            }

            Persistence.service().persist(bill);

            updateBillingCycleStats(bill, false);

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

    static BillingCycle getNextBillingCycle(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, lease.billingAccount().getPrimaryKey());
        Persistence.service().retrieve(lease.unit());

        if (previousBill == null) {
            if (billingAccount.carryforwardBalance().isNull()) {
                return getNewLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().building(), lease.currentTerm().termFrom().getValue());

            } else {
                return getExistingLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().building(), lease.currentTerm().termFrom().getValue(),
                        lease.creationDate().getValue());
            }
        } else {
            return getSubsiquentBillingCycle(previousBill.billingCycle());
        }
    }

    static Bill getBill(Lease lease, int billSequenceNumber) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.eq(criteria.proto().billSequenceNumber(), billSequenceNumber));
        return Persistence.service().retrieve(criteria);
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

    static BillingType ensureBillingType(final PaymentFrequency paymentFrequency, final Integer billingCycleStartDay) {
        return TaskRunner.runAutonomousTransation(new Callable<BillingType>() {
            @Override
            public BillingType call() {

                //try to find existing billing cycle    
                EntityQueryCriteria<BillingType> criteria = EntityQueryCriteria.create(BillingType.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().paymentFrequency(), paymentFrequency));
                criteria.add(PropertyCriterion.eq(criteria.proto().billingCycleStartDay(), billingCycleStartDay));
                BillingType billingType = Persistence.service().retrieve(criteria);

                if (billingType == null) {
                    billingType = EntityFactory.create(BillingType.class);
                    billingType.paymentFrequency().setValue(paymentFrequency);
                    billingType.billingCycleStartDay().setValue(billingCycleStartDay);
                    billingType.billingCycleTargetDay().setValue(
                            (billingCycleStartDay + paymentFrequency.getNumOfCycles() - paymentFrequency.getBillRunTargetDayOffset())
                                    % paymentFrequency.getNumOfCycles());

                    Persistence.service().persist(billingType);
                    Persistence.service().commit();
                }
                return billingType;
            }
        });

    }

    static BillingType ensureBillingType(final Lease lease) {
        BillingType billingType = lease.billingAccount().billingType();

        if (!billingType.isNull()) {
            return billingType;
        } else {
            PaymentFrequency paymentFrequency = lease.paymentFrequency().getValue();
            LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(),
                    LeaseBillingPolicy.class);
            Integer billingCycleStartDay = leaseBillingPolicy.defaultBillingCycleSartDay().getValue();
            if (billingCycleStartDay == null) {
                billingCycleStartDay = BillDateUtils.calculateBillingTypeStartDay(lease.paymentFrequency().getValue(), lease.currentTerm().termFrom()
                        .getValue());
            }
            return ensureBillingType(paymentFrequency, billingCycleStartDay);
        }
    }

    static BillingCycle getNewLeaseInitialBillingCycle(BillingType billingType, Building building, LogicalDate leaseStartDate) {
        return ensureBillingCycle(billingType, building, BillDateUtils.calculateInitialBillingCycleStartDate(billingType, leaseStartDate));
    }

    static BillingCycle getExistingLeaseInitialBillingCycle(BillingType billingType, Building building, LogicalDate leaseStartDate,
            LogicalDate leaseCreationDate) {
        if (!leaseStartDate.before(leaseCreationDate)) {
            throw new BillingException("Existing lease start date should be earlier than creation date");
        }
        LogicalDate firstBillingCycleStartDate = BillDateUtils.calculateInitialBillingCycleStartDate(billingType, leaseStartDate);
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

    private static BillingCycle ensureBillingCycle(final BillingType billingType, final Building building, final LogicalDate billingCycleStartDate) {

        // Avoid transaction isolation problems in HDQLDB, retrieve "stats" in different transaction.
        // The @Detached(level = AttachLevel.Detached) on billingCycle.stats() would be a better option

        BillingCycle billingCycle = TaskRunner.runAutonomousTransation(new Callable<BillingCycle>() {
            @Override
            public BillingCycle call() {
                EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
                criteria.add(PropertyCriterion.eq(criteria.proto().billingCycleStartDate(), billingCycleStartDate));
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                List<Key> keys = Persistence.service().queryKeys(criteria);
                if (keys.size() != 0) {
                    return EntityFactory.createIdentityStub(BillingCycle.class, keys.get(0));
                } else {
                    BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
                    billingCycle.billingType().set(billingType);
                    billingCycle.building().set(building);
                    billingCycle.billingCycleStartDate().setValue(billingCycleStartDate);
                    billingCycle.billingCycleEndDate().setValue(
                            BillDateUtils.calculateBillingCycleEndDate(billingType.paymentFrequency().getValue(), billingCycleStartDate));
                    billingCycle.executionTargetDate().setValue(BillDateUtils.calculateBillingCycleTargetExecutionDate(billingType, billingCycleStartDate));

// Can't initialize this table here! HDQLDB will lock. TODO fix HDQLDB                    
//                    billingCycle.stats().notConfirmed().setValue(0L);
//                    billingCycle.stats().failed().setValue(0L);
//                    billingCycle.stats().rejected().setValue(0L);
//                    billingCycle.stats().confirmed().setValue(0L);

                    Persistence.service().persist(billingCycle);
                    Persistence.service().commit();
                    return billingCycle;
                }

            }
        });

        //? If @Detached(level = AttachLevel.Detached) on billingCycle.stats()
        //Persistence.service().retrieveMember(billingCycle.stats(), AttachLevel.Attached);

        if (billingCycle.isValueDetached()) {
            return Persistence.service().retrieve(BillingCycle.class, billingCycle.getPrimaryKey());
        } else {
            return billingCycle;
        }
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

    static void updateBillingCycleStats(Bill bill, boolean newlyCreated) {

        switch (bill.billStatus().getValue()) {
        case Failed:
            BillingUtils.increment(bill.billingCycle().stats().failed());
            break;
        case Finished:
            BillingUtils.increment(bill.billingCycle().stats().notConfirmed());
            break;
        case Confirmed:
            BillingUtils.increment(bill.billingCycle().stats().confirmed());
            BillingUtils.decrement(bill.billingCycle().stats().notConfirmed());
            break;
        case Rejected:
            BillingUtils.increment(bill.billingCycle().stats().rejected());
            BillingUtils.decrement(bill.billingCycle().stats().notConfirmed());
            break;
        default:
            throw new Error("Unexpected billStatus");

        }

        if (newlyCreated && bill.billSequenceNumber().getValue() > 1) {
            EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), bill.billingAccount()));
            criteria.add(PropertyCriterion.eq(criteria.proto().billSequenceNumber(), bill.billSequenceNumber().getValue() - 1));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingCycle(), bill.billingCycle()));
            Bill previousBill = Persistence.service().retrieve(criteria);

            if (previousBill != null) {

                //Define previous bill as not the latest
                previousBill.latestBillInCycle().setValue(false);
                Persistence.service().persist(previousBill);

                switch (previousBill.billStatus().getValue()) {
                case Failed:
                    BillingUtils.decrement(bill.billingCycle().stats().failed());
                    break;
                case Rejected:
                    BillingUtils.decrement(bill.billingCycle().stats().rejected());
                    break;

                default:
                    throw new Error("Unexpected billStatus");
                }
            }
        }

        Persistence.service().persist(bill.billingCycle().stats());

    }

    public static void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
        if (adjustment.overwriteDefaultTax().isBooleanTrue()) {
            return; // do nothing - use stored tax value 
        }

        Persistence.service().retrieve(adjustment.billingAccount());
        Persistence.service().retrieve(adjustment.billingAccount().lease());
        Persistence.service().retrieve(adjustment.billingAccount().lease().unit());
        LeaseAdjustmentPolicy result = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                adjustment.billingAccount().lease().unit().building(), LeaseAdjustmentPolicy.class);

        // TODO: currently calculate current policed tax value,  
        // in the future (when versioned policy will be implemented) - calculate tax effective on adjustment.targetDate().

        BigDecimal taxRate = BigDecimal.ZERO;
        for (LeaseAdjustmentPolicyItem item : result.policyItems()) {
            if (item.leaseAdjustmentReason().equals(adjustment.reason())) {
                for (Tax tax : item.taxes()) {
                    taxRate = taxRate.add(tax.rate().getValue());
                }
                break;
            }
        }

        adjustment.tax().setValue(taxRate);
    }
}
