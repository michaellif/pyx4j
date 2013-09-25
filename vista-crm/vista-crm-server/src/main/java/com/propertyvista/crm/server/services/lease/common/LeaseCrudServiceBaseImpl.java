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
    protected void enhanceRetrieved(Lease in, DTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.unit().building());

        if (!to.currentTerm().isNull()) {
            Persistence.service().retrieve(to.currentTerm());
            if (to.currentTerm().version().isNull()) {
                to.currentTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, to.currentTerm().getPrimaryKey()));
            }

            Persistence.service().retrieveMember(to.currentTerm().version().tenants());
            Persistence.service().retrieveMember(to.currentTerm().version().guarantors());
        }

        loadDetachedProducts(to);

        for (LeaseTermTenant item : to.currentTerm().version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
            fillPreauthorizedPayments(item);
        }

        for (LeaseTermGuarantor item : to.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        fillTenantInsurance(to);

        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(to.unit(), RestrictionsPolicy.class);
        if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
            to.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO dto) {
        Persistence.service().retrieve(dto.unit().building());
        Persistence.service().retrieve(dto._applicant());
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