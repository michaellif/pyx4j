/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.marshaling.LeaseMarshaller;
import com.propertyvista.oapi.v1.marshaling.TenantMarshaller;
import com.propertyvista.oapi.v1.model.LeaseIO;
import com.propertyvista.oapi.v1.model.TenantIO;
import com.propertyvista.oapi.v1.persisting.LeasePersister;

public class LeaseServiceProcessor extends AbstractProcessor {

    public LeaseServiceProcessor(ServiceType serviceType) {
        super(serviceType);
    }

    public List<LeaseIO> getLeases() {
        List<LeaseIO> leasesRS = new ArrayList<LeaseIO>();
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.asc(leaseCriteria.proto().leaseId());
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        for (Lease lease : leases) {
            Persistence.service().retrieve(lease.unit().building());
            leasesRS.add(LeaseMarshaller.getInstance().marshal(lease));
        }

        return leasesRS;
    }

    public LeaseIO getLeaseById(String leaseId) {
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().leaseId(), leaseId));
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases == null || leases.isEmpty()) {
            return null;
        }

        Lease lease = leases.get(0);
        Persistence.service().retrieve(lease.unit().building());
        return LeaseMarshaller.getInstance().marshal(lease);
    }

    public List<TenantIO> getTenants(String leaseId) {
        List<TenantIO> tenantsIO = new ArrayList<TenantIO>();
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.eq(leaseCriteria.proto().leaseId(), leaseId);
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases == null || leases.isEmpty()) {
            return Collections.emptyList();
        }

        Lease lease = leases.get(0);
        Persistence.service().retrieveMember(lease.leaseParticipants());
        for (LeaseParticipant<?> participant : lease.leaseParticipants()) {
            TenantIO tenantIO = TenantMarshaller.getInstance().marshal(participant.customer().person());
            tenantIO.leaseId = leaseId;
            tenantsIO.add(tenantIO);
        }

        return tenantsIO;
    }

    public void updateLease(LeaseIO leaseIO) {
        Lease leaseDTO = LeaseMarshaller.getInstance().unmarshal(leaseIO);

        new LeasePersister().persist(leaseDTO);

        Persistence.service().commit();
    }
}
