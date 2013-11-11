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

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.GeneralInsuranceFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.server.services.financial.PreauthorizedPaymentsCommons;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<Tenant, TenantDTO> implements TenantCrudService {

    private static final Logger log = LoggerFactory.getLogger(TenantCrudServiceImpl.class);

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Tenant bo, TenantDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        to.role().setValue(retrieveTenant(to.leaseTermV(), bo).role().getValue());

        Persistence.service().retrieve(to.customer().emergencyContacts());
        Persistence.service().retrieve(to.lease().unit().building());

        fillPreauthorizedPayments(to, retrieveTarget);
        fillInsuranceCertificates(to);

        if (retrieveTarget == RetrieveTarget.Edit) {
            TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(bo.lease().unit(),
                    TenantInsurancePolicy.class);
            if (insurancePolicy.requireMinimumLiability().isBooleanTrue()) {
                to.minimumRequiredLiability().setValue(insurancePolicy.minimumRequiredLiability().getValue());
            }

            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(bo.lease().unit(),
                    RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                to.ageOfMajority().setValue((to.role().getValue() != Role.Dependent) ? restrictionsPolicy.ageOfMajority().getValue() : null);
            }
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            LeaseTerm leaseTerm = Persistence.service().retrieve(LeaseTerm.class, to.leaseTermV().holder().getPrimaryKey());
            boolean isPotentialTenant = leaseTerm.status().getValue() != LeaseTerm.Status.Current & leaseTerm.status().getValue() != LeaseTerm.Status.Historic;
            to.isPotentialTenant().setValue(isPotentialTenant);
        }

        to.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(bo.lease()));
    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, TenantDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(dto.lease().unit().building());
        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());
    }

    @Override
    protected void persist(Tenant tenant, TenantDTO tenantDto) {
        super.persist(tenant, tenantDto);

        savePreauthorizedPayments(tenantDto);
        updateInsuranceCertificates(tenantDto);
    }

    private LeaseTermTenant retrieveTenant(LeaseTerm.LeaseTermV termV, Tenant leaseCustomer) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public void createPreauthorizedPayment(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId) {
        callback.onSuccess(PreauthorizedPaymentsCommons.createNewPreauthorizedPayment(tenantId));
    }

    @Override
    public void getPortalAccessInformation(AsyncCallback<TenantPortalAccessInformationDTO> callback, Tenant tenantId) {
        Tenant tenant = Persistence.secureRetrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieveMember(tenant.lease());
        Persistence.service().retrieveMember(tenant.lease().unit().building());
        TenantPortalAccessInformationDTO dto = ExportTenantsPortalSecretsDeferredProcess.convert(tenant);
        callback.onSuccess(dto);
    }

    private void fillPreauthorizedPayments(TenantDTO dto, RetrieveTarget retrieveTarget) {
        dto.preauthorizedPayments().addAll(
                PreauthorizedPaymentsCommons.createPreauthorizedPayments(EntityFactory.createIdentityStub(Tenant.class, dto.id().getValue()), retrieveTarget));
        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(dto.lease()));
    }

    private void savePreauthorizedPayments(TenantDTO dto) {
        PreauthorizedPaymentsCommons
                .savePreauthorizedPayments(dto.preauthorizedPayments(), EntityFactory.createIdentityStub(Tenant.class, dto.id().getValue()));
    }

    private void fillInsuranceCertificates(TenantDTO dto) {
        dto.insuranceCertificates().addAll(
                ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(
                        EntityFactory.createIdentityStub(Tenant.class, dto.getPrimaryKey()), true));
    }

    private void updateInsuranceCertificates(TenantDTO tenantDto) {
        List<InsuranceCertificate<?>> oldInsuranceCertificates = ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(
                EntityFactory.createIdentityStub(Tenant.class, tenantDto.getPrimaryKey()), true);

        @SuppressWarnings("unchecked")
        Collection<InsuranceCertificate<?>> deletedInsuranceCertificates = CollectionUtils
                .subtract(oldInsuranceCertificates, tenantDto.insuranceCertificates());

        for (InsuranceCertificate<?> insuranceCertificate : tenantDto.insuranceCertificates()) {
            // skip certificates that cannot be updated by pmc
            if ((insuranceCertificate instanceof PropertyVistaIntegratedInsurance) || insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                log.debug("skip update of ManagedByTenant Certificate", insuranceCertificate);
                continue;
            }
            if (insuranceCertificate.getPrimaryKey() == null && (insuranceCertificate instanceof GeneralInsuranceCertificate)) {
                ServerSideFactory.create(GeneralInsuranceFacade.class).createGeneralTenantInsurance(
                        EntityFactory.createIdentityStub(Tenant.class, tenantDto.getPrimaryKey()), (GeneralInsuranceCertificate) insuranceCertificate);
            } else {
                // check that nobody is tampering the PV/Tenant managed insurance certificates (we have to validate the type of the data based on pk from our db and don't rely on flags from outside)
                InsuranceCertificate<?> oldInsuranceCertificate = Persistence.service().retrieve(InsuranceCertificate.class,
                        insuranceCertificate.getPrimaryKey());
                if (!(oldInsuranceCertificate instanceof PropertyVistaIntegratedInsurance) || !oldInsuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                    Persistence.secureSave(insuranceCertificate);
                } else {
                    log.debug("skip update of ManagedByTenant Certificate", insuranceCertificate);
                }
            }

        }

        for (InsuranceCertificate<?> deletedCertificate : deletedInsuranceCertificates) {
            if ((deletedCertificate instanceof PropertyVistaIntegratedInsurance) || deletedCertificate.isManagedByTenant().isBooleanTrue()) {
                throw new SecurityViolationException("it's forbidden to delete property vista integrated or user managed insurance certificates");
            }
            ServerSideFactory.create(GeneralInsuranceFacade.class).deleteGeneralInsurance((GeneralInsuranceCertificate) deletedCertificate.cast());
        }
    }
}
