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
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsuranceCertificateDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsuranceCertificateCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class GeneralInsuranceCertificateCrudServiceImpl extends AbstractCrudServiceDtoImpl<InsuranceGeneric, GeneralInsuranceCertificateDTO> implements
        GeneralInsuranceCertificateCrudService {

    public GeneralInsuranceCertificateCrudServiceImpl() {
        super(InsuranceGeneric.class, GeneralInsuranceCertificateDTO.class);
    }

    @Override
    public void bind() {
        bind(toProto.insuranceProvider(), boProto.insuranceProvider());
        bind(toProto.insuranceCertificateNumber(), boProto.insuranceCertificateNumber());
        bind(toProto.liabilityCoverage(), boProto.liabilityCoverage());
        bind(toProto.inceptionDate(), boProto.inceptionDate());
        bind(toProto.expiryDate(), boProto.expiryDate());
    }

    @Override
    public GeneralInsuranceCertificateDTO init(InitializationData initializationData) {
        GeneralInsuranceCertificateDTO dto = super.init(initializationData);
        populateMinLiability(dto);
        return dto;
    }

    @Override
    public void enhanceRetrieved(InsuranceGeneric bo, GeneralInsuranceCertificateDTO to, RetrieveTarget retrieveTarget) {
        populateMinLiability(to);
    }

    private void populateMinLiability(GeneralInsuranceCertificateDTO to) {
        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), TenantInsurancePolicy.class);
        to.minLiability().setValue(insurancePolicy.requireMinimumLiability().isBooleanTrue() ? insurancePolicy.minimumRequiredLiability().getValue() : null);

    }
}
