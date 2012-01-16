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

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;

public class TenantInLeaseRetriever extends TenantRetriever {

    public TenantInLease tenantInLease;

    // Construction:
    public TenantInLeaseRetriever() {
    }

    public TenantInLeaseRetriever(Key tenanInLeasetId) {
        super(false);
        retrieve(tenanInLeasetId);
    }

    public TenantInLeaseRetriever(Key tenanInLeasetId, boolean financial) {
        super(financial);
        retrieve(tenanInLeasetId);
    }

    // Manipulation:
    @Override
    public void retrieve(Key tenanInLeasetId) {
        tenantInLease = Persistence.service().retrieve(TenantInLease.class, tenanInLeasetId);
        // TODO correct this check:
        if ((tenantInLease == null) /* || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }

        super.retrieve(tenantInLease.tenant().getPrimaryKey());
    }

    // Lease management:
    static public void UpdateLeaseTenants(Lease lease) {
        // update Tenants double links:
        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease));
        criteria.asc(criteria.proto().orderInLease());
        List<TenantInLease> tenants = Persistence.service().query(criteria);

        // here: clear the current list, add queried tenants placing Applicant first:  
        lease.tenants().clear();
        for (TenantInLease til : tenants) {
            if (Role.Applicant == til.role().getValue()) {
                lease.tenants().add(til);
                tenants.remove(til);
                break;
            }
        }
        lease.tenants().addAll(tenants);
    }
}
