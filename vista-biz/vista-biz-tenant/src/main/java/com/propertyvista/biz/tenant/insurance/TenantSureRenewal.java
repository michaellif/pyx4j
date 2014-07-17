/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.CancellationType;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

class TenantSureRenewal {

    private static final Logger log = LoggerFactory.getLogger(TenantSureRenewal.class);

    public void processRenewal(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        processRenewalOffer(executionMonitor, runDate);
        processBuyRenewal(executionMonitor, runDate);
    }

    private void processRenewalOffer(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        LogicalDate renewalAniversary = new LogicalDate(DateUtils.addDays(DateUtils.addYears(runDate, -1), 45)); //45 before year
        log.info("processing TenantSure Renewal offers for inceptionDate before {}", renewalAniversary);

        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.le(criteria.proto().certificate().inceptionDate(), renewalAniversary);
        criteria.eq(criteria.proto().status(), TenantSureStatus.Active);
        criteria.notExists(criteria.proto().renewal());
        ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                final TenantSureInsurancePolicy ts = iterator.next();
                String certificateNumber = ts.certificate().insuranceCertificateNumber().getValue();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() throws RuntimeException {
                            createRenewOffer(ts);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("RenewalOffer", "Participant Id " + ts.client().tenant().participantId().getValue() + "; Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber);
                } catch (Throwable e) {
                    log.error("failed to Renew Offer TenatSure insurance certificate: (#{}) {}", certificateNumber, ts.client().tenant().participantId(), e);
                    executionMonitor.addErredEvent("RenewalOffer", "Participant Id " + ts.client().tenant().participantId().getValue() + "Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber, e);
                }
            }
        } finally {
            iterator.close();
        }
    }

    private void createRenewOffer(TenantSureInsurancePolicy originalInsurancePolicy) {
        log.debug("create Renewal for {} Participant Id {}", originalInsurancePolicy.certificate().insuranceCertificateNumber(), originalInsurancePolicy
                .client().tenant().participantId());

        TenantSureCoverageDTO quotationRequest = restoreCoverage(originalInsurancePolicy);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.addYears(originalInsurancePolicy.certificate().inceptionDate().getValue(), 1)));

        quotationRequest.renewalOfPolicyNumber().setValue(originalInsurancePolicy.certificate().insuranceCertificateNumber().getValue());

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, originalInsurancePolicy.tenant());
        if (!quote.specialQuote().isNull()) {
            log.debug("unable to renew {}", quote.specialQuote());
            return;
        }
        TenantSureInsurancePolicy newTenantSurePolicy = ServerSideFactory.create(TenantSureFacade.class).createDraftPolicy(quote,
                originalInsurancePolicy.tenant());
        newTenantSurePolicy.renewalOf().set(originalInsurancePolicy);
        Persistence.service().persist(newTenantSurePolicy);

        log.info("Renewal for {} created {}", originalInsurancePolicy.certificate().insuranceCertificateNumber(), newTenantSurePolicy.quoteId());

        Persistence.ensureRetrieve(newTenantSurePolicy.tenant(), AttachLevel.Attached);

        ServerSideFactory.create(CommunicationFacade.class).sendTenantSureRenewalEmail(newTenantSurePolicy.tenant().customer().person().email().getValue(),
                newTenantSurePolicy);
    }

    private TenantSureCoverageDTO restoreCoverage(TenantSureInsurancePolicy originalInsurancePolicy) {
        TenantSureCoverageDTO quotationRequest = originalInsurancePolicy.coverage().duplicate(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);

        quotationRequest.contentsCoverage().setValue(originalInsurancePolicy.contentsCoverage().getValue());
        quotationRequest.deductible().setValue(originalInsurancePolicy.deductible().getValue());
        quotationRequest.personalLiabilityCoverage().setValue(originalInsurancePolicy.certificate().liabilityCoverage().getValue());
        return quotationRequest;

    }

    private void processBuyRenewal(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        log.info("processing TenantSure Buy Renewal for inceptionDate before {}", runDate);
        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.le(criteria.proto().certificate().inceptionDate(), runDate);
        criteria.eq(criteria.proto().status(), TenantSureStatus.Draft);
        criteria.eq(criteria.proto().renewalOf().status(), TenantSureStatus.Active);
        ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                final TenantSureInsurancePolicy ts = iterator.next();
                String certificateNumber = ts.certificate().insuranceCertificateNumber().getValue();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() throws RuntimeException {
                            buyRenewalInsurance(ts);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("RenewalBuy", "Participant Id " + ts.client().tenant().participantId().getValue() + "; Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber);
                } catch (Throwable e) {
                    log.error("failed to Buy Renewal of TenatSure insurance certificate: (#{}) {}", certificateNumber, ts.client().tenant().participantId(), e);
                    executionMonitor.addErredEvent("RenewalBuy", "Participant Id " + ts.client().tenant().participantId().getValue() + "Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber, e);
                }
            }
        } finally {
            iterator.close();
        }
    }

    private void buyRenewalInsurance(TenantSureInsurancePolicy insurancePolicy) {
        Persistence.ensureRetrieve(insurancePolicy.renewalOf(), AttachLevel.Attached);
        log.info("buy Renewal for {}, quoteId {} Participant Id {}", insurancePolicy.renewalOf().certificate().insuranceCertificateNumber(),
                insurancePolicy.quoteId(), insurancePolicy.client().tenant().participantId());
        ServerSideFactory.create(TenantSureFacade.class).buyInsurance(insurancePolicy);

        insurancePolicy.renewalOf().status().setValue(TenantSureStatus.Cancelled);
        insurancePolicy.renewalOf().cancellation().setValue(CancellationType.Renewed);
        insurancePolicy.renewalOf().cancellationDate().setValue(SystemDateManager.getLogicalDate());
        insurancePolicy.renewalOf().certificate().expiryDate().setValue(insurancePolicy.certificate().inceptionDate().getValue());
        Persistence.service().persist(insurancePolicy.renewalOf());
    }
}
