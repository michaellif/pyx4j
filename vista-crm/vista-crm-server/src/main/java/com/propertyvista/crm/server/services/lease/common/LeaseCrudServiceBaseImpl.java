/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.legal.LeaseLegalFacade;
import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStackholderSigningProgressDTO;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractCrudServiceDtoImpl<Lease, DTO> {

    private static final I18n i18n = I18n.get(LeaseCrudServiceBaseImpl.class);

    protected LeaseCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.unit().building());

        loadCurrentTerm(to);
        loadDetachedProducts(to);

        for (LeaseTermTenant item : to.currentTerm().version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers, false);
            fillPreauthorizedPayments(item);
        }

        for (LeaseTermGuarantor item : to.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers, false);
        }

        loadTenantInsurance(to);
        loadCommunicationLetters(to);
        loadLegalStatus(to);
        // TODO loadLeaseAgreementSigningProgress(to);
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO to) {
        Persistence.service().retrieve(to.unit().building());
        Persistence.service().retrieve(to._applicant());

        loadCurrentTerm(to);
    }

    @Override
    protected void persist(Lease dbo, DTO in) {
        throw new Error("Facade should be used");
    }

    /**
     * override in descendants to implement appropriate term loading procedure (for Lease/Application)
     * 
     * @param to
     */
    protected abstract void loadCurrentTerm(DTO to);

    protected void loadDetachedProducts(DTO dto) {
        Persistence.service().retrieve(dto.currentTerm().version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }

        EntityQueryCriteria<LeaseTermAgreementDocument> agreementDocumentCriteria = EntityQueryCriteria.create(LeaseTermAgreementDocument.class);
        agreementDocumentCriteria.eq(agreementDocumentCriteria.proto().leaseTermV(), dto.currentTerm().version());

        dto.currentTerm().version().agreementDocument().set(Persistence.service().retrieve(agreementDocumentCriteria));
    }

    private void loadTenantInsurance(LeaseDTO lease) {
        Tenant tenantId = lease.currentTerm().version().tenants().get(0).leaseParticipant().<Tenant> createIdentityStub();
        lease.tenantInsuranceCertificates().addAll(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(tenantId, false));
    }

    private void loadCommunicationLetters(LeaseDTO lease) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, lease.getPrimaryKey());
        Map<Lease, List<N4LegalLetter>> n4s = ServerSideFactory.create(N4ManagementFacade.class).getN4(Arrays.asList(leaseId), null);
        lease.letters().addAll(n4s.get(leaseId));
    }

    private void loadLegalStatus(LeaseDTO lease) {
        lease.legalStatusHistory().addAll(
                ServerSideFactory.create(LeaseLegalFacade.class).getLegalStatusHistory(EntityFactory.createIdentityStub(Lease.class, lease.getPrimaryKey())));
        if (!lease.legalStatusHistory().isEmpty()) {
            LegalStatus current = lease.legalStatusHistory().get(0);
            if (current.status().getValue() != LegalStatus.Status.None) {
                lease.currentLegalStatus().setValue(
                        SimpleMessageFormat.format("{0} ({1})", current.status().getValue().toString(), current.details().getValue()));
            }
        }
    }

    private void loadLeaseAgreementSigningProgress(DTO to) {
        List<LeaseTermParticipant<?>> stakeholderParticipants = getStakeholderParticipants(to.currentTerm());

        LeaseAgreementSigningProgressDTO progress = EntityFactory.create(LeaseAgreementSigningProgressDTO.class);

        for (LeaseTermParticipant<?> participant : stakeholderParticipants) {
            LeaseAgreementStackholderSigningProgressDTO stakeholdersProgress = EntityFactory.create(LeaseAgreementStackholderSigningProgressDTO.class);
            stakeholdersProgress.name().setValue(participant.leaseParticipant().customer().person().name().getStringView());

            boolean hasSigned = true;
            Iterator<LeaseAgreementLegalTerm> legalTerms = to.currentTerm().version().agreementLegalTerms().iterator();

            while (hasSigned && legalTerms.hasNext()) {
                LeaseAgreementLegalTerm legalTerm = legalTerms.next();
                if (legalTerm.signatureFormat().getValue() != SignatureFormat.None) {

                }
            }

            progress.stackholdersProgressBreakdown().add(stakeholdersProgress);
        }

    }

    private List<LeaseTermParticipant<?>> getStakeholderParticipants(LeaseTerm leaseTerm) {
        List<LeaseTermParticipant<?>> stakeholderParticipants = new ArrayList<>();
        stakeholderParticipants.addAll(leaseTerm.version().tenants());
        stakeholderParticipants.addAll(leaseTerm.version().guarantors());
        Iterator<LeaseTermParticipant<?>> i = stakeholderParticipants.iterator();
        while (i.hasNext()) {
            if (!shouldSign(i.next())) {
                i.remove();
            }
        }
        return stakeholderParticipants;
    }

    private boolean shouldSign(LeaseTermParticipant<?> participant) {
        return false;
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();
        item.leaseParticipant().preauthorizedPayments()
                .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(item.leaseParticipant()));
    }
}