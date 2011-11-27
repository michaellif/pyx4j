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

    private final boolean financial;

    // Construction:
    public TenantRetriever() {
        this.financial = false;
    }

    public TenantRetriever(boolean financial) {
        this.financial = financial;
    }

    public TenantRetriever(Key tenantId) {
        this(tenantId, false);
    }

    public TenantRetriever(Key tenantId, boolean financial) {
        this(financial);
        retrieve(tenantId);
    }

    // Manipulation:
    public void retrieve(Key tenantId) {
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

            if (!tenantScreening.isEmpty()) {
                Persistence.service().retrieve(tenantScreening.documents());
                if (financial) {
                    Persistence.service().retrieve(tenantScreening.incomes());
                    Persistence.service().retrieve(tenantScreening.assets());
                    Persistence.service().retrieve(tenantScreening.guarantors());
                    Persistence.service().retrieve(tenantScreening.equifaxApproval());
                }
            } else {
                // newly created - set belonging to tenant:
                tenantScreening.tenant().set(tenant);
            }

            Persistence.service().retrieve(tenant.emergencyContacts());
        }
    }

    public void saveTenant() {
        Persistence.service().merge(tenant);
    }

    public void saveScreening() {
        Persistence.service().merge(tenantScreening);
        // save detached entities:
        Persistence.service().merge(tenantScreening.documents());
        if (financial) {
            Persistence.service().merge(tenantScreening.incomes());
            Persistence.service().merge(tenantScreening.assets());
            Persistence.service().merge(tenantScreening.guarantors());
        }
    }
}
