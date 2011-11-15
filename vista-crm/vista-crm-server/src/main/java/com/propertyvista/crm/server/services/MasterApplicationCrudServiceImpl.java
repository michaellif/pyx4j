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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.MasterApplicationCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantRetriever;

public class MasterApplicationCrudServiceImpl extends GenericCrudServiceDtoImpl<MasterApplication, MasterApplicationDTO> implements
        MasterApplicationCrudService {

    public MasterApplicationCrudServiceImpl() {
        super(MasterApplication.class, MasterApplicationDTO.class);
    }

    @Override
    protected void enhanceDTO(MasterApplication in, MasterApplicationDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        Persistence.service().retrieve(dto.lease());
        Persistence.service().retrieve(dto.lease().tenants());

        dto.mainApplicant().set(dto.lease().tenants().get(0));

        TenantRetriever.UpdateLeaseTenants(dto.lease());

        for (TenantInLease tenantInLease : dto.lease().tenants()) {
            Persistence.service().retrieve(tenantInLease);

            TenantRetriever tr = new TenantRetriever(tenantInLease.getPrimaryKey(), true);

            dto.tenantsWithInfo().add(createTenantInfoDTO(tr));
            dto.tenantFinancials().add(createTenantFinancialDTO(tr));
        }
    }

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        tfDTO.person().set(tr.tenant.person());
        return tfDTO;
    }
}
