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
import java.util.List;

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
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewChargeDetailDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.shared.config.VistaFeatures;

class PreauthorizedPaymentAutoPayReviewReport {

    List<AutoPayReviewDTO> reportSuspendedPreauthorizedPayments(List<Building> selectedBuildings) {
        List<AutoPayReviewDTO> records = new ArrayList<AutoPayReviewDTO>();

        ICursorIterator<BillingAccount> billingAccountIterator;
        { //TODO->Closure
            EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
            if (selectedBuildings != null) {
                criteria.in(criteria.proto().lease().unit().building(), selectedBuildings);
            }
            criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());

            // TODO make it actually do its job
            //criteria.isNotNull(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().expiring());

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
        review.lease().set(billingAccount.lease().createIdentityStub());
        review.unit().setValue(billingAccount.lease().unit().info().number().getValue());

        review.paymentDue().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(billingAccount.lease()));

        AutoPayChangePolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(billingAccount.lease().unit().building(),
                AutoPayChangePolicy.class);
        AutoPayChangePolicy.ChangeRule changeRule = policy.rule().getValue();

        List<PreauthorizedPayment> preauthorizedPayments = getPreauthorizedPayments(billingAccount);
        for (PreauthorizedPayment preauthorizedPayment : preauthorizedPayments) {
            review.pap().add(createPreauthorizedPaymentPreview(billingAccount, changeRule, preauthorizedPayment));
        }

        return review;
    }

    //TODO proper implementation that will use adjustments
    private BigDecimal getActualPrice(BillableItem billableItem) {
        return billableItem.agreedPrice().getValue();
    }

    private AutoPayReviewPreauthorizedPaymentDTO createPreauthorizedPaymentPreview(BillingAccount billingAccount, AutoPayChangePolicy.ChangeRule changeRule,
            PreauthorizedPayment preauthorizedPayment) {
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

            chargeReview.suggested().set(calulateSuggestedChargeDetail(billingAccount, changeRule, allBillableItem, coveredItem));

            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(coveredItem.billableItem()));

            papReview.items().add(chargeReview);
        }

        // Add newly added items
        for (BillableItem billableItem : allBillableItem) {
            AutoPayReviewChargeDTO chargeReview = EntityFactory.create(AutoPayReviewChargeDTO.class);

            chargeReview.suggested().billableItem().set(billableItem.createIdentityStub());
            chargeReview.suggested().totalPrice().setValue(getActualPrice(billableItem));

            chargeReview.leaseCharge().setValue(getLeaseChargeDescription(billableItem));

            papReview.items().add(chargeReview);
        }

        return papReview;
    }

    private String getLeaseChargeDescription(BillableItem billableItem) {
        String description = "";
        if (VistaFeatures.instance().yardiIntegration()) {
            description = billableItem.extraData().getStringView() + " ";
        }
        description += billableItem.description().getValue();
        return description;
    }

    private void calulatePercent(AutoPayReviewChargeDetailDTO suspended) {
        if (suspended.totalPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            suspended.percent().setValue(suspended.payment().getValue().divide(suspended.totalPrice().getValue(), 2, RoundingMode.FLOOR));
        } else {
            suspended.percent().setValue(BigDecimal.ZERO);
        }
    }

    public BillableItem extractSameBillableItem(BillableItem item, List<BillableItem> allBillableItem) {
        for (BillableItem billableItem : allBillableItem) {
            if (billableItem.uid().equals(item.uid())) {
                allBillableItem.remove(billableItem);
                return billableItem;
            }
        }
        return null;
    }

    private AutoPayReviewChargeDetailDTO calulateSuggestedChargeDetail(BillingAccount billingAccount, AutoPayChangePolicy.ChangeRule changeRule,
            List<BillableItem> allBillableItem, PreauthorizedPaymentCoveredItem coveredItem) {
        AutoPayReviewChargeDetailDTO suggestedChargeDetail = EntityFactory.create(AutoPayReviewChargeDetailDTO.class);

        BillableItem sameBillableItem = extractSameBillableItem(coveredItem.billableItem(), allBillableItem);
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
                return DomainUtil.roundMoney(originalPaymentAmount.multiply(newPrice).divide(originalPrice));
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
