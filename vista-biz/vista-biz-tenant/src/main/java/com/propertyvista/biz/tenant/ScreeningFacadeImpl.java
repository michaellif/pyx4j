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
 */
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.EquifaxFacade;
import com.propertyvista.biz.validation.validators.lease.ScreeningValidator;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;
import com.propertyvista.domain.media.ProofOfIncomeDocumentFile;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.LegalQuestionsPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationDocumentType.Importance;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.LegalQuestionsPolicyItem;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestion;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;
import com.propertyvista.dto.LeaseApprovalDTO.SuggestedDecision;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.equifax.EquifaxCreditCheck;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.common.util.LocalizedContent;

public class ScreeningFacadeImpl implements ScreeningFacade {

    private final static I18n i18n = I18n.get(ScreeningFacadeImpl.class);

    @Override
    public boolean isCreditCheckActivated() {
        return isCreditCheckActivated(getCurrentPmcEquifaxInfo());
    }

    @Override
    public boolean isReadReportLimitReached() {
        return ServerSideFactory.create(EquifaxFacade.class).isLimitReached(AuditRecordEventType.EquifaxReadReport);
    }

    @Override
    public PmcEquifaxStatus getCreditCheckServiceStatus() {
        return getCurrentPmcEquifaxInfo().status().getValue(PmcEquifaxStatus.NotRequested);
    }

    private boolean isCreditCheckActivated(PmcEquifaxInfo equifaxInfo) {
        return (equifaxInfo.status().getValue() == PmcEquifaxStatus.Active) && !equifaxInfo.reportType().isNull();
    }

    private static PmcEquifaxInfo getCurrentPmcEquifaxInfo() {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                return null;
            }
        });
        return pmc.equifaxInfo();
    }

    @Override
    public void calculateSuggestedDecision(BigDecimal rentAmount, LeaseApprovalDTO leaseApproval) {
        leaseApproval.rentAmount().setValue(rentAmount);
        if (rentAmount == null) {
            return;
        }

        if (rentAmount.compareTo(BigDecimal.ZERO) == 0) {
            leaseApproval.recommendedDecision().setValue(SuggestedDecision.ManualReview);
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
            leaseApproval.recommendedDecision().setValue(SuggestedDecision.RunCreditCheck);
        } else {
            leaseApproval.totalAmountApproved().setValue(amountApproved);
            BigDecimal percenrtageApproved = amountApproved.divide(rentAmount, RoundingMode.DOWN);
            if (percenrtageApproved.compareTo(BigDecimal.ONE) > 0) {
                percenrtageApproved = BigDecimal.ONE;
            }
            leaseApproval.percenrtageApproved().setValue(percenrtageApproved);

            if (creditCheckResults.contains(CreditCheckResult.Decline)) {
                leaseApproval.recommendedDecision().setValue(SuggestedDecision.Decline);
            } else {
                if (amountApproved.compareTo(rentAmount) >= 0) {
                    if (creditCheckResults.contains(CreditCheckResult.Review)) {
                        leaseApproval.recommendedDecision().setValue(SuggestedDecision.ManualReview);
                    } else {
                        leaseApproval.recommendedDecision().setValue(SuggestedDecision.Approve);
                    }
                } else {
                    if (creditCheckResults.contains(CreditCheckResult.Review)) {
                        leaseApproval.recommendedDecision().setValue(SuggestedDecision.ManualReview);
                    } else if (creditCheckResults.contains(CreditCheckResult.ReviewNoInformationAvalable)) {
                        leaseApproval.recommendedDecision().setValue(SuggestedDecision.ManualReview);
                    } else {
                        leaseApproval.recommendedDecision().setValue(SuggestedDecision.RequestGuarantor);
                    }
                }
            }
        }
    }

    @Override
    public CustomerCreditCheck runCreditCheck(BigDecimal rentAmount, LeaseTermParticipant<?> leaseParticipantId, Employee currentUserEmployee) {
        PmcEquifaxInfo equifaxInfo = getCurrentPmcEquifaxInfo();
        if (!isCreditCheckActivated(equifaxInfo)) {
            throw new UserRuntimeException(i18n.tr("Credit Check interface was not activated"));
        }

        LeaseTermParticipant<?> leaseTermParticipant = (LeaseTermParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());

        Persistence.ensureRetrieve(leaseTermParticipant.leaseTermV().holder().lease(), AttachLevel.Attached);

        Lease lease = leaseTermParticipant.leaseTermV().holder().lease();

        CustomerCreditCheck ccc = EntityFactory.create(CustomerCreditCheck.class);

        ccc.amountChecked().setValue(rentAmount);
        ccc.screening().set(retrivePersonScreening(leaseTermParticipant.leaseParticipant().customer()));
        ccc.screene().set(leaseTermParticipant.leaseParticipant());
        ccc.building().set(lease.unit().building());
        ccc.createdBy().set(currentUserEmployee);

        BackgroundCheckPolicy backgroundCheckPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(ccc.building(),
                BackgroundCheckPolicy.class);
        ccc.backgroundCheckPolicy().set(EntityGraph.businessDuplicate(backgroundCheckPolicy.version()));

        Persistence.ensureRetrieve(ccc.screening().version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(ccc.screening().version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(ccc.screening().version().documents(), AttachLevel.Attached);

        ServerSideFactory.create(EquifaxFacade.class).validateRequiredData(leaseTermParticipant.leaseParticipant().customer(), ccc);

        ccc.transactionId().setValue(ScreeningPayments.preAuthorization(equifaxInfo));

        // Need this for simulations
        lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());

        boolean success = false;
        try {
            ccc = ServerSideFactory.create(EquifaxFacade.class).runCreditCheck(equifaxInfo, leaseTermParticipant.leaseParticipant().customer(), ccc,
                    backgroundCheckPolicy.strategyNumber().getValue(), lease, leaseTermParticipant);

            // This is the business, we charge only when riskCode is returned
            success = !ccc.riskCode().isNull();

            Persistence.service().persist(ccc);

            Persistence.service().commit();
        } finally {
            if (!ccc.transactionId().isNull()) {
                if (success) {
                    ScreeningPayments.compleateTransaction(ccc.transactionId().getValue());
                } else {
                    ScreeningPayments.preAuthorizationReversal(ccc.transactionId().getValue());
                }
            }
        }

        return ccc;
    }

    private CustomerScreening retrivePersonScreening(Customer customer) {
        EntityQueryCriteria<CustomerScreening> criteria = EntityQueryCriteria.create(CustomerScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customer));
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        CustomerScreening screening = Persistence.service().retrieve(criteria);
        if ((screening != null) && (!screening.version().isNull())) {
            if (ScreeningValidator.screeningIsAutomaticallyFinalized) {
                screening.saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(screening);
                return screening;
            } else {
                throw new UserRuntimeException(i18n.tr("Non-finalized Screening exists"));
            }
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        screening = Persistence.service().retrieve(criteria);
        if (screening == null) {
            throw new UserRuntimeException(i18n.tr("Screening does not exists"));
        } else {
            return screening;
        }
    }

    @Override
    public CustomerScreening retrivePersonScreeningFinalOrDraft(Customer customerId, AttachLevel attachLevel) {
        EntityQueryCriteria<CustomerScreening> criteria = EntityQueryCriteria.create(CustomerScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customerId));
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        CustomerScreening screening = Persistence.service().retrieve(criteria, attachLevel);
        if (screening != null) {
            return screening;
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        screening = Persistence.service().retrieve(criteria, attachLevel);
        return screening;
    }

    @Override
    public CustomerScreening retrivePersonScreeningDraftForEdit(Customer customerId, PolicyNode policyNode) {
        CustomerScreening screening;
        CustomerScreening screeningId = retrivePersonScreeningDraftOrFinal(customerId, AttachLevel.IdOnly);
        if (screeningId == null) {
            screening = EntityFactory.create(CustomerScreening.class);
            screening.screene().set(customerId);
        } else {
            screening = Persistence.retrieveDraftForEdit(CustomerScreening.class, screeningId.getPrimaryKey());
        }

        // initialize required docs for new screening version
        if (screening.getPrimaryKey() == null && policyNode != null) {
            initializeRequiredDocuments(screening, policyNode);
            initializeLegalQuestions(screening, policyNode);
        }

        return screening;
    }

    private void initializeRequiredDocuments(CustomerScreening screening, PolicyNode policyNode) {
        ApplicationDocumentationPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode,
                ApplicationDocumentationPolicy.class);

        screening.version().documents().clear();
        for (IdentificationDocumentType docType : policy.allowedIDs()) {
            if (Importance.activate().contains(docType.importance().getValue())) {
                // see if we already have it.
                boolean found = false;
                for (IdentificationDocument doc : screening.version().documents()) {
                    if (doc.idType().equals(docType)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    IdentificationDocument doc = EntityFactory.create(IdentificationDocument.class);
                    doc.set(doc.idType(), docType);
                    screening.version().documents().add(doc);
                }
            }
        }
    }

    private void initializeLegalQuestions(CustomerScreening screening, PolicyNode policyNode) {
        LegalQuestionsPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, LegalQuestionsPolicy.class);

        screening.version().legalQuestions().clear();
        if (policy.enabled().getValue(false)) {
            for (LegalQuestionsPolicyItem item : LocalizedContent.selectAllFromList(policy.questions())) {
                CustomerScreeningLegalQuestion question = EntityFactory.create(CustomerScreeningLegalQuestion.class);
                question.question().setValue(item.question().getValue());
                screening.version().legalQuestions().add(question);
            }
        }
    }

    @Override
    public void registerUploadedDocuments(CustomerScreening screening) {
        for (IdentificationDocument document : screening.version().documents()) {
            for (IdentificationDocumentFile applicationDocument : document.files()) {
                FileUploadRegistry.register(applicationDocument.file());
            }
        }
        for (CustomerScreeningIncome income : screening.version().incomes()) {
            for (ProofOfIncomeDocumentFile applicationDocument : income.files()) {
                FileUploadRegistry.register(applicationDocument.file());
            }
        }
        for (CustomerScreeningAsset asset : screening.version().assets()) {
            for (ProofOfAssetDocumentFile applicationDocument : asset.files()) {
                FileUploadRegistry.register(applicationDocument.file());
            }
        }
    }

    @Override
    public CustomerScreening retrivePersonScreeningDraftOrFinal(Customer customerId, AttachLevel attachLevel) {
        EntityQueryCriteria<CustomerScreening> criteria = EntityQueryCriteria.create(CustomerScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customerId));
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        CustomerScreening screening = Persistence.service().retrieve(criteria, attachLevel);
        if (screening == null) {
            criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
            screening = Persistence.service().retrieve(criteria, attachLevel);
        }
        return screening;
    }

    @Override
    public CustomerScreening retriveAndFinalizePersonScreening(Customer customerId, AttachLevel attachLevel) {
        CustomerScreening screening = retrivePersonScreeningDraftOrFinal(customerId, AttachLevel.Attached);
        if (screening != null && screening.getPrimaryKey().isDraft()) {
            screening.saveAction().setValue(SaveAction.saveAsFinal);
            Persistence.service().persist(screening);
        }
        return screening;
    }

    @Override
    public CustomerCreditCheck retrivePersonCreditCheck(Customer customerId) {
        EntityQueryCriteria<CustomerCreditCheck> criteria = EntityQueryCriteria.create(CustomerCreditCheck.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().screening().screene(), customerId));
        criteria.add(PropertyCriterion.ge(criteria.proto().creditCheckDate(), DateUtils.addDays(new Date(), -30)));
        criteria.desc(criteria.proto().creditCheckDate());

        return Persistence.service().retrieve(criteria);
    }

    @Override
    public CustomerCreditCheckLongReportDTO retriveLongReport(CustomerCreditCheck creditCheckId) {
        PmcEquifaxInfo equifaxInfo = getCurrentPmcEquifaxInfo();
        if (!isCreditCheckActivated(equifaxInfo)) {
            throw new UserRuntimeException(i18n.tr("Credit Check interface was not activated in Onboarding"));
        }
        if (equifaxInfo.reportType().getValue() != CreditCheckReportType.FullCreditReport) {
            throw new UserRuntimeException(i18n.tr("Credit Check Full Credit Report was not activated"));
        }
        CustomerCreditCheck ccc = Persistence.service().retrieve(CustomerCreditCheck.class, creditCheckId.getPrimaryKey());
        if (ccc == null) {
            throw new IllegalArgumentException(SimpleMessageFormat.format("CustomerCreditCheck (pk={0}) does not exists", creditCheckId.getPrimaryKey()));
        }
        return updateCustomerCreditCheckLongReport(EquifaxCreditCheck.createLongReport(ccc), ccc);
    }

    private CustomerCreditCheckLongReportDTO updateCustomerCreditCheckLongReport(CustomerCreditCheckLongReportDTO report, CustomerCreditCheck ccc) {

// Do not add non-Equifax data!

//        Persistence.ensureRetrieve(ccc.screening(), AttachLevel.Attached);
//        Persistence.service().retrieveMember(ccc.screening().version().incomes(), AttachLevel.Attached);
//
//        BigDecimal grossMonthlyIncome = new BigDecimal(0);
//        for (CustomerScreeningIncome income : ccc.screening().version().incomes()) {
//            grossMonthlyIncome = grossMonthlyIncome.add(income.details().monthlyAmount().getValue());
//        }
//
//        report.grossMonthlyIncome().setValue(grossMonthlyIncome);
//        report.monthlyIncomeToRentRatio().setValue(grossMonthlyIncome.divide(ccc.amountChecked().getValue(), RoundingMode.DOWN).doubleValue());

        return report;
    }

}