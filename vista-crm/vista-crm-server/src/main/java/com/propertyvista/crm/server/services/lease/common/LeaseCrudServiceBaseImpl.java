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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.legal.N4LegalLetter;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
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
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
            fillPreauthorizedPayments(item);
        }

        for (LeaseTermGuarantor item : to.currentTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        loadTenantInsurance(to);
        loadRestrictions(to);
        loadCommunicationLetters(to);
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

    protected void loadCurrentTerm(DTO to) {
        if (!to.currentTerm().isNull()) {
            Persistence.service().retrieve(to.currentTerm());
            if (to.currentTerm().version().isNull()) {
                to.currentTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, to.currentTerm().getPrimaryKey()));
            }

            Persistence.service().retrieveMember(to.currentTerm().version().tenants());
            Persistence.service().retrieveMember(to.currentTerm().version().guarantors());
        }
    }

    protected void loadDetachedProducts(DTO dto) {
        Persistence.service().retrieve(dto.currentTerm().version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private void loadTenantInsurance(LeaseDTO lease) {
        Tenant tenantId = lease.currentTerm().version().tenants().get(0).leaseParticipant().<Tenant> createIdentityStub();
        lease.tenantInsuranceCertificates().addAll(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(tenantId, false));
    }

    private void loadRestrictions(LeaseDTO lease) {
        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), RestrictionsPolicy.class);
        if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
            lease.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
        }
    }

    private void loadCommunicationLetters(LeaseDTO lease) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, lease.getPrimaryKey());
        Map<Lease, List<N4LegalLetter>> n4s = ServerSideFactory.create(N4ManagementFacade.class).getN4(Arrays.asList(leaseId), null);
        lease.letters().addAll(n4s.get(leaseId));

        if (!n4s.get(leaseId).isEmpty()) {
            lease.legalStatus().setValue(i18n.tr("{0} N4''s issued", n4s.get(leaseId).size()));
        }
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();
        item.leaseParticipant().preauthorizedPayments()
                .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(item.leaseParticipant()));
    }
}