/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.oapi.binder;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.tenant.lease.Lease;

public class LeasePersister extends AbstractPersister<Lease, Lease> {

    public LeasePersister() {
        super(Lease.class, Lease.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public Lease retrieve(Lease dto) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().leaseId(), dto.leaseId());
        return Persistence.service().retrieve(criteria);
    }
}
