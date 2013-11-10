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
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;

public class SelectTenantListServiceImpl extends AbstractListServiceImpl<Tenant> implements SelectTenantListService {

    public SelectTenantListServiceImpl() {
        super(Tenant.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Tenant> dbCriteria, EntityListCriteria<Tenant> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

        // filter out just current tenants:
        dbCriteria.in(dbCriteria.proto().lease().status(), Lease.Status.current());
        dbCriteria.eq(dbCriteria.proto().leaseTermParticipants().$().leaseTermV().holder(), dbCriteria.proto().lease().currentTerm());
        // and finalized e.g. last only:
        dbCriteria.isCurrent(dbCriteria.proto().leaseTermParticipants().$().leaseTermV());

    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, Tenant dto) {
        Persistence.service().retrieve(dto.lease(), AttachLevel.ToStringMembers, false);
    }
}
