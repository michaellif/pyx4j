/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.operations.rpc.dto.TenantSureDTO;
import com.propertyvista.operations.rpc.services.TenantSureCrudService;

public class TenantSureCrudServiceImpl extends AbstractCrudServiceDtoImpl<TenantSureSubscribers, TenantSureDTO> implements TenantSureCrudService {

    public TenantSureCrudServiceImpl() {
        super(TenantSureSubscribers.class, TenantSureDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(TenantSureSubscribers bo, TenantSureDTO dto, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(dto.pmc());
        fillPolicy(dto);

        Persistence.ensureRetrieve(dto.policy().client().tenant().lease().unit().building(), AttachLevel.ToStringMembers);
        dto.propertyCode().setValue(dto.policy().client().tenant().lease().unit().building().propertyCode().getValue());
    }

    @Override
    protected void enhanceListRetrieved(TenantSureSubscribers entity, TenantSureDTO dto) {
        Persistence.service().retrieve(dto.pmc());
        fillPolicy(dto);

        Persistence.ensureRetrieve(dto.policy().client().tenant().lease().unit().building(), AttachLevel.ToStringMembers);
        dto.propertyCode().setValue(dto.policy().client().tenant().lease().unit().building().propertyCode().getValue());
    }

    private void fillPolicy(TenantSureDTO dto) {
        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.eq(criteria.proto().certificate().insuranceCertificateNumber(), dto.certificateNumber());
        dto.policy().set(Persistence.service().retrieve(criteria));
        assert (!dto.policy().isNull());
    }
}
