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
 */
package com.propertyvista.portal.server.portal.resident;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

/**
 * This may be an optimization point for future.
 */
public class ResidentPortalContext extends PortalVistaContext {

    public static void setLease(ResidentUserVisit visit, Lease lease) {
        LeaseParticipant<?> leaseParticipant = null;
        if (lease != null) {
            @SuppressWarnings("rawtypes")
            EntityQueryCriteria<LeaseParticipant> criteria = EntityQueryCriteria.create(LeaseParticipant.class);
            criteria.eq(criteria.proto().lease(), lease);
            criteria.eq(criteria.proto().customer().user(), visit.getCurrentUser());
            leaseParticipant = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        }
        visit.setLease(lease, leaseParticipant);
    }

    public static LeaseTermTenant getLeaseTermTenant() {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), getCustomerUserIdStub());
        criteria.eq(criteria.proto().leaseParticipant().lease(), getLeaseIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isCurrent(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }

    @SuppressWarnings("rawtypes")
    public static LeaseTermParticipant<?> getLeaseTermParticipant() {
        EntityQueryCriteria<LeaseTermParticipant> criteria = EntityQueryCriteria.create(LeaseTermParticipant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), getCustomerUserIdStub());
        criteria.eq(criteria.proto().leaseParticipant().lease(), getLeaseIdStub());
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isCurrent(criteria.proto().leaseTermV());
        return Persistence.service().retrieve(criteria);
    }

    public static AptUnit getUnit() {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().leases(), getLeaseIdStub());
        return Persistence.service().retrieve(criteria);
    }

}
