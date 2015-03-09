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
 */
package com.propertyvista.crm.server.services.customer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.GeneralInsuranceFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.server.services.financial.PreauthorizedPaymentsCommons;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Lease;
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

        to.role().setValue(retrieveTenantRole(to.leaseTermV(), bo));

        Persistence.service().retrieve(to.customer().emergencyContacts());
        Persistence.service().retrieve(to.lease().unit().building());

        fillPreauthorizedPayments(to, retrieveTarget);
        fillInsuranceCertificates(to);

        PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(bo.lease());

        TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, TenantInsurancePolicy.class);
        if (insurancePolicy.requireMinimumLiability().getValue(false)) {
            to.minimumRequiredLiability().setValue(insurancePolicy.minimumRequiredLiability().getValue());
        }

        if (retrieveTarget == RetrieveTarget.Edit) {
            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().getValue(false)) {
                to.ageOfMajority().setValue((to.role().getValue() != Role.Dependent) ? restrictionsPolicy.ageOfMajority().getValue() : null);
            }
            to.emergencyContactsIsMandatory().setValue(restrictionsPolicy.emergencyContactsIsMandatory().getValue(false));
            to.emergencyContactsNumberRequired().setValue(restrictionsPolicy.emergencyContactsNumber().getValue());
        } else {
            to.emergencyContactsIsMandatory().setValue(false);
            to.emergencyContactsNumberRequired().setValue(1);
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
        dto.role().setValue(retrieveTenantRole(dto.leaseTermV(), entity));
    }

    @Override
    protected boolean persist(Tenant tenant, TenantDTO tenantDto) {
        super.persist(tenant, tenantDto);

        savePreauthorizedPayments(tenantDto);
        updateInsuranceCertificates(tenantDto);

        return true;
    }

    private LeaseTermTenant retrieveTenant(LeaseTerm.LeaseTermV termV, Tenant leaseCustomer) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));

        return Persistence.service().retrieve(criteria);
    }

    private Role retrieveTenantRole(LeaseTerm.LeaseTermV termV, Tenant tenant) {
        LeaseTermTenant ltt = retrieveTenant(termV, tenant);
        if (ltt == null) {
            log.debug("Can't find Tenant {} in leaseTem {} !?!", tenant, termV);
            return null;
        }
        return ltt.role().getValue();
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
        TenantPortalAccessInformationDTO dto = ExportTenantsSecurityCodesDeferredProcess.convert(tenant);
        callback.onSuccess(dto);
    }

    private void fillPreauthorizedPayments(TenantDTO dto, RetrieveTarget retrieveTarget) {
        dto.preauthorizedPayments().addAll(
                PreauthorizedPaymentsCommons.createPreauthorizedPayments(EntityFactory.createIdentityStub(Tenant.class, dto.id().getValue()), retrieveTarget));
        if (Lease.Status.isApplicationUnitSelected(dto.lease())) {
            dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(dto.lease()));
            dto.nextAutopayApplicabilityMessage().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayApplicabilityMessage(dto.lease()));
        }
    }

    private void savePreauthorizedPayments(TenantDTO dto) {
        // remove PAPs with no corresponding Payment Method (removed during edit session):
        Iterator<PreauthorizedPaymentDTO> it = dto.preauthorizedPayments().iterator();
        while (it.hasNext()) {
            PreauthorizedPaymentDTO papDTO = it.next();
            LeasePaymentMethod lpm = Persistence.service().retrieve(LeasePaymentMethod.class, papDTO.paymentMethod().getPrimaryKey());
            if (lpm == null || lpm.isDeleted().getValue(false)) {
                it.remove();
            }
        }

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
            if ((insuranceCertificate instanceof PropertyVistaIntegratedInsurance) || insuranceCertificate.isManagedByTenant().getValue(false)) {
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
                if (!(oldInsuranceCertificate instanceof PropertyVistaIntegratedInsurance) || !oldInsuranceCertificate.isManagedByTenant().getValue(false)) {
                    Persistence.secureSave(insuranceCertificate);
                } else {
                    log.debug("skip update of ManagedByTenant Certificate", insuranceCertificate);
                }
            }

        }

        for (InsuranceCertificate<?> deletedCertificate : deletedInsuranceCertificates) {
            if ((deletedCertificate instanceof PropertyVistaIntegratedInsurance) || deletedCertificate.isManagedByTenant().getValue(false)) {
                throw new SecurityViolationException("it's forbidden to delete property vista integrated or user managed insurance certificates");
            }
            ServerSideFactory.create(GeneralInsuranceFacade.class).deleteGeneralInsurance((GeneralInsuranceCertificate) deletedCertificate.cast());
        }
    }
}
