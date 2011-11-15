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

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.lease.Lease;

public class TenantRetriever {

    public Tenant tenant;

    public TenantScreening tenantScreening;

    public TenantInLease tenantInLease;

    // Construction:
    public TenantRetriever() {
    }

    public TenantRetriever(Key tenanInLeasetId) {
        retrieve(tenanInLeasetId, false);
    }

    public TenantRetriever(Key tenanInLeasetId, boolean financial) {
        retrieve(tenanInLeasetId, financial);
    }

    // Manipulation:
    public void retrieve(Key tenanInLeasetId) {
        retrieve(tenanInLeasetId, false);
    }

    public void retrieve(Key tenanInLeasetId, boolean financial) {
        tenantInLease = Persistence.service().retrieve(TenantInLease.class, tenanInLeasetId);
        // TODO correct this check:
        if ((tenantInLease == null) /* || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }

        EntityQueryCriteria<TenantScreening> criteria = EntityQueryCriteria.create(TenantScreening.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenantInLease.tenant()));
        tenantScreening = Persistence.service().retrieve(criteria);

        if (financial) {
            Persistence.service().retrieve(tenantScreening.documents());
            Persistence.service().retrieve(tenantScreening.incomes());
            Persistence.service().retrieve(tenantScreening.assets());
            Persistence.service().retrieve(tenantScreening.guarantors());
        }

        tenant = Persistence.service().retrieve(Tenant.class, tenantInLease.tenant().getPrimaryKey());
        Persistence.service().retrieve(tenant.emergencyContacts());
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
