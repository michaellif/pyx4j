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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewChargeDetailDTO;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.shared.config.VistaFeatures;

class AutopayReviewReport {

    List<AutoPayReviewLeaseDTO> reportPreauthorizedPaymentsRequiresReview(PreauthorizedPaymentsReportCriteria reportCriteria) {
        List<AutoPayReviewLeaseDTO> records = new ArrayList<AutoPayReviewLeaseDTO>();

        ICursorIterator<BillingAccount> billingAccountIterator;
        { //TODO->Closure
            EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
            if (reportCriteria.selectedBuildings != null) {
                criteria.in(criteria.proto().lease().unit().building(), reportCriteria.selectedBuildings);
            } else {
                criteria.in(criteria.proto().lease().unit().building().suspended(), false);
            }
            criteria.eq(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), false);
            criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().updatedBySystem());

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
                AutoPayReviewLeaseDTO leaseRecord = createBillingAccountReview(billingAccountIterator.next());
                if (leaseRecord != null) {
                    records.add(leaseRecord);
                }
            }
        } finally {
            billingAccountIterator.close();
        }

        return records;
    }

    AutoPayReviewLeaseDTO getPreauthorizedPaymentRequiresReview(BillingAccount billingAccountId) {
        return createBillingAccountReview(Persistence.service().retrieve(BillingAccount.class, billingAccountId.getPrimaryKey()));
    }

    private AutoPayReviewLeaseDTO createBillingAccountReview(BillingAccount billingAccount) {
        AutoPayReviewLeaseDTO leaseReview = EntityFactory.create(AutoPayReviewLeaseDTO.class);

        Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit().building(), AttachLevel.Attached);

        leaseReview.building().setValue(billingAccount.lease().unit().building().propertyCode().getValue());

        leaseReview.leaseId().setValue(billingAccount.lease().leaseId().getValue());
        leaseReview.lease().set(billingAccount.lease().duplicate());
        // Clear unused values
        leaseReview.lease().billingAccount().setValueDetached();
        leaseReview.lease().currentTerm().setValueDetached();
        leaseReview.lease().previousTerm().setValueDetached();
        leaseReview.lease().nextTerm().setValueDetached();
        leaseReview.lease().unit().setValueDetached();

        leaseReview.unit().setValue(billingAccount.lease().unit().info().number().getValue());

        BillingCycle nextPaymentCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(billingAccount.lease());
        BillingCycle previousPaymentCycle = ServerSideFactory.create(BillingCycleFacade.class).getPriorBillingCycle(nextPaymentCycle);

        leaseReview.paymentDue().setValue(nextPaymentCycle.targetAutopayExecutionDate().getValue());

        boolean reviewRequired = false;
        List<AutopayAgreement> preauthorizedPayments = getPreauthorizedPayments(billingAccount, nextPaymentCycle);
        Set<AutopayAgreement> previousCycleRemovedPayments = new HashSet<AutopayAgreement>();
        for (AutopayAgreement preauthorizedPayment : preauthorizedPayments) {
            AutoPayReviewPreauthorizedPaymentDTO papReview = createPreauthorizedPaymentPreview(preauthorizedPayment, previousPaymentCycle, nextPaymentCycle);
            leaseReview.pap().add(papReview);

            if (papReview.reviewRequired().getValue()) {
                reviewRequired = true;
            }
            if (!papReview.previousCyclePap().isNull()) {
                previousCycleRemovedPayments.add(papReview.previousCyclePap());
            }
        }

        if (!reviewRequired) {
            return null;
        }

        // add PreviousCycle Removed Payment Agreements
        {
            EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            criteria.eq(criteria.proto().padBillingCycle(), previousPaymentCycle);
            for (PaymentRecord paymentRecord : Persistence.service().query(criteria)) {
                if ((!paymentRecord.preauthorizedPayment().isNull()) && (!previousCycleRemovedPayments.contains(paymentRecord.preauthorizedPayment()))) {
                    leaseReview.pap().add(createPreviousPreauthorizedPaymentPreview(paymentRecord.preauthorizedPayment()));
                }
            }
        }

        calulateLeaseTotals(leaseReview);
        return leaseReview;
    }

    private void calulateLeaseTotals(AutoPayReviewLeaseDTO review) {
        // Count each item once in totals
        Set<BillableItem> countedSuspended = new HashSet<BillableItem>();
        Set<BillableItem> countedSuggested = new HashSet<BillableItem>();

        for (AutoPayReviewPreauthorizedPaymentDTO pap : review.pap()) {
            for (AutoPayReviewChargeDTO chargeReview : pap.items()) {
                if (!chargeReview.previous().billableItem().isNull() && !countedSuspended.contains(chargeReview.previous().billableItem())) {
                    countedSuspended.add(chargeReview.previous().billableItem());
                    DomainUtil.nvlAddBigDecimal(review.totalPrevious().totalPrice(), chargeReview.previous().totalPrice());
                }
                DomainUtil.nvlAddBigDecimal(review.totalPrevious().payment(), chargeReview.previous().payment());

                if (!chargeReview.current().billableItem().isNull() && !countedSuggested.contains(chargeReview.current().billableItem())) {
                    countedSuggested.add(chargeReview.current().billableItem());
                    DomainUtil.nvlAddBigDecimal(review.totalCurrent().totalPrice(), chargeReview.current().totalPrice());
                }
                DomainUtil.nvlAddBigDecimal(review.totalCurrent().payment(), chargeReview.current().payment());
            }
        }

        if (!review.totalPrevious().totalPrice().isNull()) {
            calulatePercent(review.totalPrevious());
        }
        if (!review.totalCurrent().totalPrice().isNull()) {
            calulatePercent(review.totalCurrent());
        }
    }

    private AutoPayReviewPreauthorizedPaymentDTO createPreauthorizedPaymentPreview(AutopayAgreement preauthorizedPayment, BillingCycle previousPaymentCycle,
            BillingCycle nextPaymentCycle) {
        AutoPayReviewPreauthorizedPaymentDTO papReview = EntityFactory.create(AutoPayReviewPreauthorizedPaymentDTO.class);

        papReview.pap().set(preauthorizedPayment.createIdentityStub());
        papReview.tenant_().set(preauthorizedPayment.tenant().createIdentityStub());
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);
        papReview.tenantName().setValue(preauthorizedPayment.tenant().customer().person().name().getStringView());

        papReview.paymentMethodView().setValue(preauthorizedPayment.paymentMethod().getStringView());

        papReview.reviewRequired().setValue(nextPaymentCycle.billingCycleStartDate().equals(preauthorizedPayment.updatedBySystem()));
        papReview.changedByTenant().setValue(AutopayAgreementMananger.isChangeByTenant(preauthorizedPayment, nextPaymentCycle));

        Map<String, AutopayAgreementCoveredItem> coveredItemItemsPrevious = new LinkedHashMap<String, AutopayAgreementCoveredItem>();
        if (!preauthorizedPayment.reviewOfPap().isNull()) {
            boolean hasPaymentRecords = false;
            {
                EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
                criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment.reviewOfPap());
                criteria.eq(criteria.proto().padBillingCycle(), previousPaymentCycle);
                hasPaymentRecords = Persistence.service().count(criteria) > 0;
            }
            if (hasPaymentRecords) {
                papReview.previousCyclePap().set(preauthorizedPayment.reviewOfPap().createIdentityStub());
                Persistence.ensureRetrieve(preauthorizedPayment.reviewOfPap(), AttachLevel.Attached);

                for (AutopayAgreementCoveredItem coveredItem : preauthorizedPayment.reviewOfPap().coveredItems()) {
                    coveredItemItemsPrevious.put(coveredItem.billableItem().uid().getValue(), coveredItem);
                }
            }
        }

        for (AutopayAgreementCoveredItem coveredItem : preauthorizedPayment.coveredItems()) {
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);
            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));
            chargeReview.current().billableItem().set(coveredItem.billableItem().createIdentityStub());
            chargeReview.current().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItem.billableItem()));
            chargeReview.current().payment().setValue(coveredItem.amount().getValue());
            calulatePercent(chargeReview.current());

            AutopayAgreementCoveredItem coveredItemItemPrevious = coveredItemItemsPrevious.remove(coveredItem.billableItem().uid().getValue());
            if (coveredItemItemPrevious != null) {
                chargeReview.previous().billableItem().set(coveredItemItemPrevious.billableItem().createIdentityStub());
                chargeReview.previous().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItemItemPrevious.billableItem()));
                chargeReview.previous().payment().setValue(coveredItemItemPrevious.amount().getValue());
                calulatePercent(chargeReview.previous());

                if (chargeReview.previous().totalPrice().getValue().compareTo(BigDecimal.ZERO) == 0) {
                    if (chargeReview.current().totalPrice().getValue().compareTo(BigDecimal.ZERO) == 0) {
                        chargeReview.current().percentChange().setValue(BigDecimal.ZERO);
                    } else {
                        chargeReview.current().percentChange().setValue(BigDecimal.ONE);
                    }
                } else {
                    chargeReview
                            .current()
                            .percentChange()
                            .setValue(
                                    chargeReview.current().totalPrice().getValue()
                                            .divide(chargeReview.previous().totalPrice().getValue(), 4, RoundingMode.FLOOR).subtract(BigDecimal.ONE));
                }

            }

            papReview.items().add(chargeReview);
        }

        // Removed billableItem are shown at the end, and they don't have current price
        for (AutopayAgreementCoveredItem coveredItem : coveredItemItemsPrevious.values()) {
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));
            chargeReview.previous().billableItem().set(coveredItem.billableItem().createIdentityStub());
            chargeReview.previous().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItem.billableItem()));
            chargeReview.previous().payment().setValue(coveredItem.amount().getValue());
            calulatePercent(chargeReview.previous());

            papReview.items().add(chargeReview);
        }
        return papReview;
    }

    private AutoPayReviewPreauthorizedPaymentDTO createPreviousPreauthorizedPaymentPreview(AutopayAgreement preauthorizedPayment) {
        AutoPayReviewPreauthorizedPaymentDTO papReview = EntityFactory.create(AutoPayReviewPreauthorizedPaymentDTO.class);
        Persistence.ensureRetrieve(preauthorizedPayment, AttachLevel.Attached);

        papReview.previousCyclePap().set(preauthorizedPayment.createIdentityStub());

        papReview.tenant_().set(preauthorizedPayment.tenant().createIdentityStub());
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);
        papReview.tenantName().setValue(preauthorizedPayment.tenant().customer().person().name().getStringView());

        papReview.paymentMethodView().setValue(preauthorizedPayment.paymentMethod().getStringView());

        papReview.reviewRequired().setValue(false);
        papReview.changedByTenant().setValue(false);

        // Removed billableItem , and they don't have current price
        for (AutopayAgreementCoveredItem coveredItem : preauthorizedPayment.coveredItems()) {
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));
            chargeReview.previous().billableItem().set(coveredItem.billableItem().createIdentityStub());
            chargeReview.previous().totalPrice().setValue(PaymentBillableUtils.getActualPrice(coveredItem.billableItem()));
            chargeReview.previous().payment().setValue(coveredItem.amount().getValue());
            calulatePercent(chargeReview.previous());

            papReview.items().add(chargeReview);
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
            description += billableItem.item().name().getStringView();
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

    private List<AutopayAgreement> getPreauthorizedPayments(BillingAccount billingAccount, BillingCycle nextPaymentCycle) {
        List<AutopayAgreement> records = new ArrayList<AutopayAgreement>();

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
            List<AutopayAgreement> preauthorizedPayments;
            {
                EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
                criteria.eq(criteria.proto().tenant(), leaseParticipant.leaseParticipant().cast());
                criteria.eq(criteria.proto().isDeleted(), false);
                criteria.asc(criteria.proto().id());
                preauthorizedPayments = Persistence.service().query(criteria);
            }
            records.addAll(preauthorizedPayments);
        }
        return records;
    }
}
