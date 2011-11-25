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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantRetriever {

    public Tenant tenant;

    public TenantScreening tenantScreening;

    public List<TenantScreening> tenantScreenings;

    // Construction:
    public TenantRetriever() {
    }

    public TenantRetriever(Key tenantId) {
        retrieve(tenantId, false);
    }

    public TenantRetriever(Key tenantId, boolean financial) {
        retrieve(tenantId, financial);
    }

    // Manipulation:
    public void retrieve(Key tenantId) {
        retrieve(tenantId, false);
    }

    public void retrieve(Key tenantId, boolean financial) {
        tenant = Persistence.service().retrieve(Tenant.class, tenantId);
        if (tenant != null) {
            EntityQueryCriteria<TenantScreening> criteria = EntityQueryCriteria.create(TenantScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
            tenantScreenings = Persistence.service().query(criteria);
            if (tenantScreenings != null && !tenantScreenings.isEmpty()) {
                tenantScreening = tenantScreenings.get(tenantScreenings.size() - 1); // use last screenings
            } else {
                tenantScreening = EntityFactory.create(TenantScreening.class);
            }

            if (financial && !tenantScreening.isEmpty()) {
                Persistence.service().retrieve(tenantScreening.documents());
                Persistence.service().retrieve(tenantScreening.incomes());
                Persistence.service().retrieve(tenantScreening.assets());
                Persistence.service().retrieve(tenantScreening.guarantors());
                Persistence.service().retrieve(tenantScreening.equifaxApproval());
            }

            Persistence.service().retrieve(tenant.emergencyContacts());
        }
    }
}
