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
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class TenantRetriever extends CustomerRetriever {

    private LeaseTermTenant tenant;

    public TenantRetriever() {
        super();
    }

    public TenantRetriever(Key tenantId) {
        this();
        retrieve(tenantId);
    }

    public TenantRetriever(Key tenantId, boolean retrieveFinancialData) {
        super(retrieveFinancialData);
        retrieve(tenantId);
    }

    public LeaseTermTenant getTenant() {
        return tenant;
    }

    @Override
    public void retrieve(Key tenantId) {
        tenant = Persistence.service().retrieve(LeaseTermTenant.class, tenantId);
        if (tenant == null) {
            throw new SecurityViolationException("Invalid data access");
        }
        super.retrieve(tenant.leaseParticipant().customer());
        super.retrieve(tenant.screening());
    }
}
