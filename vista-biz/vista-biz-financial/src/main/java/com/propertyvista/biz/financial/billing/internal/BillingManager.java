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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.portal.rpc.shared.BillingException;

public class BillingManager {

    private static final I18n i18n = I18n.get(BillingManager.class);

    private final static Logger log = LoggerFactory.getLogger(BillingManager.class);

    private BillingManager() {
    }

    private static class SingletonHolder {
        public static final BillingManager INSTANCE = new BillingManager();
    }

    //TODO shouldn't be public
    public static BillingManager instance() {
        return SingletonHolder.INSTANCE;
    }

    Bill runBilling(Lease leaseId, boolean preview) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asCurrentKey()));
        if (lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        }

        BillingCycle billingCycle = getNextBillBillingCycle(lease);
        return runBilling(lease, billingCycle, preview);
    }

    Bill runBilling(Lease lease, BillingCycle billingCycle, boolean preview) {
        if (validateBillingRunPreconditions(billingCycle, lease, preview)) {
            InternalBillProducer producer = new InternalBillProducer(billingCycle, lease, preview);
            return producer.produceBill();
        } else {
            throw new BillingException(i18n.tr("Bill Run Precondition Validation failed"));
        }
    }

    boolean validateBillingRunPreconditions(BillingCycle billingCycle, Lease lease, boolean preview) {

        BillingCycle nextCycle = getNextBillBillingCycle(lease);
        if (!billingCycle.equals(nextCycle)) {
            log.warn(i18n.tr("Invalid billing cycle: {0}; expected: {1}; lease end: {2}", billingCycle.billingCycleStartDate().getValue(), nextCycle
                    .billingCycleStartDate().getValue(), lease.leaseTo().getValue()));
            return false;
        }

        if (VersionedEntityUtils.isDraft(lease.currentTerm()) && !preview) {
            log.warn(i18n.tr("Lease Term is in draft state. Billing can run only in preview mode."));
            return false;
        }

        if (lease.status().getValue() == Lease.Status.Closed) {
            log.warn(i18n.tr("Lease is closed"));
            return false;
        }

        Bill previousBill = getLatestBill(lease);
        if (previousBill != null) {
            if (BillStatus.notConfirmed(previousBill.billStatus().getValue())) {
                log.warn(i18n.tr("Can't run billing on Account with non-confirmed bills"));
                return false;
            }
        }

        if (lease.status().getValue() == Lease.Status.Completed && previousBill.billType().getValue().equals(BillType.Final)) {
            log.warn(i18n.tr("Final bill has been already issued"));
            return false;
        }

        Bill previousConfirmedBill = getLatestConfirmedBill(lease);
        if (previousConfirmedBill != null) {
            Persistence.service().retrieve(previousConfirmedBill.billingAccount());

            //check if previous confirmed Bill is the last cycle bill and only final bill should run after
            boolean isPreviousConfirmedBillTheLast = previousConfirmedBill.billingPeriodEndDate().getValue().compareTo(lease.leaseTo().getValue()) >= 0;

            //previous bill wasn't the last one so we are dealing here with the regular bill which can't run before executionTargetDate
            if (!isPreviousConfirmedBillTheLast && SystemDateManager.getDate().compareTo(billingCycle.targetBillExecutionDate().getValue()) < 0) {
                log.warn(i18n.tr("Regular billing can't run before target execution date"));
                return false;
            }

            //previous bill was the last one so we have to run a final bill but not before lease end date or lease move-out date whatever is first
            if (isPreviousConfirmedBillTheLast && (SystemDateManager.getDate().compareTo(lease.leaseTo().getValue()) < 0)
                    && (lease.expectedMoveOut().isNull() || (SystemDateManager.getDate().compareTo(lease.expectedMoveOut().getValue()) < 0))) {
                log.warn(i18n.tr("Final billing can't run before both lease end date and move-out date"));
                return false;
            }
        }

        return true;
    }

    Bill confirmBill(Bill billStub) {
        return verifyBill(billStub, BillStatus.Confirmed);
    }

    Bill rejectBill(Bill billStub, String reason) {
        Bill bill = verifyBill(billStub, BillStatus.Rejected);
        bill.rejectReason().setValue(reason);
        Persistence.service().persist(bill);
        return bill;
    }

    private Bill verifyBill(Bill billStub, BillStatus billStatus) {
        Bill bill = Persistence.service().retrieve(Bill.class, billStub.getPrimaryKey());
        if (BillStatus.Finished.equals(bill.billStatus().getValue())) {
            setBillStatus(bill, billStatus, false);

            if (BillStatus.Confirmed == billStatus) {
                Persistence.service().retrieve(bill.lineItems());
                postNewLineItems(bill);
                ServerSideFactory.create(DepositFacade.class).onConfirmedBill(bill);
            }

            Persistence.service().persist(bill);
        } else {
            throw new BillingException("Bill is in status '" + bill.billStatus().getValue() + "'. Bill should be in 'Finished' state in order to verify it.");
        }
        return bill;
    }

    protected BillingCycle getNextBillBillingCycle(Lease lease) {
        Bill previousBill = getLatestConfirmedBill(lease);
        if (previousBill == null) {
            return ServerSideFactory.create(BillingCycleFacade.class).getLeaseFirstBillingCycle(lease);
        } else {
            return ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(previousBill.billingCycle());
        }
    }

    private void postNewLineItems(Bill bill) {
        Persistence.ensureRetrieve(bill.lineItems(), AttachLevel.Attached);
        for (InvoiceLineItem invoiceLineItem : bill.lineItems()) {
            if (invoiceLineItem.postDate().isNull() && invoiceLineItem.amount().getValue().compareTo(BigDecimal.ZERO) != 0) {
                ServerSideFactory.create(ARFacade.class).postInvoiceLineItem(invoiceLineItem, bill.billingCycle());
            }
        }
    }

    Bill getBill(Lease lease, int billSequenceNumber) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.eq(criteria.proto().billSequenceNumber(), billSequenceNumber));
        return Persistence.service().retrieve(criteria);
    }

    Bill getLatestConfirmedBill(Lease lease) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.eq(criteria.proto().billStatus(), BillStatus.Confirmed));
        criteria.desc(criteria.proto().billSequenceNumber());
        return Persistence.service().retrieve(criteria);
    }

    Bill getLatestBill(Lease lease) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.desc(criteria.proto().billSequenceNumber());
        return Persistence.service().retrieve(criteria);
    }

    boolean isLatestBill(Bill bill) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), bill.billingAccount()));
        criteria.add(PropertyCriterion.gt(criteria.proto().billSequenceNumber(), bill.billSequenceNumber()));
        return !Persistence.service().exists(criteria);
    }

    public void setBillStatus(Bill bill, BillStatus status, boolean newlyCreated) {
        if (bill.billingCycle().isEmpty()) {
            throw new Error("BillingCycle must have been set at this point.");
        }

        bill.billStatus().setValue(status);
        updateBillingCycleStats(bill, newlyCreated);
    }

    void updateBillingCycleStats(Bill bill, boolean newlyCreated) {

        if (bill.billingCycle().stats().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieveMember(bill.billingCycle().stats());
        }

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
            return;

        }

        if (newlyCreated && bill.billSequenceNumber().getValue() > 1) {
            EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), bill.billingAccount()));
            criteria.add(PropertyCriterion.eq(criteria.proto().billSequenceNumber(), bill.billSequenceNumber().getValue() - 1));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingCycle(), bill.billingCycle()));
            Bill previousBill = Persistence.service().retrieve(criteria);

            if (previousBill != null) {

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

        // persist stats for non-preview bills
        if (bill.billSequenceNumber().getValue() > 0) {
            Persistence.service().persist(bill.billingCycle().stats());
        }

    }

    public void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
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
            if (item.code().equals(adjustment.code())) {
                for (Tax tax : item.taxes()) {
                    taxRate = taxRate.add(tax.rate().getValue());
                }
                break;
            }
        }

        adjustment.tax().setValue(taxRate);
    }
}
