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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantInsuranceCertificateDTO;
import com.propertyvista.server.common.security.VistaContext;

public class TenantCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<Tenant, TenantDTO> implements TenantCrudService {

    private static final Logger log = LoggerFactory.getLogger(TenantCrudServiceImpl.class);

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

        EntityQueryCriteria<InsuranceCertificate> oldInsuranceCertificatesCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        oldInsuranceCertificatesCriteria.eq(oldInsuranceCertificatesCriteria.proto().tenant(), tenant.getPrimaryKey());
        List<InsuranceCertificate> oldInsuranceCertificates = Persistence.service().query(oldInsuranceCertificatesCriteria);
        List<InsuranceCertificate> deletedInsuranceCertificates = new ArrayList<InsuranceCertificate>();

        for (InsuranceCertificate oldCertificate : oldInsuranceCertificates) {
            boolean isDeleted = true;
            found: for (TenantInsuranceCertificateDTO newCertificate : tenantDto.insuranceCertificates()) {
                if (oldCertificate.getPrimaryKey().equals(newCertificate.getPrimaryKey())) {
                    isDeleted = false;
                    break found;
                }
            }
            if (isDeleted) {
                deletedInsuranceCertificates.add(oldCertificate);
            }
        }
        // TODO support delete insurance
        for (TenantInsuranceCertificateDTO insuranceCertificate : tenantDto.insuranceCertificates()) {
            if (insuranceCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue() | insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                // property vista integrated insurance certificates should be managed by software, i.e. the users have no right to manually modify such insurance certificates
                // tenant is managed by tenant alos are managed by tenant... so no pmc can touch it

                continue;
            }
            // we use insurance generic for those insurances that are managed by pmc
            InsuranceGeneric insuranceGeneric = insuranceCertificate.duplicate(InsuranceCertificate.class).duplicate(InsuranceGeneric.class);
            // workaround since 'duplicate' seems not to do this
            for (InsuranceCertificateDocument document : insuranceGeneric.documents()) {
                document.owner().set(insuranceGeneric);
            }

            if (insuranceCertificate.getPrimaryKey() == null) {
                // This is new                               
                insuranceGeneric.tenant().set(tenant.createIdentityStub());
            } else {
                // check that nobody is tampering the PV/Tenant managed insurance certificates:
                InsuranceCertificate preUpdated = Persistence.service().retrieve(InsuranceCertificate.class, insuranceCertificate.getPrimaryKey());
                if (preUpdated.isPropertyVistaIntegratedProvider().isBooleanTrue() | insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                    log.warn(SimpleMessageFormat.format("Evil CRM user {0} has tried to override insurance settting for insurance id={1}", VistaContext
                            .getCurrentUser().getPrimaryKey(), preUpdated.getPrimaryKey()));
                    throw new Error();
                }
            }
            Persistence.secureSave(insuranceGeneric);
        }

        for (InsuranceCertificate deletedCertificate : deletedInsuranceCertificates) {
            if (deletedCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue() | deletedCertificate.isManagedByTenant().isBooleanTrue()) {
                throw new SecurityViolationException("it's forbidden to delete property vista integrated or user managed insurance certificates");
            }
            Persistence.service().delete(deletedCertificate.getInstanceValueClass(), deletedCertificate.getPrimaryKey());
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

    private List<TenantInsuranceCertificateDTO> retrieveInsuranceCertificates(Tenant tenantId) {
        EntityQueryCriteria<InsuranceCertificate> tenantInsuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        tenantInsuranceCriteria.eq(tenantInsuranceCriteria.proto().tenant(), tenantId);
        tenantInsuranceCriteria.desc(tenantInsuranceCriteria.proto().inceptionDate());
        List<InsuranceCertificate> certificates = Persistence.service().query(tenantInsuranceCriteria);
        List<TenantInsuranceCertificateDTO> dtoCertificates = new ArrayList<TenantInsuranceCertificateDTO>();
        for (InsuranceCertificate c : certificates) {
            dtoCertificates.add(c.duplicate(TenantInsuranceCertificateDTO.class));
        }
        return dtoCertificates;
    }
}
