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
 */
package com.propertyvista.crm.server.services.lease.common;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractCrudServiceDtoImpl<Lease, DTO> {

    protected LeaseCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(Lease bo, DTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.unit().building());

        loadCurrentTerm(to);
        loadDetachedProducts(to);

        for (LeaseTermTenant item : to.currentTerm().version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers, false);
            fillPreauthorizedPayments(item);
            loadTenantInsurance(to, item);
        }
        if (Lease.Status.isApplicationUnitSelected(to)) {
            to.nextAutopayApplicabilityMessage().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayApplicabilityMessage(bo));
        }

        for (LeaseTermGuarantor item : to.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers, false);
        }
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

    private void loadDetachedProducts(DTO dto) {
        Persistence.service().retrieve(dto.currentTerm().version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private void loadTenantInsurance(LeaseDTO lease, LeaseTermTenant tenant) {
        Tenant tenantId = tenant.leaseParticipant().<Tenant> createIdentityStub();
        lease.tenantInsuranceCertificates().addAll(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(tenantId, true));

        for (InsuranceCertificate<?> certificate : lease.tenantInsuranceCertificates()) {
            Persistence.ensureRetrieve(certificate.insurancePolicy().tenant().customer().person().name(), AttachLevel.ToStringMembers);
        }
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();
        item.leaseParticipant().preauthorizedPayments()
                .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(item.leaseParticipant()));
    }
}