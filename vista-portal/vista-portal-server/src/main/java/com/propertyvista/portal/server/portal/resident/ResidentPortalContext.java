/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

/**
 * This may be an optimization point for future.
 */
public class ResidentPortalContext extends PortalVistaContext {

    private final static String slectedLeaseAtt = "selected-lease";

    public static void setLease(Lease lease) {
        Context.getVisit().setAttribute(slectedLeaseAtt, lease.getPrimaryKey());
    }

    public static Lease getLeaseIdStub() {
        return EntityFactory.createIdentityStub(Lease.class, (Key) Context.getVisit().getAttribute(slectedLeaseAtt));
    }

    public static Lease getLease() {
        return Persistence.service().retrieve(Lease.class, getLeaseIdStub().getPrimaryKey());
    }

    public static Tenant getTenant() {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease(), getLeaseIdStub());
        criteria.eq(criteria.proto().customer().user(), getCustomerUserIdStub());
        return Persistence.service().retrieve(criteria);
    }

    public static LeaseTermTenant getTenantInLease() {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), getCustomerUserIdStub());
        criteria.eq(criteria.proto().leaseParticipant().lease(), getLeaseIdStub());
        criteria.isCurrent(criteria.proto().leaseTermV());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        return Persistence.service().retrieve(criteria);
    }

    public static AptUnit getUnit() {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto()._Leases(), getLeaseIdStub());
        return Persistence.service().retrieve(criteria);
    }

}
