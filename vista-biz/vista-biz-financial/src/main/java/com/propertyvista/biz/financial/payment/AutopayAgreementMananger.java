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
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.essentials.server.dev.EntityFileLogger;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapChargeDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.policy.policies.AutoPayPolicy.ChangeRule;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.shared.config.VistaFeatures;

class AutopayAgreementMananger {

    private static final Logger log = LoggerFactory.getLogger(AutopayAgreementMananger.class);

    private static final I18n i18n = I18n.get(AutopayAgreementMananger.class);

    AutopayAgreement persistAutopayAgreement(AutopayAgreement preauthorizedPayment, Tenant tenantId) {
        Validate.isTrue(!preauthorizedPayment.paymentMethod().isNull());
        Validate.isTrue(preauthorizedPayment.paymentMethod().type().getValue().isSchedulable());

        boolean ok = false;
        try {
            preauthorizedPayment.tenant().set(tenantId);
            Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);

            BillingCycle nextPaymentCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(
                    preauthorizedPayment.tenant().lease());

            AutopayAgreement origPreauthorizedPayment;
            AutopayAgreement orig = null;

            boolean isNewEntity = false;
            boolean isNewAgreement = false;
            // Creates a new version of PAP if values changed and there are payments created
            if (!preauthorizedPayment.id().isNull()) {
                origPreauthorizedPayment = Persistence.service().retrieve(AutopayAgreement.class, preauthorizedPayment.getPrimaryKey());
                orig = origPreauthorizedPayment.duplicate();
                if (!EntityGraph.fullyEqual(origPreauthorizedPayment, preauthorizedPayment)) {
                    boolean hasPaymentRecords = false;
                    {
                        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
                        criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment);
                        hasPaymentRecords = Persistence.service().count(criteria) > 0;
                    }
                    if (hasPaymentRecords) {
                        addComment(origPreauthorizedPayment, "Replaced by Updated Agreement");
                        origPreauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
                        origPreauthorizedPayment.expiredFrom().setValue(nextPaymentCycle.billingCycleStartDate().getValue());
                        Persistence.service().merge(origPreauthorizedPayment);

                        ServerSideFactory.create(AuditFacade.class).updated(origPreauthorizedPayment, EntityDiff.getChanges(orig, origPreauthorizedPayment));
                        isNewEntity = true;

                        preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                        preauthorizedPayment.reviewOfPap().set(origPreauthorizedPayment);
                        preauthorizedPayment.expiredFrom().setValue(null);
                    }
                    preauthorizedPayment.isDeleted().setValue(Boolean.FALSE);
                } else {
                    log.debug("no changes detected in AutopayAgreement");
                    ok = true;
                    return preauthorizedPayment;
                }
            } else {
                isNewEntity = true;
                isNewAgreement = true;
                preauthorizedPayment.isDeleted().setValue(Boolean.FALSE);
                preauthorizedPayment.createdBy().set(VistaContext.getCurrentUserIfAvalable());
            }

            if (preauthorizedPayment.effectiveFrom().isNull()
                    || preauthorizedPayment.effectiveFrom().getValue().before(nextPaymentCycle.billingCycleStartDate().getValue())) {
                preauthorizedPayment.effectiveFrom().setValue(nextPaymentCycle.billingCycleStartDate().getValue());
            }

            //Set Tenant intervention flag
            if (VistaContext.getCurrentUserIfAvalable() instanceof CustomerUser) {
                preauthorizedPayment.updatedByTenant().setValue(nextPaymentCycle.billingCycleStartDate().getValue());
            }
            if (!nextPaymentCycle.billingCycleStartDate().equals(preauthorizedPayment.updatedBySystem())) {
                // reset review flag for old updates
                preauthorizedPayment.updatedBySystem().setValue(null);
            }

            Persistence.service().merge(preauthorizedPayment);

            if (isNewEntity) {
                ServerSideFactory.create(AuditFacade.class).created(preauthorizedPayment);
            } else {
                ServerSideFactory.create(AuditFacade.class).updated(preauthorizedPayment, EntityDiff.getChanges(orig, preauthorizedPayment));
            }

            if (isNewAgreement) {
                ServerSideFactory.create(NotificationFacade.class).autoPaySetupCompleted(preauthorizedPayment);
            } else if (isAmountsChanged(orig, preauthorizedPayment)) {
                ServerSideFactory.create(NotificationFacade.class).autoPayChanges(preauthorizedPayment);
            }
            ok = true;
            return preauthorizedPayment;
        } finally {
            if (!ok) {
                EntityFileLogger.log("unexpected-errors", "autopayAgreement-persist", preauthorizedPayment);
            }
        }
    }

    private void addComment(AutopayAgreement preauthorizedPayment, String message) {
        preauthorizedPayment.comments().setValue(CommonsStringUtils.nvl_concat(preauthorizedPayment.comments().getValue(), message, "\n"));
    }

    private static class AllAmounts {

        Set<BigDecimal> amounts = new HashSet<>();

        BigDecimal total = BigDecimal.ZERO;

        void add(BigDecimal amount) {
            total = total.add(amount);
            amounts.add(amount.setScale(2));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AllAmounts) {
                AllAmounts other = (AllAmounts) obj;
                return (total.compareTo(other.total) == 0) && EqualsHelper.equals(amounts, other.amounts);
            } else {
                return false;
            }
        }
    }

    private boolean isAmountsChanged(AutopayAgreement orig, AutopayAgreement preauthorizedPayment) {
        AllAmounts origAmounts = new AllAmounts();
        AllAmounts origPrice = new AllAmounts();
        for (AutopayAgreementCoveredItem item : orig.coveredItems()) {
            origAmounts.add(item.amount().getValue());
            origPrice.add(ServerSideFactory.create(BillingFacade.class).getActualPrice(item.billableItem()));
        }

        AllAmounts newAmounts = new AllAmounts();
        AllAmounts newPrice = new AllAmounts();
        for (AutopayAgreementCoveredItem item : preauthorizedPayment.coveredItems()) {
            newAmounts.add(item.amount().getValue());
            newPrice.add(ServerSideFactory.create(BillingFacade.class).getActualPrice(item.billableItem()));
        }
        return !origAmounts.equals(newAmounts);
    }

    void persitAutopayAgreementReview(ReviewedAutopayAgreementDTO preauthorizedPaymentChanges) {
        AutopayAgreement preauthorizedPayment = Persistence.service().retrieve(AutopayAgreement.class, preauthorizedPaymentChanges.papId().getPrimaryKey());
        preauthorizedPayment.updatedBySystem().setValue(null);
        // Update amounts
        nextCharge: for (ReviewedPapChargeDTO reviewedPapCharge : preauthorizedPaymentChanges.reviewedCharges()) {
            for (AutopayAgreementCoveredItem coveredItem : preauthorizedPayment.coveredItems()) {
                if (reviewedPapCharge.billableItem().equals(coveredItem.billableItem())) {
                    coveredItem.amount().setValue(reviewedPapCharge.paymentAmountUpdate().getValue());
                    continue nextCharge;
                }
            }
            log.debug("Error with ReviewedAutopay  {}", DataDump.xmlStringView(preauthorizedPaymentChanges));
            log.debug("Error with AutopayAgreement {}", DataDump.xmlStringView(preauthorizedPayment));
            throw new Error("BillableItem item " + reviewedPapCharge.paymentAmountUpdate().getValue() + "$ "
                    + reviewedPapCharge.billableItem().id().getStringView() + " not found in AutopayAgreement " + preauthorizedPayment.id().getStringView());
        }
        persistAutopayAgreement(preauthorizedPayment, preauthorizedPayment.tenant());
    }

    List<AutopayAgreement> retrieveAutopayAgreements(Tenant tenantId) {
        EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().query(criteria);
    }

    List<AutopayAgreement> retrieveAutopayAgreements(Lease lease) {
        EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        criteria.in(criteria.proto().tenant().lease(), lease);
        return Persistence.service().query(criteria);
    }

    boolean isAutopayAgreementsPresent(Lease leaseId) {
        EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        criteria.in(criteria.proto().tenant().lease(), leaseId);
        return Persistence.service().exists(criteria);
    }

    //If Tenant removes PAP - payment will NOT be canceled.
    void deleteAutopayAgreement(AutopayAgreement preauthorizedPaymentId, boolean sendNotification, String comments) {
        AutopayAgreement preauthorizedPayment = Persistence.service().retrieve(AutopayAgreement.class, preauthorizedPaymentId.getPrimaryKey());
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(preauthorizedPayment.tenant().lease(), AttachLevel.Attached);

        if (VistaContext.getCurrentUserIfAvalable() instanceof CustomerUser) {
            AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(preauthorizedPayment.tenant().lease().unit(),
                    AutoPayPolicy.class);
            if (!autoPayPolicy.allowCancelationByResident().getValue(true)) {
                throw new UserRuntimeException(i18n.tr("AutoPay Agreement can not be cancelled online. Please contact your property management office."));
            } else {
                List<AutopayAgreement> canceledAgreements = new ArrayList<AutopayAgreement>();
                canceledAgreements.add(preauthorizedPayment);
                if (sendNotification) {
                    ServerSideFactory.create(NotificationFacade.class).autoPayCancelledByResidentNotification(preauthorizedPayment.tenant().lease(),
                            canceledAgreements);
                }
            }
        } else if (sendNotification) {
            ServerSideFactory.create(NotificationFacade.class).autoPayCancellation(preauthorizedPayment);
        }

        BillingCycle nextPaymentCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(preauthorizedPayment.tenant().lease());
        preauthorizedPayment.expiredFrom().setValue(nextPaymentCycle.billingCycleStartDate().getValue());

        addComment(preauthorizedPayment, comments);
        preauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(preauthorizedPayment);
        ServerSideFactory.create(AuditFacade.class).updated(preauthorizedPayment, "Deleted");
    }

    void deletePreauthorizedPayments(LeasePaymentMethod paymentMethod) {
        EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
        criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);

        for (AutopayAgreement preauthorizedPayment : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            deleteAutopayAgreement(preauthorizedPayment, true, "Payment Method removed");
            new ScheduledPaymentsManager().cancelScheduledPayments(preauthorizedPayment);
        }

    }

    public String getNextAutopayApplicabilityMessage(Lease lease) {
        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(lease), AutoPayPolicy.class);
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        StringBuilder trace = new StringBuilder();
        if (!isPreauthorizedPaymentsApplicableForBillingCycle(lease, nextCycle, autoPayPolicy, trace)) {
            return i18n.tr("Auto Pay will not be applicable for next BillingCycle; {0}", trace);
        } else {
            return null;
        }
    }

    public void renewPreauthorizedPayments(Lease lease) {
        List<AutopayAgreement> activePaps = retrieveAutopayAgreements(lease);
        if (activePaps.isEmpty()) {
            // nothing to update.
            return;
        }

        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), AutoPayPolicy.class);
        AutoPayPolicy.ChangeRule changeRule = autoPayPolicy.onLeaseChargeChangeRule().getValue();
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        StringBuilder trace = new StringBuilder();
        if (!isPreauthorizedPaymentsApplicableForBillingCycle(lease, nextCycle, autoPayPolicy, trace)) {
            // Suspend All
            for (AutopayAgreement pap : activePaps) {
                deleteAutopayAgreement(pap, true, i18n.tr("Auto Pay will not be applicable for next BillingCycle; {0}", trace));
            }
            ServerSideFactory.create(NotificationFacade.class).autoPayCancelledBySystemNotification(lease, activePaps);
            return;
        }

        Map<String, BillableItem> billableItemsCurrent = PaymentBillableUtils.getAllBillableItems(lease.currentTerm().version());

        boolean reviewNotificatioRequired = false;

        // migrate each PAP to new billableItems
        for (AutopayAgreement pap : activePaps) {
            boolean reviewRequired = false;

            List<AutopayAgreementCoveredItem> newCoveredItems = new ArrayList<AutopayAgreementCoveredItem>();

            Map<String, BillableItem> papBillableItemsCurrent = new LinkedHashMap<String, BillableItem>(billableItemsCurrent);

            for (AutopayAgreementCoveredItem coveredItemOriginal : pap.coveredItems()) {
                BillableItem billableItemCurrent = papBillableItemsCurrent.get(coveredItemOriginal.billableItem().uuid().getValue());
                if ((billableItemCurrent == null) || (!PaymentBillableUtils.isBillableItemPapable(billableItemCurrent, nextCycle))) {
                    // Not found, item removed
                    reviewRequired = true;
                    log.debug("removed BillableItem {} {}, paid {}", coveredItemOriginal.billableItem().uuid(), coveredItemOriginal.billableItem(),
                            coveredItemOriginal.amount());
                } else {
                    papBillableItemsCurrent.remove(coveredItemOriginal.billableItem().uuid().getValue());

                    // Update the price if required
                    AutopayAgreementCoveredItem newCoveredItem = EntityFactory.create(AutopayAgreementCoveredItem.class);

                    BigDecimal priceOriginal = ServerSideFactory.create(BillingFacade.class).getActualPrice(coveredItemOriginal.billableItem());
                    BigDecimal priceCurrent = ServerSideFactory.create(BillingFacade.class).getActualPrice(billableItemCurrent);

                    if (priceOriginal.compareTo(priceCurrent) != 0) {
                        BigDecimal originalPaymentAmount = coveredItemOriginal.amount().getValue();
                        // Price change detected
                        if (originalPaymentAmount.compareTo(BigDecimal.ZERO) != 0) {
                            reviewRequired = true;
                        }
                        if (isChangeByTenant(pap, nextCycle)) {
                            // Tenant intervention -> amount not changed automatically
                            newCoveredItem.amount().setValue(originalPaymentAmount);
                        } else {
                            newCoveredItem.amount().setValue(calulateNewPaymentValue(originalPaymentAmount, priceOriginal, priceCurrent, changeRule));
                            log.debug("update BillableItem {} {}, pay {}, paid {}", billableItemCurrent.uuid(), billableItemCurrent, newCoveredItem.amount(),
                                    coveredItemOriginal.amount());
                        }
                    } else {
                        // Price not changed
                        newCoveredItem.amount().setValue(coveredItemOriginal.amount().getValue());
                    }

                    newCoveredItem.billableItem().set(billableItemCurrent);
                    newCoveredItems.add(newCoveredItem);
                }
            }
            // newly added items or not covered
            for (Map.Entry<String, BillableItem> bi : papBillableItemsCurrent.entrySet()) {
                BillableItem billableItemCurrent = bi.getValue();
                reviewRequired = true;
                AutopayAgreementCoveredItem newCoveredItem = EntityFactory.create(AutopayAgreementCoveredItem.class);
                newCoveredItem.amount().setValue(BigDecimal.ZERO);
                newCoveredItem.billableItem().set(billableItemCurrent);
                newCoveredItems.add(newCoveredItem);
                log.debug("new BillableItem {} {}, pay {}", billableItemCurrent.uuid(), billableItemCurrent, newCoveredItem.amount());
            }

            pap.coveredItems().clear();
            pap.coveredItems().addAll(newCoveredItems);
            if (reviewRequired) {
                pap.updatedBySystem().setValue(nextCycle.billingCycleStartDate().getValue());
                reviewNotificatioRequired = true;
            } else if (!nextCycle.billingCycleStartDate().equals(pap.updatedBySystem())) {
                // Keep review flag for multiple updates in the same month
                pap.updatedBySystem().setValue(null);
            }
            persistAutopayAgreement(pap, pap.tenant());
        }
        if (reviewNotificatioRequired) {
            ServerSideFactory.create(NotificationFacade.class).autoPayReviewRequiredNotification(lease);
        }
    }

    static boolean isChangeByTenant(AutopayAgreement pap, BillingCycle nextCycle) {
        if ((pap.updatedByTenant().isNull()) || (pap.updatedByTenant().getValue().before(nextCycle.billingCycleStartDate().getValue()))) {
            return false;
        } else {
            return true;
        }
    }

    private BigDecimal calulateNewPaymentValue(BigDecimal originalPaymentAmount, BigDecimal priceOriginal, BigDecimal priceCurrent, ChangeRule changeRule) {
        switch (changeRule) {
        case keepUnchanged:
            return originalPaymentAmount;
        case keepPercentage:
            if (originalPaymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else if (priceOriginal.compareTo(BigDecimal.ZERO) == 0) {
                // 100% for payments that was base on 0$ charges
                return priceCurrent;
            } else {
                return DomainUtil.roundMoney(originalPaymentAmount.multiply(priceCurrent).divide(priceOriginal, RoundingMode.HALF_UP));
            }
        default:
            throw new IllegalArgumentException();
        }
    }

    public List<BillableItem> getAutoPpayApplicabelItems(Lease lease, BillingCycle billingCycle) {
        List<BillableItem> billableItems = new ArrayList<>();
        LeaseTermV leaseTermV = lease.currentTerm().version();
        if (PaymentBillableUtils.isBillableItemPapable(leaseTermV.leaseProducts().serviceItem(), billingCycle)) {
            billableItems.add(leaseTermV.leaseProducts().serviceItem());
        }
        for (BillableItem bi : leaseTermV.leaseProducts().featureItems()) {
            if (PaymentBillableUtils.isBillableItemPapable(bi, billingCycle)) {
                billableItems.add(bi);
            }
        }
        return billableItems;
    }

    public void terminateAutopayAgreements(Lease lease) {
        List<AutopayAgreement> activePaps = retrieveAutopayAgreements(lease);
        if (activePaps.isEmpty()) {
            return; // nothing to do!..
        }

        boolean terminate = false;

        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), AutoPayPolicy.class);

        StringBuilder trace = new StringBuilder();
        if (!isPreauthorizedPaymentsApplicableForBillingCycle(lease, nextCycle, autoPayPolicy, trace)) {
            terminate = true;
        }

        if (terminate) {
            for (AutopayAgreement pap : activePaps) {
                deleteAutopayAgreement(pap, true, trace.toString());
            }
            ServerSideFactory.create(NotificationFacade.class).autoPayCancelledBySystemNotification(lease, activePaps);
        }
    }

    void deleteAutopayAgreements(Lease lease, boolean sendNotification) {
        List<AutopayAgreement> activePaps = retrieveAutopayAgreements(lease);
        if (!activePaps.isEmpty()) {
            for (AutopayAgreement pap : activePaps) {
                deleteAutopayAgreement(pap, sendNotification, "");
            }
            if (sendNotification) {
                ServerSideFactory.create(NotificationFacade.class).autoPayCancelledBySystemNotification(lease, activePaps);
            }
        }
    }

    public void deleteExpiringAutopayAgreement(final ExecutionMonitor executionMonitor, LogicalDate forDate) {
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.le(criteria.proto().billingCycleStartDate(), forDate);
        criteria.ge(criteria.proto().billingCycleEndDate(), forDate);

        ServerSideFactory.create(NotificationFacade.class).aggregateNotificationsStart();

        ICursorIterator<BillingCycle> billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (billingCycleIterator.hasNext()) {
                BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycleIterator.next());
                final BillingCycle suspensionCycle;
                if (!forDate.before(nextCycle.targetAutopayExecutionDate().getValue())) {
                    suspensionCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(nextCycle);
                } else {
                    suspensionCycle = nextCycle;
                }

                EntityQueryCriteria<BillingAccount> criteria1 = EntityQueryCriteria.create(BillingAccount.class);
                criteria1.eq(criteria1.proto().lease().unit().building(), suspensionCycle.building());
                criteria1.eq(criteria1.proto().billingType(), suspensionCycle.billingType());
                criteria1.isNotNull(criteria1.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
                {
                    // retrieve BillingAccounts which confirms :
                    //
                    // lease().expectedMoveOut()    <=      suspensionCycle.billingCycleEndDate();
                    // lease().actualMoveOut()      <=      suspensionCycle.billingCycleEndDate();
                    // lease().leaseTo()            <=      suspensionCycle.billingCycleEndDate();
                    //
                    // Note: do not synchronize it with set of leaseXXXCheck(...) methods!!!

                    OrCriterion or1 = new OrCriterion();
                    or1.left().le(criteria1.proto().lease().expectedMoveOut(), suspensionCycle.billingCycleEndDate());
                    or1.right().le(criteria1.proto().lease().actualMoveOut(), suspensionCycle.billingCycleEndDate());

                    OrCriterion or2 = new OrCriterion();
                    or2.left().le(criteria1.proto().lease().leaseTo(), suspensionCycle.billingCycleEndDate());
                    or2.right(or1);

                    OrCriterion or3 = new OrCriterion();
                    or3.left().in(criteria1.proto().lease().status(), Lease.Status.noAutoPay());
                    or3.right(or2);

                    criteria1.add(or3);

                }

                for (final BillingAccount account : Persistence.service().query(criteria1)) {
                    Persistence.ensureRetrieve(account.lease(), AttachLevel.Attached);
                    try {
                        new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                            @Override
                            public Void execute() {
                                List<AutopayAgreement> activePaps = retrieveAutopayAgreements(account.lease());
                                if (!activePaps.isEmpty()) {
                                    boolean atLeaseOneTerminated = false;
                                    AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                                            account.lease().unit().building(), AutoPayPolicy.class);

                                    boolean terminate = false;
                                    StringBuilder trace = new StringBuilder();
                                    if (!isPreauthorizedPaymentsApplicableForBillingCycle(account.lease(), suspensionCycle, autoPayPolicy, trace)) {
                                        terminate = true;
                                    }
                                    if (terminate) {
                                        for (AutopayAgreement item : activePaps) {
                                            if (terminate) {
                                                atLeaseOneTerminated = true;
                                                deleteAutopayAgreement(item, true, "Auto Pay will not not applicable for next BillingCycle " + trace);
                                                executionMonitor.addProcessedEvent("AutoPay Cancel");
                                            }
                                        }
                                        if (atLeaseOneTerminated) {
                                            ServerSideFactory.create(NotificationFacade.class)
                                                    .autoPayCancelledBySystemNotification(account.lease(), activePaps);
                                            Persistence.ensureRetrieve(account.lease(), AttachLevel.Attached);
                                            executionMonitor.addInfoEvent("Lease with Cancelled AutoPay", "LeaseId "
                                                    + account.lease().leaseId().getStringView());
                                        }
                                    }
                                }
                                return null;
                            }
                        });
                    } catch (Throwable error) {
                        executionMonitor.addErredEvent("AutoPay Cancel", error);
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
            billingCycleIterator.close();
            ServerSideFactory.create(NotificationFacade.class).aggregatedNotificationsSend();
        }
    }

    static boolean isPreauthorizedPaymentsApplicableForBillingCycle(Lease lease, BillingCycle paymentCycle, AutoPayPolicy autoPayPolicy) {
        return isPreauthorizedPaymentsApplicableForBillingCycle(lease, paymentCycle, autoPayPolicy, new StringBuilder());
    }

    static boolean isPreauthorizedPaymentsApplicableForBillingCycle(Lease lease, BillingCycle paymentCycle, AutoPayPolicy autoPayPolicy, StringBuilder trace) {
        if (lease.status().getValue().isNoAutoPay()) {
            trace.append("Lease status '" + lease.status().getValue() + "' do not allow Auto Pay");
            return false;
        }
        // TODO: lease first month check:
        if (leaseFirstBillingPeriodChargePolicyCheck(lease, paymentCycle, autoPayPolicy, trace)) {
            return false;
        } else
        // lease last month check:
        if (leaseLastBillingPeriodChargePolicyCheck(lease, paymentCycle, autoPayPolicy, trace)) {
            return false;
        } else
        // Lease end date check:
        if (leaseEndDateCheck(lease, paymentCycle, trace)) {
            return false;
        } else {
            return true;
        }
    }

    // lease leaseXXXCheck(...) methods:
    // Note: do not synchronize it with criteria1 in updatePreauthorizedPayments(ExecutionMonitor executionMonitor, LogicalDate forDate) !!!

    static private boolean leaseFirstBillingPeriodChargePolicyCheck(Lease lease, BillingCycle nextCycle, AutoPayPolicy autoPayPolicy, StringBuilder trace) {
        if (autoPayPolicy.excludeFirstBillingPeriodCharge().getValue(false)) {
            if (beforeOrEqual(nextCycle.billingCycleStartDate(), lease.leaseFrom())) {
                trace.append("Lease First Month detected; Lease From : '" + lease.leaseFrom().getStringView() + "'");
                return true;
            }

            if (beforeOrEqual(nextCycle.billingCycleStartDate(), lease.expectedMoveIn())) {
                trace.append("Lease First Month detected; Expected Move In : '" + lease.expectedMoveIn().getStringView() + "'");
                return true;
            }
        }
        return false;
    }

    static private boolean leaseLastBillingPeriodChargePolicyCheck(Lease lease, BillingCycle suspensionCycle, AutoPayPolicy autoPayPolicy, StringBuilder trace) {
        if (autoPayPolicy.excludeLastBillingPeriodCharge().getValue(false)) {
            if (beforeOrEqual(lease.expectedMoveOut(), suspensionCycle.billingCycleEndDate())) {
                trace.append("Lease Last Month detected; Expected Move Out : '" + lease.expectedMoveOut().getStringView() + "'");
                return true;
            }

            if (beforeOrEqual(lease.actualMoveOut(), suspensionCycle.billingCycleEndDate())) {
                trace.append("Lease Last Month detected; Actual Move Out : '" + lease.actualMoveOut().getStringView() + "'");
                return true;
            }
        }
        return false;
    }

    static private boolean leaseEndDateCheck(Lease lease, BillingCycle suspensionCycle, StringBuilder trace) {
        if (VistaFeatures.instance().yardiIntegration()) {
            if (before(lease.expectedMoveOut(), suspensionCycle.billingCycleStartDate())) {
                trace.append("Lease end detected; Expected Move Out : '" + lease.expectedMoveOut().getStringView() + "'");
                return true;
            }
            if (before(lease.actualMoveOut(), suspensionCycle.billingCycleStartDate())) {
                trace.append("Lease end detected; Actual Move Out : '" + lease.actualMoveOut().getStringView() + "'");
                return true;
            }

            return false;

        } else {
            // TODO : calculate/ensure (case of Fixed and Periodic lease types) real lease end date!?
            return (before(lease.leaseTo(), suspensionCycle.billingCycleStartDate()));
        }
    }

    static private boolean before(IPrimitive<LogicalDate> one, IPrimitive<LogicalDate> two) {
        if (!one.isNull() && !two.isNull()) {
            return one.getValue().before(two.getValue());
        }
        return false;
    }

    static private boolean beforeOrEqual(IPrimitive<LogicalDate> one, IPrimitive<LogicalDate> two) {
        if (!one.isNull() && !two.isNull()) {
            return !one.getValue().after(two.getValue());
        }
        return false;
    }

}
