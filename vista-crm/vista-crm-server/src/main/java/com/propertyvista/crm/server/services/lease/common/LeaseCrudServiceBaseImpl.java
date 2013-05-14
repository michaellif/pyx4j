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
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.TenantInsuranceCertificateDTO;

public abstract class LeaseCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractCrudServiceDtoImpl<Lease, DTO> {

    protected LeaseCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto, RetrieveTraget retrieveTraget) {
        enhanceRetrievedCommon(in, dto);

        loadDetachedProducts(dto);

        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(in.unit(), RestrictionsPolicy.class);
        for (LeaseTermTenant item : dto.currentTerm().version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
            fillPreauthorizedPayments(item);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                item.ageOfMajority().setValue((item.role().getValue() != Role.Dependent) ? restrictionsPolicy.ageOfMajority().getValue() : null);
            }
        }

        for (LeaseTermGuarantor item : dto.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                item.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
            }
        }

        fillTenantInsurance(dto);
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO dto) {
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease in, DTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().building());

        if (!dto.currentTerm().isNull()) {
            Persistence.service().retrieve(dto.currentTerm());
            if (dto.currentTerm().version().isNull()) {
                dto.currentTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, dto.currentTerm().getPrimaryKey()));
            }

            Persistence.service().retrieveMember(dto.currentTerm().version().tenants());
            Persistence.service().retrieveMember(dto.currentTerm().version().guarantors());
        }

        Persistence.service().retrieve(dto.billingAccount());
    }

    @Override
    protected void persist(Lease dbo, DTO in) {
        throw new Error("Facade should be used");
    }

    protected void loadDetachedProducts(DTO dto) {
        Persistence.service().retrieve(dto.currentTerm().version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private void fillTenantInsurance(LeaseDTO lease) {
        EntityQueryCriteria<InsuranceCertificate> criteria = new EntityQueryCriteria<InsuranceCertificate>(InsuranceCertificate.class);
        criteria.eq(criteria.proto().tenant().lease(), lease);
        List<InsuranceCertificate> certificates = Persistence.service().query(criteria);

        List<TenantInsuranceCertificateDTO> dtoCertificates = new ArrayList<TenantInsuranceCertificateDTO>();
        // TODO add sorting
        if (certificates != null) {
            for (InsuranceCertificate c : certificates) {
                dtoCertificates.add(c.duplicate(TenantInsuranceCertificateDTO.class));
            }
            lease.tenantInsuranceCertificates().addAll(dtoCertificates);
        }
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();
        item.leaseParticipant().preauthorizedPayments()
                .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(item.leaseParticipant()));
    }
}