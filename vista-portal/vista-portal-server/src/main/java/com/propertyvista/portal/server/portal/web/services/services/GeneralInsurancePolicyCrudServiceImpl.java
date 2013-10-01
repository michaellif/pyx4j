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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.services;



import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class GeneralInsurancePolicyCrudServiceImpl extends AbstractCrudServiceDtoImpl<GeneralInsuranceCertificate, GeneralInsurancePolicyDTO> implements
             GeneralInsurancePolicyCrudService {

    public GeneralInsurancePolicyCrudServiceImpl() {
        super(GeneralInsuranceCertificate.class, GeneralInsurancePolicyDTO.class);
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
    public void enhanceRetrieved(GeneralInsuranceCertificate bo, GeneralInsurancePolicyDTO to, RetrieveTarget retrieveTarget) {
        populateMinLiability(to);
    }

    private void populateMinLiability(GeneralInsurancePolicyDTO to) {
        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), TenantInsurancePolicy.class);
        to.minLiability().setValue(insurancePolicy.requireMinimumLiability().isBooleanTrue() ? insurancePolicy.minimumRequiredLiability().getValue() : null);

    }
}
