/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewChargeDetailDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.shared.config.VistaFeatures;

class PreauthorizedPaymentAutoPayReviewReport {

    List<AutoPayReviewDTO> reportSuspendedPreauthorizedPayments(PreauthorizedPaymentsReportCriteria reportCriteria) {
        List<AutoPayReviewDTO> records = new ArrayList<AutoPayReviewDTO>();

        ICursorIterator<BillingAccount> billingAccountIterator;
        { //TODO->Closure
            EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
            if (reportCriteria.selectedBuildings != null) {
                criteria.in(criteria.proto().lease().unit().building(), reportCriteria.selectedBuildings);
            } else {
                criteria.in(criteria.proto().lease().unit().building().suspended(), false);
            }
            criteria.eq(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), false);
            criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().expiring());

            if (reportCriteria.isLeasesOnNoticeOnly()) {
                criteria.eq(criteria.proto().lease().completion(), Lease.CompletionType.Notice);
            }

            if (reportCriteria.hasExpectedMoveOutFilter()) {
                if (reportCriteria.getMinExpectedMoveOut() != null) {
                    criteria.ge(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMinExpectedMoveOut());
                }
                if (reportCriteria.getMaxExpectedMoveOut() != null) {
                    criteria.le(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMaxExpectedMoveOut());
                }
            }

            criteria.asc(criteria.proto().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().lease().leaseId());

            billingAccountIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingAccountIterator.hasNext()) {
                AutoPayReviewDTO leaseRecord = createBillingAccountReview(billingAccountIterator.next());
                if (leaseRecord != null) {
                    records.add(leaseRecord);
                }
            }
        } finally {
            billingAccountIterator.close();
        }

        return records;
    }

    AutoPayReviewDTO getSuspendedPreauthorizedPaymentReview(BillingAccount billingAccountId) {
        return createBillingAccountReview(Persistence.service().retrieve(BillingAccount.class, billingAccountId.getPrimaryKey()));
    }

    private AutoPayReviewDTO createBillingAccountReview(BillingAccount billingAccount) {
        AutoPayReviewDTO review = EntityFactory.create(AutoPayReviewDTO.class);

        Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit().building(), AttachLevel.Attached);

        review.building().setValue(billingAccount.lease().unit().building().propertyCode().getValue());

        review.leaseId().setValue(billingAccount.lease().leaseId().getValue());
        review.lease().set(billingAccount.lease().duplicate());
        // Clear unused values
        review.lease().billingAccount().setValueDetached();
        review.lease().currentTerm().setValueDetached();
        review.lease().previousTerm().setValueDetached();
        review.lease().nextTerm().setValueDetached();
        review.lease().unit().setValueDetached();

        review.unit().setValue(billingAccount.lease().unit().info().number().getValue());

        BillingCycle nextPaymentCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentBillingCycle(billingAccount.lease());

        review.paymentDue().setValue(nextPaymentCycle.targetPadExecutionDate().getValue());

        AutoPayPolicy policy = ServerSideFactory.create(PolicyFacade.class)
                .obtainEffectivePolicy(billingAccount.lease().unit().building(), AutoPayPolicy.class);
        AutoPayPolicy.ChangeRule changeRule = policy.onLeaseChargeChangeRule().getValue();

        boolean renewPayments = new PreauthorizedPaymentAgreementMananger().isPreauthorizedPaymentsApplicableForBillingCycle(billingAccount.lease(),
                nextPaymentCycle, policy);

        List<PreauthorizedPayment> preauthorizedPayments = getPreauthorizedPayments(billingAccount);
        for (PreauthorizedPayment preauthorizedPayment : preauthorizedPayments) {
            review.pap()
                    .add(createPreauthorizedPaymentPreview(billingAccount, changeRule, renewPayments, review.paymentDue().getValue(), preauthorizedPayment));
        }

        calulateLeaseTotals(review);

        return review;
    }

    private void calulateLeaseTotals(AutoPayReviewDTO review) {
        // Count each item once in totals
        Set<BillableItem> countedSuspended = new HashSet<BillableItem>();
        Set<BillableItem> countedSuggested = new HashSet<BillableItem>();

        for (AutoPayReviewPreauthorizedPaymentDTO pap : review.pap()) {
            for (AutoPayReviewChargeDTO chargeReview : pap.items()) {
                if (!chargeReview.suspended().billableItem().isNull() && !countedSuspended.contains(chargeReview.suspended().billableItem())) {
                    countedSuspended.add(chargeReview.suspended().billableItem());
                    DomainUtil.nvlAddBigDecimal(review.totalSuspended().totalPrice(), chargeReview.suspended().totalPrice());
                }
                DomainUtil.nvlAddBigDecimal(review.totalSuspended().payment(), chargeReview.suspended().payment());

                if (!chargeReview.suggested().billableItem().isNull() && !countedSuggested.contains(chargeReview.suggested().billableItem())) {
                    countedSuggested.add(chargeReview.suggested().billableItem());
                    DomainUtil.nvlAddBigDecimal(review.totalSuggested().totalPrice(), chargeReview.suggested().totalPrice());
                }
                DomainUtil.nvlAddBigDecimal(review.totalSuggested().payment(), chargeReview.suggested().payment());
            }
        }

        if (!review.totalSuspended().totalPrice().isNull()) {
            calulatePercent(review.totalSuspended());
        }
        if (!review.totalSuggested().totalPrice().isNull()) {
            calulatePercent(review.totalSuggested());
        }
    }

    private PreauthorizedPaymentCoveredItem getCoveredItem(List<PreauthorizedPaymentCoveredItem> coveredItems, BillableItem billableItem) {
        for (PreauthorizedPaymentCoveredItem coveredItem : coveredItems) {
            if (coveredItem.billableItem().uid().equals(billableItem.uid())) {
                return coveredItem;
            }
        }
        return null;
    }

    private LeaseTermV findSuspentionLeaseTermV(LeaseTermV leaseTermV, IList<PreauthorizedPaymentCoveredItem> coveredItems) {
        LeaseTerm.LeaseTermV previousVersion = null;
        {
            // get previous version
            EntityQueryCriteria<LeaseTerm.LeaseTermV> criteria = EntityQueryCriteria.create(LeaseTerm.LeaseTermV.class);
            criteria.eq(criteria.proto().holder(), leaseTermV.holder());
            criteria.eq(criteria.proto().versionNumber(), leaseTermV.versionNumber().getValue() - 1);
            previousVersion = Persistence.service().retrieve(criteria);
        }
        if (previousVersion == null) {
            return null;
        } else {
            Map<String, BillableItem> billableItems = PaymentBillableUtils.getAllBillableItems(previousVersion);
            for (PreauthorizedPaymentCoveredItem coveredItem : coveredItems) {
                // Find items by ID,  not by UID
                if (billableItems.values().contains(coveredItem.billableItem())) {
                    return previousVersion;
                }
            }
            return findSuspentionLeaseTermV(previousVersion, coveredItems);
        }
    }

    private AutoPayReviewPreauthorizedPaymentDTO createPreauthorizedPaymentPreview(BillingAccount billingAccount, AutoPayPolicy.ChangeRule changeRule,
            boolean renewPayments, LogicalDate preauthorizedPaymentDate, PreauthorizedPayment preauthorizedPayment) {
        AutoPayReviewPreauthorizedPaymentDTO papReview = EntityFactory.create(AutoPayReviewPreauthorizedPaymentDTO.class);

        papReview.pap().set(preauthorizedPayment.createIdentityStub());
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);
        papReview.tenantName().setValue(preauthorizedPayment.tenant().customer().person().name().getStringView());

        // ordered by insertion-order. 
        Map<String, BillableItem> newBillableItems = PaymentBillableUtils.getAllBillableItems(billingAccount.lease().currentTerm().version());

        LeaseTerm.LeaseTermV previousVersion = findSuspentionLeaseTermV(billingAccount.lease().currentTerm().version(), preauthorizedPayment.coveredItems());
        Map<String, BillableItem> previousBillableItems;
        if (previousVersion != null) {
            previousBillableItems = PaymentBillableUtils.getAllBillableItems(previousVersion);
        } else {
            previousBillableItems = Collections.emptyMap();
        }

        for (Map.Entry<String, BillableItem> bi : newBillableItems.entrySet()) {
            BillableItem billableItem = bi.getValue();
            PreauthorizedPaymentCoveredItem coveredItem = getCoveredItem(preauthorizedPayment.coveredItems(), bi.getValue());
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);
            if (coveredItem == null) {
                // newly added items or not covered
                if (renewPayments && isBillableItemPapable(billableItem, preauthorizedPaymentDate)) {
                    BillableItem previousBillableItem = previousBillableItems.get(bi.getKey());
                    if (previousBillableItem != null) {
                        chargeReview.suspended().billableItem().set(previousBillableItem.createIdentityStub());
                        chargeReview.suspended().totalPrice().setValue(PaymentBillableUtils.getActualPrice(previousBillableItem));
                        chargeReview.suspended().payment().setValue(BigDecimal.ZERO);
                        calulatePercent(chargeReview.suspended());
                    }

                    chargeReview.suggested().billableItem().set(billableItem.createIdentityStub());
                    chargeReview.suggested().totalPrice().setValue(PaymentBillableUtils.getActualPrice(billableItem));

                    chargeReview.leaseCharge().setValue(getLeaseChargeDescription(billableItem));
                    papReview.items().add(chargeReview);
                }
            } else {
                chargeReview.suspended().billableItem().set(coveredItem.billableItem().createIdentityStub());
                chargeReview.suspended().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItem.billableItem()));
                chargeReview.suspended().payment().setValue(coveredItem.amount().getValue());
                calulatePercent(chargeReview.suspended());

                if (renewPayments && isBillableItemPapable(billableItem, preauthorizedPaymentDate)) {
                    chargeReview.suggested()
                            .set(calulateSuggestedChargeDetail(billingAccount, changeRule, preauthorizedPaymentDate, billableItem, coveredItem));
                }

                chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));

                papReview.items().add(chargeReview);
            }

            if (!chargeReview.suggested().totalPrice().isNull() && !chargeReview.suspended().totalPrice().isNull()) {
                if (chargeReview.suspended().totalPrice().getValue().compareTo(BigDecimal.ZERO) == 0) {
                    if (chargeReview.suggested().totalPrice().getValue().compareTo(BigDecimal.ZERO) == 0) {
                        chargeReview.suggested().percentChange().setValue(BigDecimal.ZERO);
                    } else {
                        chargeReview.suggested().percentChange().setValue(BigDecimal.ONE);
                    }
                } else {
                    chargeReview
                            .suggested()
                            .percentChange()
                            .setValue( //
                                    chargeReview.suggested().totalPrice().getValue()
                                            .divide(chargeReview.suspended().totalPrice().getValue(), 4, RoundingMode.FLOOR).subtract(BigDecimal.ONE));
                }
            }

        }

        // Removed billableItem are shown at the end, and they don't have suggestions
        for (PreauthorizedPaymentCoveredItem coveredItem : preauthorizedPayment.coveredItems()) {
            if (!newBillableItems.containsKey(coveredItem.billableItem().uid().getValue())) {

                AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

                chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));

                chargeReview.suspended().billableItem().set(coveredItem.billableItem().createIdentityStub());
                chargeReview.suspended().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItem.billableItem()));
                chargeReview.suspended().payment().setValue(coveredItem.amount().getValue());
                calulatePercent(chargeReview.suspended());

                papReview.items().add(chargeReview);
            }

        }

        return papReview;
    }

    private String getLeaseChargeDescription(BillableItem billableItem) {
        String description = "";
        if (VistaFeatures.instance().yardiIntegration()) {
            description = billableItem.extraData().getStringView() + " ";
        }
        description += billableItem.description().getStringView();
        if (description.length() == 0) {
            description += billableItem.item().code().getStringView();
        }
        return description;
    }

    private void calulatePercent(AutoPayReviewChargeDetailDTO chargeDetail) {
        if (chargeDetail.totalPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            if (!chargeDetail.payment().isNull()) {
                chargeDetail.percent().setValue(chargeDetail.payment().getValue().divide(chargeDetail.totalPrice().getValue(), 4, RoundingMode.FLOOR));
            }
        } else {
            chargeDetail.percent().setValue(BigDecimal.ZERO);
        }
    }

    boolean isBillableItemPapable(BillableItem billableItem, LogicalDate preauthorizedPaymentDate) {
        return (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(preauthorizedPaymentDate));
    }

    private AutoPayReviewChargeDetailDTO calulateSuggestedChargeDetail(BillingAccount billingAccount, AutoPayPolicy.ChangeRule changeRule,
            LogicalDate preauthorizedPaymentDate, BillableItem newBillableItem, PreauthorizedPaymentCoveredItem coveredItem) {
        AutoPayReviewChargeDetailDTO suggestedChargeDetail = EntityFactory.create(AutoPayReviewChargeDetailDTO.class);

        suggestedChargeDetail.billableItem().set(newBillableItem.createIdentityStub());
        suggestedChargeDetail.totalPrice().setValue(PaymentBillableUtils.getActualPrice(newBillableItem));

        suggestedChargeDetail.payment().setValue(
                calulateNewPaymentValue(coveredItem.amount().getValue(), PaymentBillableUtils.getActualPrice(coveredItem.billableItem()),
                        PaymentBillableUtils.getActualPrice(newBillableItem), changeRule));

        calulatePercent(suggestedChargeDetail);

        return suggestedChargeDetail;
    }

    private BigDecimal calulateNewPaymentValue(BigDecimal originalPaymentAmount, BigDecimal originalPrice, BigDecimal newPrice,
            AutoPayPolicy.ChangeRule changeRule) {
        switch (changeRule) {
        case keepUnchanged:
            return originalPaymentAmount;
        case keepPercentage:
            if (originalPaymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
                // 100% for payments that was base on 0$ charges
                return newPrice;
            } else {
                return DomainUtil.roundMoney(originalPaymentAmount.multiply(newPrice).divide(originalPrice, RoundingMode.HALF_UP));
            }
        default:
            throw new IllegalArgumentException();
        }
    }

    private List<PreauthorizedPayment> getPreauthorizedPayments(BillingAccount billingAccount) {
        List<PreauthorizedPayment> records = new ArrayList<PreauthorizedPayment>();

        List<LeaseTermTenant> leaseParticipants;
        {
            EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
            criteria.eq(criteria.proto().leaseTermV().holder().lease().billingAccount(), billingAccount);
            criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
            criteria.isCurrent(criteria.proto().leaseTermV());
            criteria.asc(criteria.proto().leaseParticipant().participantId());
            leaseParticipants = Persistence.service().query(criteria);

            // Make Applicant first 
            for (int i = 0; i < leaseParticipants.size(); i++) {
                LeaseTermTenant leaseParticipant = leaseParticipants.get(i);
                if (leaseParticipant.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                    if (i != 0) {
                        leaseParticipants.remove(i);
                        leaseParticipants.add(0, leaseParticipant);
                    }
                    break;
                }
            }
        }

        for (LeaseTermTenant leaseParticipant : leaseParticipants) {
            List<PreauthorizedPayment> preauthorizedPayments;
            {
                EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
                criteria.eq(criteria.proto().tenant(), leaseParticipant.leaseParticipant().cast());
                criteria.eq(criteria.proto().isDeleted(), false);

                //criteria.isNotNull(criteria.proto().expiring());

                criteria.asc(criteria.proto().id());
                preauthorizedPayments = Persistence.service().query(criteria);
            }
            for (PreauthorizedPayment pap : preauthorizedPayments) {
                // TODO Filter 
                records.add(pap);
            }
        }
        return records;
    }
}
