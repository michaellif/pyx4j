/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2013
 * @author michaellif
 */
package com.propertyvista.portal.server.portal.resident.services.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.GeneralInsuranceFacade;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class GeneralInsurancePolicyCrudServiceImpl extends AbstractCrudServiceDtoImpl<GeneralInsurancePolicy, GeneralInsurancePolicyDTO> implements
        GeneralInsurancePolicyCrudService {

    public GeneralInsurancePolicyCrudServiceImpl() {
        super(GeneralInsurancePolicy.class, GeneralInsurancePolicyDTO.class);
    }

    @Override
    public void bind() {
        bindCompleteObject();
    }

    @Override
    public GeneralInsurancePolicyDTO init(InitializationData initializationData) {
        GeneralInsurancePolicyDTO dto = super.init(initializationData);
        populateMinLiability(dto);
        return dto;
    }

    @Override
    protected void create(GeneralInsurancePolicy bo, GeneralInsurancePolicyDTO to) {
        ServerSideFactory.create(GeneralInsuranceFacade.class).createGeneralTenantInsurance(ResidentPortalContext.getTenant(), to.certificate());
        // We now have Insurance
        if (!SecurityController.check(PortalResidentBehavior.InsurancePresent)) {
            ServerContext.getVisit().setAclRevalidationRequired();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        ServerSideFactory.create(GeneralInsuranceFacade.class).deleteGeneralInsurance(
                EntityFactory.createIdentityStub(GeneralInsuranceCertificate.class, entityId));
        Persistence.service().commit();
        callback.onSuccess(true);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<GeneralInsurancePolicyDTO>> callback, EntityListCriteria<GeneralInsurancePolicyDTO> dtoCriteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean save(GeneralInsurancePolicy bo, GeneralInsurancePolicyDTO to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enhanceRetrieved(GeneralInsurancePolicy bo, GeneralInsurancePolicyDTO to, RetrieveTarget retrieveTarget) {
        populateMinLiability(to);
    }

    private void populateMinLiability(GeneralInsurancePolicyDTO to) {
        Lease lease = Persistence.service().retrieve(Lease.class, ResidentPortalContext.getLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), TenantInsurancePolicy.class);
        to.minLiability().setValue(insurancePolicy.requireMinimumLiability().getValue(false) ? insurancePolicy.minimumRequiredLiability().getValue() : null);
    }
}
