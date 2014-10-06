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
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.marshaling.LeaseMarshaller;
import com.propertyvista.oapi.v1.marshaling.TenantMarshaller;
import com.propertyvista.oapi.v1.model.LeaseIO;
import com.propertyvista.oapi.v1.model.LeaseListIO;
import com.propertyvista.oapi.v1.model.TenantIO;
import com.propertyvista.oapi.v1.model.TenantListIO;
import com.propertyvista.oapi.v1.persisting.LeasePersister;
import com.propertyvista.oapi.v1.service.LeaseService;

public class LeaseServiceProcessor extends AbstractProcessor {

    public LeaseServiceProcessor(ServiceType serviceType) {
        super(LeaseService.class, serviceType);
    }

    public LeaseListIO getLeases() {
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.asc(leaseCriteria.proto().leaseId());
        return LeaseMarshaller.getInstance().marshalCollection(LeaseListIO.class, Persistence.service().query(leaseCriteria));
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
        return LeaseMarshaller.getInstance().marshalItem(lease);
    }

    public TenantListIO getTenants(String leaseId) {
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.eq(leaseCriteria.proto().leaseId(), leaseId);
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases == null || leases.isEmpty()) {
            return new TenantListIO();
        }

        Lease lease = leases.get(0);
        // pre-processing
        List<Person> tenants = new ArrayList<>();
        Persistence.service().retrieveMember(lease.leaseParticipants());
        for (LeaseParticipant<?> participant : lease.leaseParticipants()) {
            tenants.add(participant.customer().person());
        }
        TenantListIO result = TenantMarshaller.getInstance().marshalCollection(TenantListIO.class, tenants);
        // post-processing
        for (TenantIO tenantIO : result.getList()) {
            tenantIO.leaseId = leaseId;
        }

        return result;
    }

    public void updateLease(LeaseIO leaseIO) {
        Lease leaseDTO = LeaseMarshaller.getInstance().unmarshalItem(leaseIO);

        new LeasePersister().persist(leaseDTO);

        Persistence.service().commit();
    }
}
