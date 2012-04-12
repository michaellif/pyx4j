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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.customer.TenantInLeaseCrudService;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class TenantInLeaseCrudServiceImpl extends AbstractCrudServiceDtoImpl<Tenant, TenantInLeaseDTO> implements TenantInLeaseCrudService {

    public TenantInLeaseCrudServiceImpl() {
        super(Tenant.class, TenantInLeaseDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceListRetrieved(Tenant in, TenantInLeaseDTO dto) {
        TenantInLeaseRetriever tr = new TenantInLeaseRetriever(in.getPrimaryKey(), true);
        if (!tr.tenantScreening.isEmpty()) {
            if (!tr.tenantScreening.incomes().isEmpty()) {
                dto.incomeSource().setValue(tr.tenantScreening.incomes().get(0).incomeSource().getValue());
            }
            dto.equifaxApproval().set(tr.tenantScreening.equifaxApproval());
        }
        Persistence.service().retrieve(dto.application());
    }
}
