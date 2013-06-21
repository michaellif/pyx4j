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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.policy.policies.AutoPayChangePolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
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
            }
            criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
            criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().expiring());

            if (reportCriteria.isLeasesOnNoticeOnly()) {
                criteria.eq(criteria.proto().lease().completion(), Lease.CompletionType.Notice);
            }

            if (reportCriteria.hasExpectedMoveOutFilter()) {
                criteria.ge(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMinExpectedMoveOut());
                criteria.le(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMaxExpectedMoveOut());
            }

            criteria.asc(criteria.proto().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().lease().leaseId());

            billingAccountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
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

        review.paymentDue().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(billingAccount.lease()));

        AutoPayChangePolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(billingAccount.lease().unit().building(),
                AutoPayChangePolicy.class);
        AutoPayChangePolicy.ChangeRule changeRule = policy.rule().getValue();

        List<PreauthorizedPayment> preauthorizedPayments = getPreauthorizedPayments(billingAccount);
        for (PreauthorizedPayment preauthorizedPayment : preauthorizedPayments) {
            review.pap().add(createPreauthorizedPaymentPreview(billingAccount, changeRule, review.paymentDue().getValue(), preauthorizedPayment));
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

    //TODO proper implementation that will use adjustments
    private BigDecimal getActualPrice(BillableItem billableItem) {
        return billableItem.agreedPrice().getValue();
    }

    private AutoPayReviewPreauthorizedPaymentDTO createPreauthorizedPaymentPreview(BillingAccount billingAccount, AutoPayChangePolicy.ChangeRule changeRule,
            LogicalDate preauthorizedPaymentDate, PreauthorizedPayment preauthorizedPayment) {
        AutoPayReviewPreauthorizedPaymentDTO papReview = EntityFactory.create(AutoPayReviewPreauthorizedPaymentDTO.class);

        papReview.pap().set(preauthorizedPayment.createIdentityStub());
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);
        papReview.tenantName().setValue(preauthorizedPayment.tenant().customer().person().name().getStringView());

        // Once BillableItem is consumed by CoveredItem it is removed from the list
        List<BillableItem> allBillableItem = new ArrayList<BillableItem>();
        allBillableItem.add(billingAccount.lease().currentTerm().version().leaseProducts().serviceItem());
        allBillableItem.addAll(billingAccount.lease().currentTerm().version().leaseProducts().featureItems());

        for (PreauthorizedPaymentCoveredItem coveredItem : preauthorizedPayment.coveredItems()) {
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

            chargeReview.suspended().billableItem().set(coveredItem.billableItem().createIdentityStub());
            chargeReview.suspended().totalPrice().setValue(getActualPrice(coveredItem.billableItem()));
            chargeReview.suspended().payment().setValue(coveredItem.amount().getValue());
            calulatePercent(chargeReview.suspended());

            chargeReview.suggested().set(calulateSuggestedChargeDetail(billingAccount, changeRule, preauthorizedPaymentDate, allBillableItem, coveredItem));

            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));

            if (!chargeReview.suggested().totalPrice().isNull()) {
                chargeReview
                        .suggested()
                        .percentChange()
                        .setValue( //
                                chargeReview.suggested().totalPrice().getValue()
                                        .divide(chargeReview.suspended().totalPrice().getValue(), 4, RoundingMode.FLOOR).subtract(BigDecimal.ONE));
            }

            papReview.items().add(chargeReview);
        }

        // Add newly added items
        for (BillableItem billableItem : allBillableItem) {
            if (isBillableItemPapable(billableItem, preauthorizedPaymentDate)) {
                AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

                chargeReview.suggested().billableItem().set(billableItem.createIdentityStub());
                chargeReview.suggested().totalPrice().setValue(getActualPrice(billableItem));

                chargeReview.leaseCharge().setValue(getLeaseChargeDescription(billableItem));

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

    public BillableItem extractSameBillableItem(BillableItem item, LogicalDate preauthorizedPaymentDate, List<BillableItem> allBillableItem) {
        for (BillableItem billableItem : allBillableItem) {
            if (billableItem.uid().equals(item.uid())) {
                allBillableItem.remove(billableItem);

                if (isBillableItemPapable(billableItem, preauthorizedPaymentDate)) {
                    return billableItem;
                } else {
                    // Just return matching item from the list (test case two parkings with the same code)
                    return null;
                }
            }
        }
        return null;
    }

    private AutoPayReviewChargeDetailDTO calulateSuggestedChargeDetail(BillingAccount billingAccount, AutoPayChangePolicy.ChangeRule changeRule,
            LogicalDate preauthorizedPaymentDate, List<BillableItem> allBillableItem, PreauthorizedPaymentCoveredItem coveredItem) {
        AutoPayReviewChargeDetailDTO suggestedChargeDetail = EntityFactory.create(AutoPayReviewChargeDetailDTO.class);

        BillableItem sameBillableItem = extractSameBillableItem(coveredItem.billableItem(), preauthorizedPaymentDate, allBillableItem);
        if (sameBillableItem == null) {
            // Removed item
        } else {
            suggestedChargeDetail.billableItem().set(sameBillableItem.createIdentityStub());
            suggestedChargeDetail.totalPrice().setValue(getActualPrice(sameBillableItem));

            suggestedChargeDetail.payment().setValue(
                    calulateNewPaymentValue(coveredItem.amount().getValue(), getActualPrice(coveredItem.billableItem()), getActualPrice(sameBillableItem),
                            changeRule));

            calulatePercent(suggestedChargeDetail);
        }

        return suggestedChargeDetail;
    }

    private BigDecimal calulateNewPaymentValue(BigDecimal originalPaymentAmount, BigDecimal originalPrice, BigDecimal newPrice,
            AutoPayChangePolicy.ChangeRule changeRule) {
        switch (changeRule) {
        case keepUnchanged:
            return originalPaymentAmount;
        case keepPercentage:
            if (originalPaymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
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
