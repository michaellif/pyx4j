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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.TenantCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.server.common.util.TenantRetriever;

public class TenantCrudServiceImpl extends GenericCrudServiceDtoImpl<Tenant, TenantDTO> implements TenantCrudService {

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceDTO(Tenant in, TenantDTO dto, boolean fromList) {

        if (!fromList) {
            // load detached data:
            Persistence.service().retrieve(dto.emergencyContacts());
        }

        TenantRetriever tr = new TenantRetriever(in, true);
        if (tr.tenantScreening.isEmpty() && !tr.tenantScreening.incomes().isEmpty()) {
            dto.incomeSource().setValue(tr.tenantScreening.incomes().get(0).incomeSource().getValue());
        }

        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), in));
        TenantInLease tenantInLease = Persistence.service().retrieve(criteria);
        dto.role().setValue(tenantInLease.role().getValue());
    }
}
