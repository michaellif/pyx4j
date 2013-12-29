/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.entity.core.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.services.customer.ActiveTenantCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantDTO;

public class ActiveTenantCrudServiceImpl extends TenantCrudServiceImpl implements ActiveTenantCrudService {

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Tenant> dbCriteria, EntityListCriteria<TenantDTO> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

        // filter out just current tenants:
        dbCriteria.in(dbCriteria.proto().lease().status(), Lease.Status.current());
        dbCriteria.eq(dbCriteria.proto().leaseTermParticipants().$().leaseTermV().holder(), dbCriteria.proto().lease().currentTerm());
        // and finalized e.g. last only:
        dbCriteria.isCurrent(dbCriteria.proto().leaseTermParticipants().$().leaseTermV());

    }

}
