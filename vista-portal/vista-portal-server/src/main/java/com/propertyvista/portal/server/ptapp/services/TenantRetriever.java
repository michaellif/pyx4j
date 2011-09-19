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
package com.propertyvista.portal.server.ptapp.services;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class TenantRetriever {

    Tenant tenant;

    TenantScreening tenantScreening;

    TenantInLease tenantInLease;

    public TenantRetriever() {
    }

    public TenantRetriever(Key tenantId) {
        retrieve(tenantId, false);
    }

    public TenantRetriever(Key tenantId, boolean financial) {
        retrieve(tenantId, financial);
    }

    void retrieve(Key tenantId) {
        retrieve(tenantId, false);
    }

    void retrieve(Key tenantId, boolean financial) {
        tenantInLease = Persistence.service().retrieve(TenantInLease.class, tenantId);
        if ((tenantInLease == null) || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey()))) {
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
}
