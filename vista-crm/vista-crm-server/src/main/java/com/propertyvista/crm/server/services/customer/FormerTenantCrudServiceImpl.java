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

import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;

import com.propertyvista.crm.rpc.services.customer.FormerTenantCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantDTO;

public class FormerTenantCrudServiceImpl extends TenantCrudServiceImpl implements FormerTenantCrudService {

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Tenant> dbCriteria, EntityListCriteria<TenantDTO> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

        // filter out just former tenants:
        OrCriterion or = dbCriteria.or();

        or.left().in(dbCriteria.proto().lease().status(), Lease.Status.former());

        AndCriterion currentTermCriterion = new AndCriterion();
        currentTermCriterion.eq(dbCriteria.proto().leaseTermParticipants().$().leaseTermV().holder(), dbCriteria.proto().lease().currentTerm());
        // and finalized e.g. last only:
        currentTermCriterion.isCurrent(dbCriteria.proto().leaseTermParticipants().$().leaseTermV());

        or.right().notExists(dbCriteria.proto().leaseTermParticipants(), currentTermCriterion);
        or.right().ne(dbCriteria.proto().lease().status(), Lease.Status.Application);
        or.right().ne(dbCriteria.proto().lease().status(), Lease.Status.ExistingLease);
    }

}
