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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.validation.validators.lease.ScreeningValidator;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;
import com.propertyvista.dto.LeaseApprovalDTO.SuggestedDecision;
import com.propertyvista.dto.LeaseApprovalParticipantDTO;
import com.propertyvista.equifax.EquifaxCreditCheck;

public class ScreeningFacadeImpl implements ScreeningFacade {

    @Override
    public void calculateSuggestedDecision(BigDecimal rentAmount, LeaseApprovalDTO leaseApproval) {
        leaseApproval.rentAmount().setValue(rentAmount);
        if (rentAmount == null) {
            return;
        }

        BigDecimal amountApproved = BigDecimal.ZERO;
        int noCreditCheckCount = 0;

        for (LeaseApprovalParticipantDTO participant : leaseApproval.participants()) {
            if (participant.creditCheck().isNull()) {
                noCreditCheckCount++;
                continue;
            }
            switch (participant.creditCheck().creditCheckResult().getValue()) {
            case Accept:
                amountApproved.add(participant.creditCheck().amountApproved().getValue());
                break;
            case Decline:
                leaseApproval.suggestedDecision().setValue(SuggestedDecision.Decline);
                break;
            case SoftDecline:
                leaseApproval.suggestedDecision().setValue(SuggestedDecision.RequestInfo);
                break;
            }

        }

        if (amountApproved.compareTo(BigDecimal.ZERO) > 0) {
            leaseApproval.totalAmountApproved().setValue(amountApproved);
            leaseApproval.percenrtageApproved().setValue(amountApproved.divide(rentAmount).multiply(new BigDecimal("100.00")).doubleValue());
        }

        if (amountApproved.compareTo(rentAmount) >= 0) {
            leaseApproval.suggestedDecision().setValue(SuggestedDecision.Approve);
        } else if (leaseApproval.suggestedDecision().isNull()) {
            if (noCreditCheckCount > 0) {
                leaseApproval.suggestedDecision().setValue(SuggestedDecision.RunCreditCheck);
            } else {
                leaseApproval.suggestedDecision().setValue(SuggestedDecision.Decline);
            }
        }
    }

    @Override
    public void runCreditCheck(BigDecimal rentAmount, LeaseParticipant<?> leaseParticipantId) {
        LeaseParticipant<?> leaseParticipant = (LeaseParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());
        PersonScreening screening = retrivePersonScreening(leaseParticipant.leaseCustomer().customer());

        PersonCreditCheck pcc = EntityFactory.create(PersonCreditCheck.class);
        pcc.amountCheked().setValue(rentAmount);
        pcc.screening().set(screening);

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
                throw new UserRuntimeException("Unfinalized Screening exists");
            }
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        criteria.desc(criteria.proto().version().fromDate());
        screening = Persistence.service().retrieve(criteria);
        if (screening == null) {
            throw new UserRuntimeException("Screening nod not exists");
        } else {
            return screening;
        }

    }
}