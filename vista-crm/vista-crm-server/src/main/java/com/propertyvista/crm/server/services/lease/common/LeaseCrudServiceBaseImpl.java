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

import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.legal.LeaseLegalFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LegalStatusDTO;

public abstract class LeaseCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractCrudServiceDtoImpl<Lease, DTO> {

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
            loadTenantInsurance(to, item);
        }

        for (LeaseTermGuarantor item : to.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers, false);
        }

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
    protected boolean persist(Lease dbo, DTO in) {
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
    }

    private void loadTenantInsurance(LeaseDTO lease, LeaseTermTenant tenant) {
        Tenant tenantId = tenant.leaseParticipant().<Tenant> createIdentityStub();
        lease.tenantInsuranceCertificates().addAll(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(tenantId, false));
    }

    private void loadCommunicationLetters(LeaseDTO lease) {
        EntityQueryCriteria<LegalLetter> criteria = EntityQueryCriteria.create(LegalLetter.class);
        criteria.eq(criteria.proto().lease(), lease.getPrimaryKey());
        criteria.asc(criteria.proto().generatedOn());
        lease.letters().addAll(Persistence.service().query(criteria));
    }

    private void loadLegalStatus(LeaseDTO lease) {
        List<LegalStatus> legalStatuses = ServerSideFactory.create(LeaseLegalFacade.class).getLegalStatusHistory(
                EntityFactory.createIdentityStub(Lease.class, lease.getPrimaryKey()));
        for (LegalStatus status : legalStatuses) {
            LegalStatusDTO dto = status.duplicate(LegalStatusDTO.class);

            EntityQueryCriteria<LegalLetter> criteria = EntityQueryCriteria.create(LegalLetter.class);
            criteria.eq(criteria.proto().lease(), lease.getPrimaryKey());
            criteria.eq(criteria.proto().status(), status);
            dto.letters().addAll(Persistence.service().query(criteria));

            lease.legalStatusHistory().add(dto);
        }

        if (!lease.legalStatusHistory().isEmpty()) {
            LegalStatus current = lease.legalStatusHistory().get(0);
            if (current.status().getValue() != LegalStatus.Status.None) {
                lease.currentLegalStatus().setValue(
                        SimpleMessageFormat.format("{0} ({1})", current.status().getValue().toString(), current.details().getValue()));
            }
        }
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();
        item.leaseParticipant().preauthorizedPayments()
                .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(item.leaseParticipant()));
    }
}