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
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class TenantRetriever {

    TenantInLease tenantInLease;

    Tenant tenant;

    TenantScreening tenantScreening;

    void retrieve(Key tenantId) {
        tenantInLease = PersistenceServicesFactory.getPersistenceService().retrieve(TenantInLease.class, tenantId);
        if ((tenantInLease == null) || (!tenantInLease.lease().id().equals(PtAppContext.getCurrentUserLeasePrimaryKey()))) {
            throw new SecurityViolationException("Invalid data access");
        }

        tenant = EntityFactory.create(Tenant.class);
        tenant.set(tenantInLease.tenant());
        {
            EntityQueryCriteria<TenantScreening> criteria = EntityQueryCriteria.create(TenantScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenantInLease.tenant()));
            tenantScreening = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (tenantScreening != null) {
                tenantScreening.tenant().set(tenant);
            }
        }
    }
}
