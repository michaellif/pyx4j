/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantDTO;

public class TenantCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<LeaseTermTenant, Tenant, TenantDTO> implements TenantCrudService {

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Tenant entity, TenantDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);

        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());

        Persistence.service().retrieve(dto.customer().emergencyContacts());

        // mark pre-authorized one:
        for (LeasePaymentMethod paymentMethod : dto.paymentMethods()) {
            if (paymentMethod.equals(entity.preauthorizedPayment())) {
                paymentMethod.isPreauthorized().setValue(Boolean.TRUE);
                break;
            }
        }

        dto.insuranceCertificates().addAll(retrieveInsuranceCertificates(entity));
        // unattach tenant related information since we don't want to send again
        for (InsuranceCertificate insuranceCertificate : dto.insuranceCertificates()) {
            insuranceCertificate.tenant().set(insuranceCertificate.tenant().createIdentityStub());
        }

        TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(entity.lease().unit(),
                TenantInsurancePolicy.class);
        if (insurancePolicy.requireMinimumLiability().isBooleanTrue()) {
            dto.minimumRequiredLiability().setValue(insurancePolicy.minimumRequiredLiability().getValue());
        }
    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, TenantDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());

    }

    @Override
    protected void persist(Tenant tenant, TenantDTO tenantDto) {
        super.persist(tenant, tenantDto);

        // memorize pre-authorized method:
        for (LeasePaymentMethod paymentMethod : tenantDto.paymentMethods()) {
            if (paymentMethod.isPreauthorized().isBooleanTrue()) {
                if (!paymentMethod.equals(tenant.preauthorizedPayment())) {
                    tenant.preauthorizedPayment().set(paymentMethod);
                    Persistence.service().merge(tenant);
                    break;
                }
            }
        }

        for (InsuranceCertificate insuranceCertificate : tenantDto.insuranceCertificates()) {
            if (insuranceCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue()) {
                // property vista integrated insurance certificates should be managed by software, i.e. the users have no right to manually modify such insurance certificates 
                continue;
            }
            if (insuranceCertificate.getPrimaryKey() == null) {
                insuranceCertificate.tenant().set(tenant);
                Persistence.service().merge(insuranceCertificate);
            }
        }
    }

    @Override
    public void getAssosiatedTenant(AsyncCallback<Tenant> callback, Key entityId) {
        callback.onSuccess(Persistence.service().retrieve(Tenant.class, entityId));
    }

    private LeaseTermTenant retrieveTenant(LeaseTerm.LeaseTermV termV, Tenant leaseCustomer) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));
        return Persistence.service().retrieve(criteria);
    }

    private List<InsuranceCertificate> retrieveInsuranceCertificates(Tenant tenantId) {
        EntityQueryCriteria<InsuranceCertificate> tenantInsuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        tenantInsuranceCriteria.eq(tenantInsuranceCriteria.proto().tenant(), tenantId);
        tenantInsuranceCriteria.desc(tenantInsuranceCriteria.proto().inceptionDate());
        return Persistence.service().query(tenantInsuranceCriteria);
    }
}
