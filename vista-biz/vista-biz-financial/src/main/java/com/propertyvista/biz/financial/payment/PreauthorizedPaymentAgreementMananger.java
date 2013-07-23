/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.common.security.VistaContext;

class PreauthorizedPaymentAgreementMananger {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PreauthorizedPaymentAgreementMananger.class);

    PreauthorizedPayment persistPreauthorizedPayment(PreauthorizedPayment preauthorizedPayment, Tenant tenantId) {
        preauthorizedPayment.tenant().set(tenantId);
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);

        LogicalDate nextPaymentDate = ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(
                preauthorizedPayment.tenant().lease());

        // Creates a new version of PAP if values changed and there are payments created
        if (!preauthorizedPayment.id().isNull()) {
            PreauthorizedPayment origPreauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPayment.getPrimaryKey());

            if (!EntityGraph.fullyEqual(origPreauthorizedPayment, preauthorizedPayment)) {
                // If tenant modifies PAP after cut off date - original will be used in this cycle and a new one in next cycle.
                LogicalDate cutOffDate = ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(
                        preauthorizedPayment.tenant().lease());

                boolean cutOffAppy = !origPreauthorizedPayment.effectiveFrom().isNull()
                        && origPreauthorizedPayment.effectiveFrom().getValue().before(nextPaymentDate);

                if (cutOffAppy && SystemDateManager.getDate().after(cutOffDate)) {
                    origPreauthorizedPayment.expiring().setValue(cutOffDate);
                    Persistence.service().merge(origPreauthorizedPayment);

                    preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                } else {
                    boolean hasPaymentRecords = false;
                    {
                        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
                        criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment);
                        hasPaymentRecords = Persistence.service().count(criteria) > 0;
                    }
                    if (hasPaymentRecords) {
                        origPreauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
                        Persistence.service().merge(origPreauthorizedPayment);

                        preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                    }
                }
                preauthorizedPayment.effectiveFrom().setValue(nextPaymentDate);

            }
        } else {
            preauthorizedPayment.effectiveFrom().setValue(nextPaymentDate);
            preauthorizedPayment.creator().set(VistaContext.getCurrentUserIfAvalable());
        }

        Persistence.service().merge(preauthorizedPayment);
        return preauthorizedPayment;
    }

    //If Tenant removes PAP - payment will NOT be canceled.
    void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        preauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(preauthorizedPayment);
    }

    List<PreauthorizedPayment> retrievePreauthorizedPayments(Tenant tenantId) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().query(criteria);
    }

    List<PreauthorizedPayment> retrievePreauthorizedPayments(Lease leaseId) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().tenant().lease(), leaseId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().query(criteria);
    }

    void deletePreauthorizedPayments(LeasePaymentMethod paymentMethod) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);

        for (PreauthorizedPayment preauthorizedPayment : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            deletePreauthorizedPayment(preauthorizedPayment);
            new ScheduledPaymentsManager().cancelScheduledPayments(preauthorizedPayment);
        }

    }

    void suspendPreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        Persistence.service().retrieve(preauthorizedPayment.tenant());
        LogicalDate cutOffDate = ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(preauthorizedPayment.tenant().lease());
        preauthorizedPayment.expiring().setValue(DateUtils.daysAdd(cutOffDate, -1));
        Persistence.service().merge(preauthorizedPayment);

        ServerSideFactory.create(NotificationFacade.class).papSuspension(preauthorizedPayment.tenant().lease());
    }

    public void renewPreauthorizedPayments(Lease lease) {
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentBillingCycle(lease);
        List<PreauthorizedPayment> activePap;
        {
            EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            {
                OrCriterion or = criteria.or();
                or.right().ge(criteria.proto().expiring(), nextCycle.targetPadGenerationDate());
                or.left().isNull(criteria.proto().expiring());
            }
            criteria.in(criteria.proto().tenant().lease(), lease);
            activePap = Persistence.service().query(criteria);
        }

        if (activePap.size() == 0) {
            return;
        }

        Map<String, BillableItem> billableItems = PaymentBillableUtils.getAllBillableItems(lease.currentTerm().version());

        // Verify that new charges not added
        LeaseTerm.LeaseTermV previousVersion = null;
        {
            // get previous version
            EntityQueryCriteria<LeaseTerm.LeaseTermV> criteria = EntityQueryCriteria.create(LeaseTerm.LeaseTermV.class);
            criteria.eq(criteria.proto().holder(), lease.currentTerm());
            criteria.eq(criteria.proto().versionNumber(), lease.currentTerm().version().versionNumber().getValue() - 1);
            previousVersion = Persistence.service().retrieve(criteria);
        }

        if (previousVersion == null) {
            // LeaseLifecycleSimulator or preload
            return;
        }

        boolean suspend = false;

        Map<String, BillableItem> previousBillableItems = PaymentBillableUtils.getAllBillableItems(previousVersion);

        if (!EqualsHelper.equals(previousBillableItems.keySet(), billableItems.keySet())) {
            suspend = true;
        } else {
            for (BillableItem previousBillableItem : previousBillableItems.values()) {
                BillableItem newBillableItem = billableItems.get(previousBillableItem.uid().getValue());
                if ((newBillableItem == null)
                        || (PaymentBillableUtils.getActualPrice(newBillableItem).compareTo(PaymentBillableUtils.getActualPrice(previousBillableItem)) != 0)) {
                    suspend = true;
                    break;
                }
            }
        }

        // lease last month check:
        suspend |= (before(nextCycle.billingCycleEndDate(), lease.expectedMoveOut()) || before(nextCycle.billingCycleEndDate(), lease.actualMoveOut()));

        if (!suspend) {
            // migrate each PAP to new billableItems
            forEachAllPap: for (PreauthorizedPayment pap : activePap) {
                for (PreauthorizedPaymentCoveredItem pi : pap.coveredItems()) {
                    BillableItem bi = billableItems.get(pi.billableItem().uid().getValue());
                    // Not found or price changed, should have been caught by previous comparison
                    if ((bi == null) || (PaymentBillableUtils.getActualPrice(bi).compareTo(PaymentBillableUtils.getActualPrice(pi.billableItem())) != 0)) {
                        suspend = true;
                        break forEachAllPap;
                    } else {
                        pi.billableItem().set(bi);
                    }
                }
            }
        }

        // Suspend all or update all
        for (PreauthorizedPayment pap : activePap) {
            if (suspend) {
                suspendPreauthorizedPayment(pap);
            } else {
                Persistence.service().merge(pap);
            }
        }
    }

    public void suspendPreauthorisedPaymentsInLastMonth(final ExecutionMonitor executionMonitor, LogicalDate forDate) {
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.lt(criteria.proto().billingCycleStartDate(), forDate);
        criteria.gt(criteria.proto().billingCycleEndDate(), forDate);

        ICursorIterator<BillingCycle> i = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (i.hasNext()) {
                BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(i.next());

                EntityQueryCriteria<BillingAccount> criteria1 = EntityQueryCriteria.create(BillingAccount.class);
                criteria1.eq(criteria1.proto().lease().unit().building(), nextCycle.building());
                criteria1.eq(criteria1.proto().billingType(), nextCycle.billingType());
                criteria1.isNotNull(criteria1.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                {
                    OrCriterion or = criteria1.or();
                    or.right().le(criteria1.proto().lease().expectedMoveOut(), nextCycle.billingCycleEndDate());
                    or.left().le(criteria1.proto().lease().actualMoveOut(), nextCycle.billingCycleEndDate());
                }

                for (final BillingAccount account : Persistence.service().query(criteria1)) {
                    try {
                        new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                            @Override
                            public Void execute() {
                                for (PreauthorizedPayment item : retrievePreauthorizedPayments(account.lease())) {
                                    suspendPreauthorizedPayment(item);
                                    executionMonitor.addProcessedEvent("Pad suspended");
                                }
                                return null;
                            }
                        });
                    } catch (Throwable error) {
                        executionMonitor.addFailedEvent("Pad suspension", error);
                    }

                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                }

                if (executionMonitor.isTerminationRequested()) {
                    break;
                }
            }
        } finally {
            i.close();
        }
    }

    private boolean before(IPrimitive<LogicalDate> one, IPrimitive<LogicalDate> two) {
        if (!one.isNull() && !two.isNull()) {
            return one.getValue().before(two.getValue());
        }
        return false;
    }
}
