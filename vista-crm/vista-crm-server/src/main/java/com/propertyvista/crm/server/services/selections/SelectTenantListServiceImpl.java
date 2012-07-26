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
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;

public class SelectTenantListServiceImpl extends AbstractListServiceImpl<Tenant> implements SelectTenantListService {

    public SelectTenantListServiceImpl() {
        super(Tenant.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.id(), dboProto.id());
        bindCompleateDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Tenant> dbCriteria, EntityListCriteria<Tenant> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

        // filter out just current tenants:
        Tenant proto = EntityFactory.getEntityPrototype(Tenant.class);
        dbCriteria.add(PropertyCriterion.in(proto.leaseV().holder().status(), Lease.Status.current()));
        // and current lease version only:
        dbCriteria.add(PropertyCriterion.isNotNull(proto.leaseV().fromDate()));
        dbCriteria.add(PropertyCriterion.isNull(proto.leaseV().toDate()));
    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, Tenant dto) {
        Persistence.service().retrieve(dto.leaseV());
        Persistence.service().retrieve(dto.leaseV().holder(), AttachLevel.ToStringMembers);
    }
}
