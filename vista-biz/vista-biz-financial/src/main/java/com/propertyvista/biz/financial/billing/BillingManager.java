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
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

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
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.portal.rpc.shared.BillingException;
import com.propertyvista.server.jobs.StatisticsUtils;
import com.propertyvista.server.jobs.TaskRunner;

public class BillingManager {

    private static final I18n i18n = I18n.get(BillingManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingManager.class);

    static Bill runBilling(Lease leaseId, boolean preview) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey().asCurrentKey());
        if (lease.version().isNull()) {
            if (!preview) {
                throw new BillingException(i18n.tr("Lease is in draft state. Billing can run only in preview mode."));
            }
            lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey().asDraftKey());
        }

        lease = ensureInitBillingAccount(lease);
        BillingCycle billingCycle = getNextBillingCycle(lease);
        validateBillingRunPreconditions(billingCycle, lease);
        return produceBill(billingCycle, lease, preview);
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

    static void runBilling(final BillingCycle billingCycle, StatisticsRecord dynamicStatisticsRecord) {
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit().building(), billingCycle.building()));
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().billingAccount().billingType(), billingCycle.billingType()));
        ICursorIterator<Lease> leaseIterator = Persistence.service().query(null, leaseCriteria, AttachLevel.Attached);
        FilterIterator<Lease> filteredLeaseIterator = new FilterIterator<Lease>(leaseIterator, new Filter<Lease>() {
            @Override
            public boolean accept(Lease lease) {
                try {
                    validateBillingRunPreconditions(billingCycle, lease);
                    return true;
                } catch (BillingException e) {
                    return false;
                }
            }
        });
        runBilling(billingCycle, filteredLeaseIterator, dynamicStatisticsRecord);
    }

    private static void validateBillingRunPreconditions(BillingCycle billingCycle, Lease lease) {

        if (!getNextBillingCycle(lease).equals(billingCycle)) {
            throw new BillingException(i18n.tr("Bill can't be created for a given billing cycle"));
        }

        if (lease.version().status().getValue() == Lease.Status.Closed) {
            throw new BillingException("Lease is closed");
        }

        Bill previousBill = getLatestBill(lease);
        if (previousBill != null) {
            if (BillStatus.notConfirmed(previousBill.billStatus().getValue())) {
                throw new BillingException(i18n.tr("Can't run billing on Account with non-confirmed bills"));
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
                return getNewLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().building(), lease.leaseFrom().getValue(), !billingAccount
                        .billingType().billingCycleStartDay().isNull());

            } else {
                return getExistingLeaseInitialBillingCycle(billingAccount.billingType(), lease.unit().building(), lease.leaseFrom().getValue(), lease
                        .creationDate().getValue(), !billingAccount.billingType().billingCycleStartDay().isNull());
            }
        } else {
            return getSubsiquentBillingCycle(previousBill.billingCycle());
        }
    }

    /**
     * Set appropriate billingType if needed
     */
    static Lease ensureInitBillingAccount(Lease lease) {
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
            Persistence.service().commit();
            lease.billingAccount().set(billingAccount);
        }
        return lease;
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

            LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(),
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

    private static BillingCycle ensureBillingCycle(final BillingType billingType, final Building building, final LogicalDate billingCycleStartDate) {
        return TaskRunner.runAutonomousTransation(new Callable<BillingCycle>() {
            @Override
            public BillingCycle call() {
                BillingCycle billingCycle = ensureBillingCycleTransaction(billingType, building, billingCycleStartDate);
                Persistence.service().commit();
                return billingCycle;
            }
        });
    }

    private synchronized static BillingCycle ensureBillingCycleTransaction(BillingType billingType, Building building, LogicalDate billingCycleStartDate) {

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

            billingCycle.notConfirmed().setValue(0L);
            billingCycle.failed().setValue(0L);
            billingCycle.rejected().setValue(0L);
            billingCycle.confirmed().setValue(0L);

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

    static void updateBillingCycleStats(Bill bill, boolean newlyCreated) {

        switch (bill.billStatus().getValue()) {
        case Failed:
            BillingUtils.increment(bill.billingCycle().failed());
            break;
        case Finished:
            BillingUtils.increment(bill.billingCycle().notConfirmed());
            break;
        case Confirmed:
            BillingUtils.increment(bill.billingCycle().confirmed());
            BillingUtils.decrement(bill.billingCycle().notConfirmed());
            break;
        case Rejected:
            BillingUtils.increment(bill.billingCycle().rejected());
            BillingUtils.decrement(bill.billingCycle().notConfirmed());
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
                    BillingUtils.decrement(bill.billingCycle().failed());
                    break;
                case Rejected:
                    BillingUtils.decrement(bill.billingCycle().rejected());
                    break;

                default:
                    throw new Error("Unexpected billStatus");
                }
            }
        }

        Persistence.service().persist(bill.billingCycle());

    }
}
