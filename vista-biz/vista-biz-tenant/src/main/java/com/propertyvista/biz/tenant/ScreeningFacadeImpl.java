/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.validation.validators.lease.ScreeningValidator;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.PersonCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;
import com.propertyvista.dto.LeaseApprovalDTO.SuggestedDecision;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.equifax.EquifaxCreditCheck;

public class ScreeningFacadeImpl implements ScreeningFacade {

    private final static I18n i18n = I18n.get(ScreeningFacadeImpl.class);

    @Override
    public void calculateSuggestedDecision(BigDecimal rentAmount, LeaseApprovalDTO leaseApproval) {
        leaseApproval.rentAmount().setValue(rentAmount);
        if (rentAmount == null) {
            return;
        }

        BigDecimal amountApproved = BigDecimal.ZERO;
        int creditCheckCount = 0;

        Set<CreditCheckResult> creditCheckResults = new HashSet<CreditCheckResult>();

        for (LeaseParticipanApprovalDTO participant : leaseApproval.participants()) {
            if (participant.creditCheck().isNull()) {
                continue;
            }
            creditCheckCount++;
            if (participant.creditCheck().creditCheckResult().getValue() == CreditCheckResult.Accept) {
                amountApproved = amountApproved.add(participant.creditCheck().amountApproved().getValue());
            }
            creditCheckResults.add(participant.creditCheck().creditCheckResult().getValue());
        }

        if (creditCheckCount == 0) {
            leaseApproval.suggestedDecision().setValue(SuggestedDecision.RunCreditCheck);
        } else {
            leaseApproval.totalAmountApproved().setValue(amountApproved);
            leaseApproval.percenrtageApproved().setValue(amountApproved.divide(rentAmount, RoundingMode.DOWN).multiply(new BigDecimal("100.00")).doubleValue());

            if (creditCheckResults.contains(CreditCheckResult.Decline)) {
                leaseApproval.suggestedDecision().setValue(SuggestedDecision.Decline);
            } else {
                if (amountApproved.compareTo(rentAmount) >= 0) {
                    if (creditCheckResults.contains(CreditCheckResult.Review)) {
                        leaseApproval.suggestedDecision().setValue(SuggestedDecision.ManualReview);
                    } else {
                        leaseApproval.suggestedDecision().setValue(SuggestedDecision.Approve);
                    }
                } else {
                    if (creditCheckResults.contains(CreditCheckResult.Review)) {
                        leaseApproval.suggestedDecision().setValue(SuggestedDecision.ManualReview);
                    } else if (creditCheckResults.contains(CreditCheckResult.ReviewNoInformationAvalable)) {
                        leaseApproval.suggestedDecision().setValue(SuggestedDecision.ManualReview);
                    } else {
                        leaseApproval.suggestedDecision().setValue(SuggestedDecision.RequestGuarantor);
                    }
                }
            }
        }
    }

    @Override
    public void runCreditCheck(BigDecimal rentAmount, LeaseParticipant<?> leaseParticipantId, Employee currentUserEmployee) {
        LeaseParticipant<?> leaseParticipant = (LeaseParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());
        PersonScreening screening = retrivePersonScreening(leaseParticipant.leaseCustomer().customer());

        PersonCreditCheck pcc = EntityFactory.create(PersonCreditCheck.class);
        pcc.amountCheked().setValue(rentAmount);
        pcc.screening().set(screening);
        pcc.createdBy().set(currentUserEmployee);

        Persistence.service().retrieve(leaseParticipant.leaseTermV());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease());

        BackgroundCheckPolicy backgroundCheckPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                leaseParticipant.leaseTermV().holder().lease().unit(), BackgroundCheckPolicy.class);
        pcc.backgroundCheckPolicy().set(EntityGraph.businessDuplicate(backgroundCheckPolicy.version()));

        pcc = EquifaxCreditCheck.runCreditCheck(leaseParticipant.leaseCustomer().customer(), pcc, backgroundCheckPolicy.strategyNumber().getValue());

        Persistence.service().persist(pcc);
        Persistence.service().commit();
    }

    private PersonScreening retrivePersonScreening(Customer customer) {
        EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customer));
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        PersonScreening screening = Persistence.service().retrieve(criteria);
        if ((screening != null) && (!screening.version().isNull())) {
            if (ScreeningValidator.screeningIsAutomaticallyFinalized) {
                screening.saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(screening);
                return screening;
            } else {
                throw new UserRuntimeException(i18n.tr("Unfinalized Screening exists"));
            }
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        criteria.desc(criteria.proto().version().fromDate());
        screening = Persistence.service().retrieve(criteria);
        if (screening == null) {
            throw new UserRuntimeException(i18n.tr("Screening does not exists"));
        } else {
            return screening;
        }

    }

    @Override
    public PersonScreening retrivePersonScreeningFinalOrDraft(Customer customerId, AttachLevel attachLevel) {
        EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customerId));
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        PersonScreening screening = Persistence.service().retrieve(criteria, attachLevel);
        if (screening != null) {
            return screening;
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        screening = Persistence.service().retrieve(criteria, attachLevel);
        return screening;
    }

    @Override
    public PersonScreening retrivePersonScreeningDraftOrFinal(Customer customerId, AttachLevel attachLevel) {
        EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customerId));
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        PersonScreening screening = Persistence.service().retrieve(criteria, attachLevel);
        if (screening != null) {
            return screening;
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        screening = Persistence.service().retrieve(criteria, attachLevel);
        return screening;
    }

    @Override
    public PersonCreditCheck retrivePersonCreditCheck(Customer customerId) {
        EntityQueryCriteria<PersonCreditCheck> criteria = EntityQueryCriteria.create(PersonCreditCheck.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screening().screene(), customerId));
        criteria.add(PropertyCriterion.ge(criteria.proto().creditCheckDate(), DateUtils.addDays(new Date(), -30)));
        criteria.desc(criteria.proto().creditCheckDate());
        return Persistence.service().retrieve(criteria);
    }
}