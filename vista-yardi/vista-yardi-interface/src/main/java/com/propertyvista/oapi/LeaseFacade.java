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
package com.propertyvista.oapi;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.oapi.marshaling.LeaseMarshaller;
import com.propertyvista.oapi.model.LeaseRS;

public class LeaseFacade {

    private static final IEntityPersistenceService service;

    static {
        service = Persistence.service();
    }

    public static List<LeaseRS> getLeases() {
        List<LeaseRS> leasesRS = new ArrayList<LeaseRS>();
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.asc(leaseCriteria.proto().leaseId());
        List<Lease> leases = service.query(leaseCriteria);
        for (Lease lease : leases) {
            service.retrieve(lease.unit().building());
            LeaseMarshaller marshaller = new LeaseMarshaller();
            leasesRS.add(marshaller.unmarshal(lease));
        }

        return leasesRS;
    }

    public static LeaseRS getLeaseById(String leaseId) {
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().leaseId(), leaseId));
        List<Lease> leases = service.query(leaseCriteria);

        Lease lease = leases.get(0);
        service.retrieve(lease.unit().building());
        LeaseMarshaller marshaller = new LeaseMarshaller();
        return marshaller.unmarshal(lease);
    }
}
