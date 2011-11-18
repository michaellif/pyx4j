/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;

public class TenantInLeaseRetriever extends TenantRetriever {

    public TenantInLease tenantInLease;

    // Construction:
    public TenantInLeaseRetriever() {
    }

    public TenantInLeaseRetriever(Key tenanInLeasetId) {
        retrieve(tenanInLeasetId, false);
    }

    public TenantInLeaseRetriever(Key tenanInLeasetId, boolean financial) {
        retrieve(tenanInLeasetId, financial);
    }

    // Manipulation:
    @Override
    public void retrieve(Key tenanInLeasetId) {
        retrieve(tenanInLeasetId, false);
    }

    @Override
    public void retrieve(Key tenanInLeasetId, boolean financial) {
        tenantInLease = Persistence.service().retrieve(TenantInLease.class, tenanInLeasetId);
        // TODO correct this check:
        if ((tenantInLease == null) /* || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }

        super.retrieve(tenantInLease.tenant().getPrimaryKey(), financial);
    }

    // Lease management:
    static public void UpdateLeaseTenants(Lease lease) {
        // update Tenants double links:
        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease));
        lease.tenants().clear();
        lease.tenants().addAll(Persistence.service().query(criteria));
    }
}
