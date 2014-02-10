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
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;

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
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFile;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFolder;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;
import com.propertyvista.dto.LeaseApprovalDTO.SuggestedDecision;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.equifax.EquifaxCreditCheck;
import com.propertyvista.server.TaskRunner;

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
        return getCurrentPmcEquifaxInfo().status().getValue();
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
            leaseApproval.suggestedDecision().setValue(SuggestedDecision.ManualReview);
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
            BigDecimal percenrtageApproved = amountApproved.divide(rentAmount, RoundingMode.DOWN);
            if (percenrtageApproved.compareTo(BigDecimal.ONE) > 0) {
                percenrtageApproved = BigDecimal.ONE;
            }
            leaseApproval.percenrtageApproved().setValue(percenrtageApproved);

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
    public void runCreditCheck(BigDecimal rentAmount, LeaseTermParticipant<?> leaseParticipantId, Employee currentUserEmployee) {
        PmcEquifaxInfo equifaxInfo = getCurrentPmcEquifaxInfo();
        if (!isCreditCheckActivated(equifaxInfo)) {
            throw new UserRuntimeException(i18n.tr("Credit Check interface was not activated in Onboarding"));
        }

        LeaseTermParticipant<?> leaseParticipant = (LeaseTermParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());
        CustomerScreening screening = retrivePersonScreening(leaseParticipant.leaseParticipant().customer());

        CustomerCreditCheck pcc = EntityFactory.create(CustomerCreditCheck.class);
        pcc.amountChecked().setValue(rentAmount);
        pcc.screening().set(screening);
        pcc.createdBy().set(currentUserEmployee);

        Persistence.service().retrieve(leaseParticipant.leaseTermV());
        Persistence.service().retrieve(leaseParticipant.leaseTermV().holder().lease());

        BackgroundCheckPolicy backgroundCheckPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                leaseParticipant.leaseTermV().holder().lease().unit(), BackgroundCheckPolicy.class);
        pcc.backgroundCheckPolicy().set(EntityGraph.businessDuplicate(backgroundCheckPolicy.version()));

        Persistence.service().retrieve(screening.version().incomes());
        Persistence.service().retrieve(screening.version().assets());
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);

        pcc.transactionId().setValue(ScreeningPayments.preAuthorization(equifaxInfo));

        // Need this for simulations
        Lease lease = leaseParticipant.leaseTermV().holder().lease();
        lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());

        boolean success = false;
        try {
            pcc = EquifaxCreditCheck.runCreditCheck(equifaxInfo, leaseParticipant.leaseParticipant().customer(), pcc, backgroundCheckPolicy.strategyNumber()
                    .getValue(), lease, leaseParticipant);

            // This is the business, we charge only when riskCode is returned
            success = !pcc.riskCode().isNull();

            Persistence.service().persist(pcc);

            Persistence.service().commit();
        } finally {
            if (!pcc.transactionId().isNull()) {
                if (success) {
                    ScreeningPayments.compleateTransaction(pcc.transactionId().getValue());
                } else {
                    ScreeningPayments.preAuthorizationReversal(pcc.transactionId().getValue());
                }
            }
        }

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
    public CustomerScreening retrivePersonScreeningDraftForEdit(Customer customerId) {
        CustomerScreening screeningId = retrivePersonScreeningDraftOrFinal(customerId, AttachLevel.IdOnly);
        if (screeningId == null) {
            CustomerScreening screening = EntityFactory.create(CustomerScreening.class);
            screening.screene().set(customerId);
            return screening;
        } else {
            return Persistence.retrieveDraftForEdit(CustomerScreening.class, screeningId.getPrimaryKey());
        }
    }

    @Override
    public void registerUploadedDocuments(CustomerScreening screening) {
        for (IdentificationDocumentFolder document : screening.version().documents()) {
            for (IdentificationDocumentFile applicationDocument : document.files()) {
                FileUploadRegistry.register(applicationDocument.file());
            }
        }
        for (CustomerScreeningIncome income : screening.version().incomes()) {
            for (ProofOfEmploymentDocumentFolder document : income.documents()) {
                for (ProofOfEmploymentDocumentFile applicationDocument : document.files()) {
                    FileUploadRegistry.register(applicationDocument.file());
                }
            }
        }
    }

    @Override
    public CustomerScreening retrivePersonScreeningDraftOrFinal(Customer customerId, AttachLevel attachLevel) {
        EntityQueryCriteria<CustomerScreening> criteria = EntityQueryCriteria.create(CustomerScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customerId));
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        CustomerScreening screening = Persistence.service().retrieve(criteria, attachLevel);
        if (screening != null) {
            return screening;
        }
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        screening = Persistence.service().retrieve(criteria, attachLevel);
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
    public CustomerCreditCheckLongReportDTO retriveLongReport(Customer customerId) {
        PmcEquifaxInfo equifaxInfo = getCurrentPmcEquifaxInfo();
        if (!isCreditCheckActivated(equifaxInfo)) {
            throw new UserRuntimeException(i18n.tr("Credit Check interface was not activated in Onboarding"));
        }
        if (equifaxInfo.reportType().getValue() != CreditCheckReportType.FullCreditReport) {
            throw new UserRuntimeException(i18n.tr("Credit Check Full Credit Report was not activated"));
        }
        CustomerCreditCheck ccc = retrivePersonCreditCheck(customerId);
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