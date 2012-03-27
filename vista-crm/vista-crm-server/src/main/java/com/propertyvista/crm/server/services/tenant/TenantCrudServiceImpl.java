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
package com.propertyvista.crm.server.services.tenant;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.crm.rpc.services.tenant.TenantCrudService;
import com.propertyvista.crm.server.util.IdAssignmentSequenceUtil;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.TenantDTO;

public class TenantCrudServiceImpl extends AbstractCrudServiceDtoImpl<Tenant, TenantDTO> implements TenantCrudService {

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
        bind(dtoProto.lease(), dboProto._tenantInLease().$().leaseV().holder());
    }

    @Override
    protected void enhanceRetrieved(Tenant in, TenantDTO dto) {
        // load detached data:
        Persistence.service().retrieve(dto.emergencyContacts());

        // find first corresponding lease(s):
        Persistence.service().retrieveMember(in._tenantInLease());
        if (!in._tenantInLease().isEmpty()) {
            TenantInLease til = in._tenantInLease().iterator().next();
            Persistence.service().retrieve(til.leaseV());
            dto.lease().set(til.leaseV().holder());
            Persistence.service().retrieve(dto.lease(), AttachLevel.ToStringMembers);
        }
    }

    @Override
    protected void enhanceListRetrieved(Tenant in, TenantDTO dto) {
        // find first corresponding lease(s):
        Persistence.service().retrieveMember(in._tenantInLease());
        if (!in._tenantInLease().isEmpty()) {
            TenantInLease til = in._tenantInLease().iterator().next();
            Persistence.service().retrieve(til.leaseV());
            dto.lease().set(til.leaseV().holder());
            Persistence.service().retrieve(dto.lease(), AttachLevel.ToStringMembers);
        }
    }

    @Override
    protected void persist(Tenant dbo, TenantDTO in) {
        if (dbo.id().isNull() && IdAssignmentSequenceUtil.needsGeneratedId(IdTarget.tenant)) {
            dbo.tenantID().setValue(IdAssignmentSequenceUtil.getId(IdTarget.tenant));
        }

        super.persist(dbo, in);
    }
}
